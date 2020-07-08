from batch_client import BatchClient
import ast
import csv
import pandas as pd
import configparser

config = configparser.ConfigParser()
config.read('config.ini')

GET_ATTACHMENT_URL = config["Url"]["BASE_URL"] + config["Url"]["GET_ATTACHMENT_ENDPOINT"]
CREATE_MESSAGE_RESPONSE_FILE_NAME = config["BatchClientDriverForCreateMessage"]["CREATE_MESSAGE_RESPONSE_FILE_NAME"]
DATASET_FILE_NAME = config["BatchClientDriverForGetAttachment"]["DATASET_FILE_NAME"]
RESPONSE_FILE_NAME = config["BatchClientDriverForGetAttachment"]["GET_ATTACHMENT_RESPONSE_FILE_NAME"]

class BatchClientDriverForGetAttachment:

    def __init__(self):
        self.__generate_dataset_for_get_attachment()
        self.batch_client = BatchClient(GET_ATTACHMENT_URL, "GET", DATASET_FILE_NAME, RESPONSE_FILE_NAME)
        self.batch_client.send_requests_store_responses()

    def __store_generated_dataset_in_file(self, data):
        with open(DATASET_FILE_NAME, 'w') as csv_file:  
            csv_writer = csv.writer(csv_file)
            csv_writer.writerow(["Metadata", "Data"])
            csv_writer.writerows(data)

    def __generate_dataset_for_get_attachment(self):
        create_message_responses_df = pd.read_csv(CREATE_MESSAGE_RESPONSE_FILE_NAME)
        message_id_list_all_batches = []
        
        for batch_id in range(0, len(create_message_responses_df)):
            batch_metadata = ast.literal_eval(create_message_responses_df["Metadata"][batch_id])
            batch_create_message_responses = ast.literal_eval(create_message_responses_df["Responses"][batch_id])
            
            if (batch_metadata["prob_of_file"] != 1):
                continue

            message_id_list_batch = []

            for sub_batch_id in range(0, len(batch_create_message_responses)):
                message_id_list_sub_batch = []

                for response_id in range(0, len(batch_create_message_responses[sub_batch_id])):
                    message_id_list_sub_batch.append(
                        str(batch_create_message_responses[sub_batch_id][response_id]["MessageId"])
                        + "/attachments")

                message_id_list_batch.append(message_id_list_sub_batch)
            
            message_id_list_all_batches.append([batch_metadata, message_id_list_batch])

        self.__store_generated_dataset_in_file(message_id_list_all_batches)
                

batch_client_driver = BatchClientDriverForGetAttachment()
