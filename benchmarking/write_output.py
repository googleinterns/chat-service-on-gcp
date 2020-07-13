import requests
import csv

def get_next_response_entry(api_response_object_qps, api_to_proportion_of_requests_of_api):
    """
    Accepts one tuple of the form (API Name, Response Object, Current QPS).
    Returns a Response entry to be filled in the ResponseEntry.csv file.

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
    CSV_FILE_NAME = "ResponseEntry.csv"
    CSV_COLUMN_NAMES = ["API Name", "ResponseTime", "login QPS", "viewUser QPS", "getUsersByMobileNumber QPS",
                        "listChats QPS", "listMessages QPS", "createChat QPS", "createMessage QPS", "signup QPS",
                        "Total QPS"]

    response_entries = []

    for api_response_object_qps in api_response_objects_qps:
        response_entries.append(get_next_response_entry(api_response_object_qps, api_to_proportion_of_requests_of_api))

    with open(CSV_FILE_NAME, 'w') as csv_file:  
        csv_writer = csv.writer(csv_file)  
        csv_writer.writerow(CSV_COLUMN_NAMES)  
        csv_writer.writerows(response_entries)
