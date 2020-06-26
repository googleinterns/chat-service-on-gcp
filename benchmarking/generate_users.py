"""
generates dummy users by sending multiple concurrent requests to signup API
stores the user details like UserID, Username, EmailID, MobileNo, Password,
Response Time and Response Status Code to a CSV file
"""

import concurrent.futures
import random_string
import requests
import csv

USERNAME_MIN_LENGTH = 4
USERNAME_MAX_LENGTH = 8
PASSWORD_LENGTH = 8
MOBILE_NUMBER_LENGTH = 10
EMAIL_DOMAIN = "@dummy.com"
ITERATIONS = 100
OUTPUT_FILE_NAME = "users.csv"
CSV_COLUMN_NAMES = ["UserID", "Username", "EmailID", "Password", "MobileNo", "Response Time"]


def signup_request():
    username = random_string.alpha_numeric_variable_length(USERNAME_MIN_LENGTH, USERNAME_MAX_LENGTH)
    email = username + EMAIL_DOMAIN
    mobile_no = random_string.numeric_fixed_length(MOBILE_NUMBER_LENGTH)
    password = random_string.alpha_numeric_fixed_length(PASSWORD_LENGTH)
    response = requests.post('https://gcp-chat-service.an.r.appspot.com/signup',
                             json={"Username": username,
                                   "EmailID": email,
                                   "MobileNo": mobile_no,
                                   "Password": password,
                                   }
                             )
    while response.status_code != 200:
        username = random_string.alpha_numeric_variable_length(USERNAME_MIN_LENGTH, USERNAME_MAX_LENGTH)
        email = username + EMAIL_DOMAIN
        response = requests.post('https://gcp-chat-service.an.r.appspot.com/signup',
                                 json={"Username": username,
                                       "EmailID": email,
                                       "MobileNo": mobile_no,
                                       "Password": password,
                                       }
                                 )
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
