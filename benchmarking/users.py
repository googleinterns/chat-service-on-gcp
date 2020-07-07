"""
This module contains definitions for User, MultiUser and SignUpUser classes.
MultiUser and SignUpUser classes are derived from User class.
MultiUser and SignUpUser classes override User's simulate method,
that sends requests and returns a list of tuple (response object, current_total_qps).
"""
from datetime import datetime

import requests

import apis
import random
import time


def get_random_username():
    """
    Returns a random username from the Username column of user_details data-frame.
    """
    user_number = random.randint(0, len(MultiUser.user_details.index) - 1)
    return MultiUser.user_details["Username"][user_number]


class User:
    """
    A base class having the following class variables:
        1. end_time: Time till which simulate method should run.
        2. interval: Number of seconds between every subsequent request sent in simulate method.
        3. total_qps: Total number of SignUpUser and MultiUser instances that have been created.
                    Since each instance is sending request parallelly, this is equal to the total QPS.
    """
    end_time = None
    interval = 2
    total_qps = 0

    def __init__(self):
        User.total_qps += 1

    def simulate(self):
        pass


class MultiUser(User):
    """
    A class derived from User that overrides simulate method
    and sends requests of following type:
        1. login
        2. viewUser
        3. listChats
        4. listMessages
        5. createChat
        6. createMessage
    Class Variables:
        1. distribution: probability distribution of each type of request.
        2. user_details: pandas data-frame containing details
                        like UserID, Username, Password etc of all the users.
        3. chat_details: pandas data-frame containing UserIDs and ChatID for all users.
        4. api_names: list of all API names that the simulate method sends requests for.
    Attributes:
        1. user_id: UserID of the user.
        2. username: Username of the user.
        3. password: Password of the user.
        4. chat_id_list: list containing all the ChatIDs belonging to this user.
    """
    distribution = []
    user_details = None
    chat_details = None
    api_names = ["login", "viewUser", "listChats", "listMessages", "createChat", "createMessage"]

    def __init__(self, user_id, username, password, chat_id_list):
        super().__init__()
        self.user_id = user_id
        self.username = username
        self.password = password
        self.chat_id_list = chat_id_list

    def simulate(self):
        """
        Generates a random number from 1 to 6 according to the probability distribution.
        Based on the generated number, sends request to appropriate API.
        Stores the response and current total qps.
        Waits for interval number of seconds.
        Repeats the above steps until end_time.
        Returns a list of tuple (api_name, response object, current_total_qps).
        """
        session = requests.Session()
        response_list = []
        while datetime.now() < User.end_time:
            api_number = random.choices(range(1, 7), MultiUser.distribution)[0]
            if api_number == 1:
                response = apis.login_request(session, self.username, self.password)
            elif api_number == 2:
                response = apis.view_user_request(session, get_random_username())
            elif api_number == 3:
                response = apis.list_chats_request(session, self.user_id)
            elif api_number == 4:
                index = random.randint(0, 1)
                response = apis.list_messages_request(session, self.user_id, self.chat_id_list[index])
            elif api_number == 5:
                response = apis.create_chat_request(session, self.user_id, get_random_username())
            else:
                index = random.randint(0, 1)
                response = apis.create_message_request(session, self.user_id, self.chat_id_list[index])
            response_list.append((MultiUser.api_names[api_number - 1], response, User.total_qps))
            time.sleep(User.interval)
        return response_list


class SignupUser(User):

    def simulate(self):
        """
        Sends signup request and waits for interval number of seconds.
        Repeats until end_time.
        Returns a list of tuple (api_name, response object, current_total_qps).
        """
        session = requests.Session()
        response_list = []
        while datetime.now() < User.end_time:
            response_list.append(("signup", apis.signup_request(session), User.total_qps))
            time.sleep(User.interval)
        return response_list
