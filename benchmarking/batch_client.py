from client import Client
import pandas as pd
import ast
from concurrent.futures import ThreadPoolExecutor
import requests
import os
import configparser
from csv_file_writer import CsvFileWriter 

class BatchClient:
    """Represents a Benchmarking-in-Batch Client.
    
    Responsible for sending a set of requests in batches, to 
    the given API and storing the consequent responses in a CSV.

    Attributes:
        api_endpoint: A String for the URL/prefix of URL at which to send the request.
        request_type: A String for the HTTP Request Type of the requests to be sent (POST/GET here).
        request_dataset_path: A String for the complete path of the file containing request body/URL suffixes.
        response_file_name: A String for the complete path of the file in which to store the responses.
    """

    def __init__(self, api_endpoint, request_type, request_dataset_path, response_file_name):
        """Initializes BatchClient with api_endpoint, request_type, request_dataset_path and response_file_name."""
        self.api_endpoint = api_endpoint
        self.request_type = request_type
        self.request_dataset_path = request_dataset_path
        self.response_file_name = response_file_name

    def __create_post_clients(self, count, request_body_list):
        clients = []

        for i in range(0, count):
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
        request_body_df = pd.read_csv(self.request_dataset_path)
        response_entry_all_batches = []
        
        for batch_id in range(0, len(request_body_df)):
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

        csv_file_writer = CsvFileWriter(self.response_file_name, ["Metadata", "Responses", "Latency"], response_entry_all_batches)
        csv_file_writer.write_to_csv()
                        
