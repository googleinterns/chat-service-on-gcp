"""
This module takes following input arguments in this order-
    1. minimum qps
    2. maximum qps
    3. test duration
    4. weight of login requests
    5. weight of viewUser requests
    6. weight of getUsersByMobileNumber requests
    7. weight of listChats requests
    8. weight of listMessages requests
    9. weight of createChat requests
    10. weight of createMessage requests
    11. weight of signup requests

It starts a warm-up period by instantiating LoadTest class of load_test module.
After warm-up it starts the load testing by instantiating LoadTest class using the input arguments.
It collects the results of load test and sends to write_output module to write all response times in a CSV file.
Finally, it instantiates ProcessOutput class of process_output module
and the output is processed and the final latencies are written to a CSV file.
"""
import sys
import configparser
import pandas as pd
from load_test import LoadTest
from write_output import write_output
from process_output import ProcessOutput

config = configparser.ConfigParser()
config.read('config.ini')
file_names = config['File Names']
load_test_constants = config['Load Test']


class ProcessInput:
    """
    A class for processing input command line arguments.

    Attributes:
        input_args : All command line arguments except the default file name
        total_weight : Sum of weights of all APIs
    """

    def __init__(self, input_args):
        self.input_args = list(map(int, input_args[1:]))  # first argument is by default file name
        self.total_weight = sum(self.input_args[3:])

    def get_min_qps(self):
        return self.input_args[0]

    def get_max_qps(self):
        return self.input_args[1]

    def get_test_duration(self):
        return self.input_args[2]

    def get_all_api_weights(self):
        return self.input_args[3:]

    def get_api_weights_dict(self):
        """
        Returns a dictionary with API names as keys and weight proportion of APIs as values.
        """
        api_names = ["login", "viewUser", "getUsersByMobileNumber", "listChats", "listMessages", "createChat",
                     "createMessage", "signup"]
        api_weights_dict = {api_names[i]: self.input_args[i + 3] / self.total_weight for i in range(len(api_names))}
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


def warm_up():
    load_test_warm_up = LoadTest(
        int(load_test_constants['WARM_UP_MIN_QPS']),
        int(load_test_constants['WARM_UP_MAX_QPS']),
        int(load_test_constants['WARM_UP_DURATION_MINUTES']),
        process_input.get_all_api_weights())
    load_test_warm_up.run()


def load_test():
    actual_load_test = LoadTest(
        process_input.get_min_qps(),
        process_input.get_max_qps(),
        process_input.get_test_duration(),
        process_input.get_all_api_weights()
    )
    return actual_load_test.run(), actual_load_test.get_all_qps()


process_input = ProcessInput(sys.argv)
LoadTest.all_users = get_all_users()
LoadTest.all_chats = get_all_chats()

warm_up()
load_test_results, all_qps = load_test()

write_output(load_test_results, process_input.get_api_weights_dict())

output_processor = ProcessOutput(all_qps)
output_processor.process_output()
