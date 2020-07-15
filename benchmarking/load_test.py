"""
This module contains the LoadTest class that takes following inputs:
    1. min qps
    2. max qps
    3. duration
    4. api weights
It starts the load test according to the input parameters by instantiating the required number of
SignupUser and MultiUser classes of user module. It collects and returns the results returned by all the user instances.
"""
import numpy as np
from datetime import datetime, timedelta
import time
import concurrent.futures
import users


class LoadTest:
    """
    A class for running the load test.

    Attributes:
        min_qps: qps at which the load test should start
        max_qps: qps at which the load test should end
        duration: time in minutes for which load test should run
        api_weights: weight-age of all APIs in the requests that will be sent
        total_api_weight: sum of weight-age of all APIs
        total_api_weight_without_signup: sum of weight-age of all APIs except signup API
        total_qps_increase_rate: number of qps that should be increased per minute during load test
        all_users_index: index of all_users data-frame
        till where the user details have been used for instantiating MultiUser class
    """

    all_users = None
    all_chats = None

    def __init__(self, min_qps, max_qps, duration, api_weights):
        self.min_qps = min_qps
        self.max_qps = max_qps
        self.duration = duration
        self.api_weights = api_weights
        self.total_api_weight = sum(api_weights)
        self.total_api_weight_without_signup = self.total_api_weight - self.api_weights[-1]
        self.total_qps_increase_rate = (max_qps - min_qps) / duration
        self.all_users_index = 0

    def get_signup_request_probability(self):
        """
        Returns the probability of signup requests among all requests.
        """
        return self.api_weights[-1] / self.total_api_weight

    def get_initial_signup_instances_count(self):
        """
        Returns the number of SignUpUser instances
        that should be created at the start of load test.
        """
        return round(self.min_qps * self.get_signup_request_probability())

    def get_initial_multi_instances_count(self):
        """
        Returns the number of MultiUser instances
        that should be created at the start of load test.
        """
        return round(self.min_qps * (1 - self.get_signup_request_probability()))

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
            3. getUsersByMobileNumber
            4. listChats
            5. listMessages
            6. createChat
            7. createMessage
        """
        return (np.array(self.api_weights[:len(self.api_weights) - 1]) / self.total_api_weight_without_signup).tolist()

    def get_all_qps(self):
        """
        Returns a list of total qps values for every minute starting from min qps to max qps.
        """
        return [self.min_qps + i * self.total_qps_increase_rate for i in range(self.duration + 1)]

    def run(self):
        """
        Starts the load test by instantiating required number of SignupUser and MultiUser instances.
        It increases the number of user instances according to the total_qps_increase_rate after every minute.
        It collects and returns the results returned by all user instances after load test is completed.
        """
        initial_signup_instances_count = self.get_initial_signup_instances_count()
        initial_multi_instances_count = self.get_initial_multi_instances_count()
        signup_qps_increase_rate = self.get_signup_qps_increase_rate()
        multi_qps_increase_rate = self.get_multi_qps_increase_rate()

        # end_time is 1 minute more than the duration for getting responses of requests sent in the last minute
        end_time = datetime.now() + timedelta(minutes=self.duration + 1)
        users.User.end_time = end_time
        users.MultiUser.user_details = LoadTest.all_users
        users.MultiUser.chat_details = LoadTest.all_chats
        users.MultiUser.distribution = self.get_api_distribution_without_signup()
        users.User.total_qps = 0

        results = []
        total_iterations = self.duration
        completed_iterations = 0

        with concurrent.futures.ThreadPoolExecutor(max_workers=self.max_qps) as executor:
            signup_instances = get_signup_user_instances(initial_signup_instances_count)
            multi_instances = get_multi_user_instances(initial_multi_instances_count, self.all_users_index)
            self.all_users_index += initial_multi_instances_count
            futures = {executor.submit(_.simulate) for _ in signup_instances}
            futures.update({executor.submit(_.simulate) for _ in multi_instances})
            while completed_iterations < total_iterations:
                time.sleep(60)
                signup_instances = get_signup_user_instances(signup_qps_increase_rate)
                multi_instances = get_multi_user_instances(multi_qps_increase_rate, self.all_users_index)
                self.all_users_index += multi_qps_increase_rate
                futures.update({executor.submit(_.simulate) for _ in signup_instances})
                futures.update({executor.submit(_.simulate) for _ in multi_instances})
                completed_iterations += 1
            for future in concurrent.futures.as_completed(futures):
                results.extend(future.result())
        return results


def get_chats_for_user(user_id):
    """
    Returns a list of ChatIDs for a given UserID.
    """
    rows = LoadTest.all_chats[(LoadTest.all_chats['UserID1'] == user_id) | (LoadTest.all_chats['UserID2'] == user_id)]
    return rows['ChatID'].to_list()


def get_signup_user_instances(count):
    """
    Returns a list of required number of SignUpUser instances.
    """
    return [users.SignupUser() for _ in range(count)]


def get_multi_user_instances(count, all_users_index):
    """
    Returns a list of required number of MultiUser instances.
    Maintains a function/static variable user_index to instantiate MultiUsers with unique UserIDs.
    """
    instances = []
    for _ in range(count):
        user_id = LoadTest.all_users['UserID'][all_users_index]
        username = LoadTest.all_users['Username'][all_users_index]
        password = LoadTest.all_users['Password'][all_users_index]
        chat_list = get_chats_for_user(user_id)
        instances.append(users.MultiUser(user_id, username, password, chat_list))
        all_users_index += 1
    return instances
