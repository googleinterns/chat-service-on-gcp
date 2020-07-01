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
    5. ListChatsQps
    6. ListMessagesQps
    7. CreateChatQps
    8. CreateMessageQps
    9. SignupQps
    10. TotalQps
    """

    api_name = api_response_object_qps[0]
    response_object = api_response_object_qps[1]
    total_qps = api_response_object_qps[2]
    
    response_time = response_object.elapsed
    login_qps = total_qps * api_to_proportion_of_requests_of_api["login"]
    view_user_qps = total_qps * api_to_proportion_of_requests_of_api["viewUser"]
    list_chats_qps = total_qps * api_to_proportion_of_requests_of_api["listChats"]
    list_messages_qps = total_qps * api_to_proportion_of_requests_of_api["listMessages"]
    create_chat_qps = total_qps * api_to_proportion_of_requests_of_api["createChat"]
    create_message_qps = total_qps * api_to_proportion_of_requests_of_api["createMessage"]
    signup_qps = total_qps * api_to_proportion_of_requests_of_api["signup"]

    response_entry = [api_name, response_time, login_qps, view_user_qps, 
                    list_chats_qps, list_messages_qps, create_chat_qps, create_message_qps, signup_qps, total_qps]

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
    CSV_COLUMN_NAMES = ["ApiName", "ResponseTime", "LoginQps", "ViewUserQps", "ListChatsQps", 
                "ListMessagesQps", "CreateChatQps", "CreateMessageQps", "SignupQps", "TotalQps"]

    response_entries = []

    for api_response_object_qps in api_response_objects_qps:
        response_entries.append(get_next_response_entry(api_response_object_qps, api_to_proportion_of_requests_of_api))

    with open(CSV_FILE_NAME, 'w') as csv_file:  
        csv_writer = csv.writer(csv_file)  
        csv_writer.writerow(CSV_COLUMN_NAMES)  
        csv_writer.writerows(response_entries)
