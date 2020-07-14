from batch_client import BatchClient
import configparser

config = configparser.ConfigParser()
config.read('config.ini')

CREATE_MESSAGE_URL = config["Url"]["BASE_URL"] + config["Url"]["CREATE_MESSAGE_ENDPOINT"]
DATASET_FILE_NAME = config["DatasetGenerator"]["DATASET_FILE_NAME"]

class BatchClientDriver:

    def __init__(self):
        self.batch_client_create_message = BatchClient(CREATE_MESSAGE_URL, "POST", DATASET_FILE_NAME)
        self.batch_client_create_message.send_requests_store_responses()

batch_client_driver = BatchClientDriver()
