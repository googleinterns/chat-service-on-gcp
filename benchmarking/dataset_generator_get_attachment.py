"""Contains the DatasetGeneratorForGetAttachment class.

Creates a DatasetGeneratorForGetAttachment object. 
Reads the Response file of CreateMessage and 
generates the dataset containing URL suffixes 
for making calls to the getAttachment API.
"""

import configparser
import ast 
import pandas as pd 
from csv_file_writer import CsvFileWriter 

config = configparser.ConfigParser()
config.read('config.ini')

CREATE_MESSAGE_RESPONSE_FILE_NAME = config["BatchClientDriverForCreateMessage"]["CREATE_MESSAGE_RESPONSE_FILE_NAME"]
DATASET_FILE_NAME = config["BatchClientDriverForGetAttachment"]["DATASET_FILE_NAME"]

class DatasetGeneratorForGetAttachment:
    """Generator of the dataset of URL Suffixes to getAttachment.
    
    Responsible for generating a dataset of URL Suffixes 
    for GET requests to the getAttachment API. Each suffix
    corresponds to call to getAttachment for a valid message
    i.e. one that does contain an attachment. 
    """

    def __init__(self):
        """Intializes DatasetGeneratorForGetAttachment."""
        self.__generate_dataset_for_get_attachment()

    def __generate_dataset_for_get_attachment(self):
        create_message_responses_df = pd.read_csv(CREATE_MESSAGE_RESPONSE_FILE_NAME)
        message_id_list_all_batches = []
        
        for batch_id in range(len(create_message_responses_df)):
            batch_metadata = ast.literal_eval(create_message_responses_df["Metadata"][batch_id])
            batch_create_message_responses = ast.literal_eval(create_message_responses_df["Responses"][batch_id])
            
            if (batch_metadata["prob_of_file"] != 1):
                continue

            message_id_list_batch = []

            for sub_batch_id in range(len(batch_create_message_responses)):
                message_id_list_sub_batch = []

                for response_id in range(len(batch_create_message_responses[sub_batch_id])):
                    message_id_list_sub_batch.append(
                        str(batch_create_message_responses[sub_batch_id][response_id]["MessageId"])
                        + "/attachments")

                message_id_list_batch.append(message_id_list_sub_batch)
            
            message_id_list_all_batches.append([batch_metadata, message_id_list_batch])

        csv_file_writer = CsvFileWriter(DATASET_FILE_NAME, ["Metadata", "Data"], message_id_list_all_batches)
        csv_file_writer.write_to_csv()

datasetGenerator = DatasetGeneratorForGetAttachment()
