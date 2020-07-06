"""
This module takes following input arguments in this order-
1. minimum qps
2. maximum qps
3. test duration
4. weight of login requests
5. weight of viewUser requests
6. weight of listChats requests
7. weight of listMessages requests
8. weight of createChat requests
9. weight of createMessage requests
10. weight of signup requests
According to these values, it instantiates the required number of user instances.
It collects the list of tuples returned by the simulate method of users and
sends this to write_output module.
"""
import users
from write_output import write_output
import sys
import configparser
import pandas as pd
import numpy as np
import concurrent.futures
import time
from datetime import datetime, timedelta

config = configparser.ConfigParser()
config.read('config.ini')
file_names = config['File Names']


class ProcessInput:
    """
    A class for processing input command line arguments.

    Attributes:
        input_args : All command line arguments except the default file name
        input_args_len : Number of given command line arguments
        total_weight : Sum of weights of all APIs
        total_weight_without_signup : Sum of weights of all APIs excluding the signup API.
        total_qps_increase_rate : The number of qps that should be increased per minute.
    """
    def __init__(self, input_args):
        self.input_args = list(map(int, input_args[1:]))  # first argument is by default file name
        self.input_args_len = len(self.input_args)
        self.total_weight = sum(self.input_args[3:])
        self.total_weight_without_signup = self.total_weight - self.input_args[-1]
        self.total_qps_increase_rate = (self.input_args[1] - self.input_args[0]) / self.input_args[2]

    def get_min_qps(self):
        return self.input_args[0]

    def get_max_qps(self):
        return self.input_args[1]

    def get_test_duration(self):
        return self.input_args[2]

    def get_all_api_weights(self):
        return self.input_args[3:]

    def get_total_weight(self):
        return self.total_weight

    def get_total_weight_without_signup(self):
        return self.total_weight_without_signup

    def get_signup_request_probability(self):
        """
        Returns the probability of signup requests among all requests.
        """
        return self.input_args[-1] / self.total_weight

    def get_initial_signup_instances_count(self):
        """
        Returns the number of SignUpUser instances
        that should be created at the start of load test.
        """
        return round(self.input_args[0] * self.get_signup_request_probability())

    def get_initial_multi_instances_count(self):
        """
        Returns the number of MultiUser instances
        that should be created at the start of load test.
        """
        return round(self.input_args[0] * (1 - self.get_signup_request_probability()))

    def get_signup_qps_increase_rate(self):
        """
        Returns the number of SignUpUser instances that should be added after every minute.
        """
        return round(self.total_qps_increase_rate * self.get_signup_request_probability())

    def get_multi_qps_increase_rate(self):
        """
        Returns the number of MultiUser instances that should be added after every minute.
        """
        return round(self.total_qps_increase_rate * (1 - self.get_signup_request_probability()))

    def get_api_distribution_without_signup(self):
        """
        Returns a list of floats where ith element corresponds
        to the probability of ith API among all APIs excluding the SignUp API.
        API ordering is as follows:
            1. login
            2. viewUser
            3. listChats
            4. listMessages
            5. createChat
            6. createMessage
        """
        return (np.array(self.input_args[3:self.input_args_len - 1])/self.total_weight_without_signup).tolist()

    def get_api_weights_dict(self):
        """
        Returns a dictionary with API names as keys and weight proportion of APIs as values.
        """
        api_names = ["login", "viewUser", "listChats", "listMessages", "createChat", "createMessage", "signup"]
        api_weights_dict = {api_names[i]: self.input_args[i + 3]/self.total_weight for i in range(len(api_names))}
        return api_weights_dict


def get_all_users():
    """
    Returns a pandas data-frame by reading from the users.csv file.
    """
    return pd.read_csv(file_names['USERS'])


def get_all_chats():
    """
    Returns a pandas data-frame by reading from the chats.csv file.
    """
    return pd.read_csv(file_names['CHATS'])


def get_chats_for_user(user_id):
    """
    Returns a list of ChatIDs for a given UserID.
    """
    rows = all_chats[(all_chats['UserID1'] == user_id) | (all_chats['UserID2'] == user_id)]
    return rows['ChatID'].to_list()


def get_signup_user_instances(count):
    """
    Returns a list of required number of SignUpUser instances.
    """
    return [users.SignupUser() for _ in range(count)]


def get_multi_user_instances(count):
    """
    Returns a list of required number of MultiUser instances.
    Maintains a function/static variable user_index to instantiate MultiUsers with unique UserIDs.
    """
    instances = []
    for _ in range(count):
        user_id = all_users['UserID'][get_multi_user_instances.user_index]
        username = all_users['Username'][get_multi_user_instances.user_index]
        password = all_users['Password'][get_multi_user_instances.user_index]
        chat_list = get_chats_for_user(user_id)
        instances.append(users.MultiUser(user_id, username, password, chat_list))
        get_multi_user_instances.user_index += 1
    return instances


process_input = ProcessInput(sys.argv)
min_qps = process_input.get_min_qps()
max_qps = process_input.get_max_qps()
duration = process_input.get_test_duration()
initial_signup_instances_count = process_input.get_initial_signup_instances_count()
initial_multi_instances_count = process_input.get_initial_multi_instances_count()
signup_qps_increase_rate = process_input.get_signup_qps_increase_rate()
multi_qps_increase_rate = process_input.get_multi_qps_increase_rate()

all_users = get_all_users()
all_chats = get_all_chats()
get_multi_user_instances.user_index = 0
# end_time is 1 minute more than the duration for getting responses of requests sent in the last minute
end_time = datetime.now() + timedelta(minutes=duration+1)
users.User.end_time = end_time
users.MultiUser.user_details = all_users
users.MultiUser.chat_details = all_chats
users.MultiUser.distribution = process_input.get_api_distribution_without_signup()
results = []
total_iterations = duration
completed_iterations = 0
with concurrent.futures.ThreadPoolExecutor(max_workers=max_qps) as executor:
    signup_instances = get_signup_user_instances(initial_signup_instances_count)
    multi_instances = get_multi_user_instances(initial_multi_instances_count)
    futures = {executor.submit(_.simulate) for _ in signup_instances}
    futures.update({executor.submit(_.simulate) for _ in multi_instances})
    while completed_iterations < total_iterations:
        time.sleep(60)
        signup_instances = get_signup_user_instances(signup_qps_increase_rate)
        multi_instances = get_multi_user_instances(multi_qps_increase_rate)
        futures.update({executor.submit(_.simulate) for _ in signup_instances})
        futures.update({executor.submit(_.simulate) for _ in multi_instances})
        completed_iterations += 1
    for future in concurrent.futures.as_completed(futures):
        results.extend(future.result())
write_output(results, process_input.get_api_weights_dict())
