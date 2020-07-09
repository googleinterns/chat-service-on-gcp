from batch_client import BatchClient
from csv_file_writer import CsvFileWriter
import configparser
 
config = configparser.ConfigParser()
config.read('config.ini')

GET_ATTACHMENT_URL = config["Url"]["BASE_URL"] + config["Url"]["GET_ATTACHMENT_ENDPOINT"]
DATASET_FILE_NAME = config["BatchClientDriverForGetAttachment"]["DATASET_FILE_NAME"]
RESPONSE_FILE_NAME = config["BatchClientDriverForGetAttachment"]["GET_ATTACHMENT_RESPONSE_FILE_NAME"]

class BatchClientDriverForGetAttachment:

    def __init__(self):
        self.batch_client = BatchClient(GET_ATTACHMENT_URL, "GET", DATASET_FILE_NAME, RESPONSE_FILE_NAME)
        self.batch_client.send_requests_store_responses()
        

batch_client_driver = BatchClientDriverForGetAttachment()
