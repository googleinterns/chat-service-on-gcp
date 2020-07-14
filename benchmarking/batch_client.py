from client import Client
import pandas as pd
import ast
from concurrent.futures import ThreadPoolExecutor
import requests
import os
import csv
import configparser

class BatchClient:

    def __init__(self, api_endpoint, request_type, request_body_dataset_path, response_file_name):
        """Initializes BatchClient with request_body_dataset_path."""
        self.api_endpoint = api_endpoint
        self.request_type = request_type
        self.request_body_dataset_path = request_body_dataset_path
        self.response_file_name = response_file_name

    def __store_responses_in_file(self, response_entry_all_batches):
        with open(self.response_file_name, 'w') as csv_file:  
            csv_writer = csv.writer(csv_file)
            csv_writer.writerow(["Metadata", "Responses", "Latency"])
            csv_writer.writerows(response_entry_all_batches)

    def __create_post_clients(self, count, request_body_list):
        clients = []
        for i in range(count):
            clients.append(Client(
                            self.api_endpoint, 
                            self.request_type, 
                            request_body_list[i]
                            )
                        )

        return clients

    def __create_get_clients(self, count, url_path_var_list):
        clients = []

        for i in range(0, count):
            clients.append(Client(
                            self.api_endpoint + url_path_var_list[i], 
                            self.request_type,
                            None
                            )
                        )

        return clients
    
    def send_requests_store_responses(self):
        request_body_df = pd.read_csv(self.request_body_dataset_path)
        response_entry_all_batches = []
        
        for batch_id in range(len(request_body_df)):
            batch_metadata = ast.literal_eval(request_body_df["Metadata"][batch_id])
            batch_data = ast.literal_eval(request_body_df["Data"][batch_id])
            response_entry_batch = []
            latency_entry_batch = []

            for sub_batch_id in range(0, len(batch_data)):
                client_count = batch_metadata["qps"]
                clients = []

                if self.request_type == "GET":
                    clients = self.__create_get_clients(client_count, batch_data[sub_batch_id])
                elif self.request_type == "POST":
                    clients = self.__create_post_clients(client_count, batch_data[sub_batch_id])
                    
                with ThreadPoolExecutor(max_workers = client_count) as executor:
                    responses = executor.map(Client.call_send_request, clients)

                response_entry_sub_batch = []

                for response in responses:
                    latency_entry_batch.append(response.elapsed.total_seconds())
                    response_entry_sub_batch.append(response.json())

                response_entry_batch.append(response_entry_sub_batch)
                
            response_entry_all_batches.append([batch_metadata, response_entry_batch, latency_entry_batch])

        self.__store_responses_in_file(response_entry_all_batches)
                        
