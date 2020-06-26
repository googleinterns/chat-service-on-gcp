"""
generates 2 chats for each user in users.csv file
stores userIds, chatId and response time to a CSV file
"""
import pandas as pd
import requests
import csv
import concurrent.futures

USERS_FILE_NAME = "users.csv"
OUTPUT_FILE_NAME = "chats.csv"
CSV_COLUMN_NAMES = ["UserID1", "UserID2", "ChatID", "Response Time"]


def generate_data():
    users = pd.read_csv(USERS_FILE_NAME)
    users_count = len(users.index)
    block_size = users_count // 2
    data = []
    for i in range(block_size):
        user_id1 = users['UserID'][i]
        username = users['Username'][i + block_size]
        user_id2 = users['UserID'][i + block_size]
        data.append((user_id1, username, user_id2))
        username = users['Username'][users_count - 1 - i]
        user_id2 = users['UserID'][users_count - 1 - i]
        data.append((user_id1, username, user_id2))
    return data


def create_chat_request(user_id1, username, user_id2):
    response = requests.post('https://gcp-chat-service.an.r.appspot.com/users/' + str(user_id1) + '/chats',
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
    futures = {executor.submit(create_chat_request, i, j, k) for i, j, k in data}
    for future in concurrent.futures.as_completed(futures):
        writer.writerow(get_chat(future.result()))
