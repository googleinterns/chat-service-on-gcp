import requests
import csv
import configparser


config = configparser.ConfigParser()
config.read('config.ini')
file_names = config['File Names']


def get_next_response_entry(api_response_object_qps, api_to_proportion_of_requests_of_api):
    """
    Accepts one tuple of the form (API Name, Response Object, Current QPS).
    Returns a Response entry to be filled in the ResponseEntry.csv file.
    Returns None if the response object's status code is not 200.

    The entry includes:
    1. APIName
    2. ResponseTime
    3. LoginQps
    4. ViewUserQps
    5. GetUsersByMobileNumberQps
    6. ListChatsQps
    7. ListMessagesQps
    8. CreateChatQps
    9. CreateMessageQps
    10. SignupQps
    11. TotalQps
    """

    response_entry = []
    response_object = api_response_object_qps[1]

    # if response is not 200, don't consider the response time and write the status code and the api name to error file
    if response_object.status_code != 200:
        print(response_object.status_code, file=open(file_names['ERROR'], 'a'))
        print(api_response_object_qps[0], file=open(file_names['ERROR'], 'a'))
        return None

    total_qps = api_response_object_qps[2]
    
    # Adding API Name to the response entry
    response_entry.append(api_response_object_qps[0])

    # Adding Response Time to the response entry
    response_entry.append(response_object.elapsed.total_seconds())

    api_list = ["login", "viewUser", "getUsersByMobileNumber", "listChats", "listMessages", "createChat",
                "createMessage", "signup"]

    for api in api_list:
        response_entry.append(total_qps * api_to_proportion_of_requests_of_api[api])
    
    response_entry.append(total_qps)

    return response_entry


def write_output(api_response_objects_qps, api_to_proportion_of_requests_of_api):
    """
    Accepts a list of tuples of the form (APIName, Response Object, Current QPS).
    Fills in a Response entry for each tuple in the ResponseEntry.csv file.

    Each entry includes:
    1. APIName
    2. ResponseTime
    3. LoginQps
    4. ViewUserQps
    5. ListChatsQps
    6. ListMessagesQps
    7. CreateChatQps
    8. CreateMessageQps
    9. SignupQps
    10. TotalQps
    """
    CSV_FILE_NAME = file_names['WRITE_OUTPUT']
    CSV_COLUMN_NAMES = ["API Name", "ResponseTime", "login QPS", "viewUser QPS", "getUsersByMobileNumber QPS",
                        "listChats QPS", "listMessages QPS", "createChat QPS", "createMessage QPS", "signup QPS",
                        "Total QPS"]
    open(file_names['ERROR'], 'w').close()  # erase the contents of error file to discard any previous entries

    response_entries = []

    for api_response_object_qps in api_response_objects_qps:
        response_entry = get_next_response_entry(api_response_object_qps, api_to_proportion_of_requests_of_api)
        if response_entry is not None:
            response_entries.append(response_entry)

    with open(CSV_FILE_NAME, 'w') as csv_file:  
        csv_writer = csv.writer(csv_file)  
        csv_writer.writerow(CSV_COLUMN_NAMES)  
        csv_writer.writerows(response_entries)
