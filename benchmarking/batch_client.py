from client import Client
import pandas as pd
import ast
from concurrent.futures import ThreadPoolExecutor
import requests
import os
import csv
import configparser

config = configparser.ConfigParser()
config.read('config.ini')

RESPONSE_FILE_NAME = config["BatchClient"]["CREATE_MESSAGE_RESPONSE_FILE_NAME"]

class BatchClient:

    def __init__(self, api_endpoint, request_type, request_body_dataset_path):
        """Initializes BatchClient with request_body_dataset_path."""
        self.api_endpoint = api_endpoint
        self.request_type = request_type
        self.request_body_dataset_path = request_body_dataset_path

    def __store_responses_in_file(self, response_entry_all_batches):
        with open(RESPONSE_FILE_NAME, 'w') as csv_file:  
            csv_writer = csv.writer(csv_file)
            csv_writer.writerow(["Metadata", "Responses"])
            csv_writer.writerows(response_entry_all_batches)

    def __create_clients(self, count, request_body_list):
        clients = []
        for i in range(0, count):
            clients.append(Client(
                            self.api_endpoint, 
                            self.request_type, 
                            request_body_list[i]
                            )
                        )

        return clients
    
    def send_requests_store_responses(self):
        request_body_df = pd.read_csv(self.request_body_dataset_path)
        batch_count = len(request_body_df)
        response_entry_all_batches = []
        
        for batch_id in range(0, batch_count):
            batch_metadata = ast.literal_eval(request_body_df["Metadata"][batch_id])
            batch_data = ast.literal_eval(request_body_df["Data"][batch_id])
            sub_batch_count = len(batch_data)
            client_count = batch_metadata["qps"]
            response_entry_batch = []

            for sub_batch_id in range(0, sub_batch_count):
                clients = self.__create_clients(client_count, batch_data[sub_batch_id])
                with ThreadPoolExecutor(max_workers = client_count) as executor:
                    responses = executor.map(Client.call_send_request, clients)

                response_entry_sub_batch = []

                for response in responses:
                    responseBody = response.json()
                    responseBody["Latency"] = response.elapsed.total_seconds()
                    response_entry_sub_batch.append(responseBody)

                response_entry_batch.append(response_entry_sub_batch)
                
            response_entry_all_batches.append([batch_metadata, response_entry_batch])

        self.__store_responses_in_file(response_entry_all_batches)
                        
