"""
Generates 2 chats for each user in users.csv file.
Stores userIds, chatId and response time to a CSV file.
"""
import configparser
import pandas as pd
import requests
import csv
import concurrent.futures

config = configparser.ConfigParser()
config.read('config.ini')
url_constants = config['Url']
file_names = config['File Names']

USERS_FILE_NAME = file_names['USERS']
OUTPUT_FILE_NAME = file_names['CHATS']
BASE_URL = url_constants['BASE_URL']
CSV_COLUMN_NAMES = ["UserID1", "UserID2", "ChatID", "Response Time"]


def generate_data():
    """
    Generates a list of tuples (user_id, peer_username, peer_user_id).
    This will be used later to create a chat between users having UserIDs as user_id and peer_user_id.
    peer_username is the Username of user having UserID as peer_user_id.

    Divide all users into 2 groups A and B having equal users.
    Let’s assume that users are numbered from 0 to n-1.
    Group A has users from 0 to n/2 - 1 while group B has users from n/2 to n-1.
    Now for each user x in group A, we will create a chat between x and n/2 + x
    and another chat between x and (n - 1 - x).
    """
    users = pd.read_csv(USERS_FILE_NAME)  # read all users data from users.csv
    users_count = len(users.index)  # total number of users
    block_size = users_count // 2  # divide all users into 2 blocks having equal number of users
    data = []
    for i in range(block_size):
        user_id = users['UserID'][i]

        peer_username1 = users['Username'][i + block_size]
        peer_user_id1 = users['UserID'][i + block_size]
        data.append((user_id, peer_username1, peer_user_id1))

        peer_username2 = users['Username'][users_count - 1 - i]
        peer_user_id2 = users['UserID'][users_count - 1 - i]
        data.append((user_id, peer_username2, peer_user_id2))
    return data


def create_chat_request(user_id1, username, user_id2):
    response = requests.post(BASE_URL + 'users/' + str(user_id1) + '/chats',
                             json={"username": username}
                             )
    return response, user_id1, user_id2


def get_chat(result):
    response, user_id1, user_id2 = result
    response_time = response.elapsed.total_seconds()
    return {
        "UserID1": user_id1,
        "UserID2": user_id2,
        "ChatID": response.json()["ChatId"],
        "Response Time": response_time,
    }


csv_file = open(OUTPUT_FILE_NAME, 'w')
writer = csv.DictWriter(csv_file, fieldnames=CSV_COLUMN_NAMES)
writer.writeheader()
data = generate_data()
with concurrent.futures.ThreadPoolExecutor(max_workers=len(data)) as executor:
    futures = {executor.submit(create_chat_request, user_id, peer_username, peer_user_id)
               for user_id, peer_username, peer_user_id in data}
    for future in concurrent.futures.as_completed(futures):
        writer.writerow(get_chat(future.result()))
