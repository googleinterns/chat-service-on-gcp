"""
generates dummy users by sending multiple concurrent requests to signup API
stores the user details like UserID, Username, EmailID, MobileNo, Password,
Response Time and Response Status Code to a CSV file
"""

import concurrent.futures
import random_string
import requests
import csv
import configparser

config = configparser.ConfigParser()
config.read('config.ini')
user_constants = config['User']
url_constants = config['Url']
file_names = config['File Names']

USERNAME_MIN_LENGTH = int(user_constants['USERNAME_MIN_LENGTH'])
USERNAME_MAX_LENGTH = int(user_constants['USERNAME_MAX_LENGTH'])
PASSWORD_LENGTH = int(user_constants['PASSWORD_LENGTH'])
MOBILE_NUMBER_LENGTH = int(user_constants['MOBILE_NUMBER_LENGTH'])
EMAIL_DOMAIN = user_constants['EMAIL_DOMAIN']
OUTPUT_FILE_NAME = file_names['USERS']
SIGNUP_URL = url_constants['SIGNUP_URL']

CSV_COLUMN_NAMES = ["UserID", "Username", "EmailID", "Password", "MobileNo", "Response Time"]
ITERATIONS = 1000000
MAX_TRIES = 5


def signup_request():
    username = random_string.alpha_numeric_variable_length(USERNAME_MIN_LENGTH, USERNAME_MAX_LENGTH)
    email = username + EMAIL_DOMAIN
    mobile_no = random_string.numeric_fixed_length(MOBILE_NUMBER_LENGTH)
    password = random_string.alpha_numeric_fixed_length(PASSWORD_LENGTH)
    response = requests.post(SIGNUP_URL,
                             json={"Username": username,
                                   "EmailID": email,
                                   "MobileNo": mobile_no,
                                   "Password": password,
                                   }
                             )
    tries = 1
    while response.status_code != 200 and tries < MAX_TRIES:
        username = random_string.alpha_numeric_variable_length(USERNAME_MIN_LENGTH, USERNAME_MAX_LENGTH)
        email = username + EMAIL_DOMAIN
        response = requests.post(SIGNUP_URL,
                                 json={"Username": username,
                                       "EmailID": email,
                                       "MobileNo": mobile_no,
                                       "Password": password,
                                       }
                                 )
        tries += 1
    return response, username, email, mobile_no, password


def get_user(result):
    (response, username, email, mobile_no, password) = result
    user_id = response.json()["UserId"]
    response_time = response.elapsed.total_seconds()
    return {
        "UserID": user_id,
        "Username": username,
        "EmailID": email,
        "Password": password,
        "MobileNo": mobile_no,
        "Response Time": response_time,
    }


csv_file = open(OUTPUT_FILE_NAME, 'w')
writer = csv.DictWriter(csv_file, fieldnames=CSV_COLUMN_NAMES)
writer.writeheader()
with concurrent.futures.ThreadPoolExecutor(max_workers=ITERATIONS) as executor:
    futures = {executor.submit(signup_request) for _ in range(ITERATIONS)}
    for future in concurrent.futures.as_completed(futures):
        writer.writerow(get_user(future.result()))
