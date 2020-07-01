"""
This module defines functions for sending requests of following types:
    1. login
    2. viewUser
    3. listChats
    4. listMessages
    5. createChat
    6. createMessage
    7. signup
"""
import requests
import random_string
import configparser
import random

config = configparser.ConfigParser()
config.read('config.ini')
user_constants = config['User']
url_constants = config['Url']
file_names = config['File Names']

BASE_URL = url_constants['BASE_URL']
SIGNUP_URL = url_constants['SIGNUP_URL']

USERNAME_MIN_LENGTH = int(user_constants['USERNAME_MIN_LENGTH'])
USERNAME_MAX_LENGTH = int(user_constants['USERNAME_MAX_LENGTH'])
PASSWORD_LENGTH = int(user_constants['PASSWORD_LENGTH'])
MOBILE_NUMBER_LENGTH = int(user_constants['MOBILE_NUMBER_LENGTH'])
EMAIL_DOMAIN = user_constants['EMAIL_DOMAIN']


def login_request(username, password):
    return requests.post(BASE_URL + 'login',
                         json={
                             "Username": username,
                             "Password": password,
                         })


def view_user_request(username):
    return requests.get(BASE_URL + 'viewUser',
                        params={
                            "username": username,
                        })


def list_chats_request(user_id):
    return requests.get(BASE_URL + 'users/' + str(user_id) + '/chats')


def list_messages_request(user_id, chat_id):
    return requests.get(
        BASE_URL +
        'users/' +
        str(user_id) +
        '/chats/' +
        str(chat_id) +
        '/messages'
    )


def create_chat_request(user_id, username):
    return requests.post(BASE_URL + 'users/' + str(user_id) + '/chats',
                         json={"username": username})


def create_message_request(user_id, chat_id):
    random_number = random.randint(0, 1)  # for choosing between text and content rich message
    if random_number == 0:
        files = {
            "textContent": "Hello friend",
        }
    else:
        files = {
            "file": open('message_file.txt', 'r')
        }
    return requests.post(BASE_URL + 'users/' + str(user_id) + '/chats/' + str(chat_id) + '/messages',
                         files=files)


def signup_request():
    username = random_string.alpha_numeric_variable_length(USERNAME_MIN_LENGTH, USERNAME_MAX_LENGTH)
    email = username + EMAIL_DOMAIN
    mobile_no = random_string.numeric_fixed_length(MOBILE_NUMBER_LENGTH)
    password = random_string.alpha_numeric_fixed_length(PASSWORD_LENGTH)
    return requests.post(SIGNUP_URL,
                         json={
                             "Username": username,
                             "EmailID": email,
                             "MobileNo": mobile_no,
                             "Password": password,
                         })
