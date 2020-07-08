import requests
import logging

class Client:
    
    def __init__(self, api_endpoint, request_type, request_body):
        """Initializes Client with post_request_body."""
        self.api_endpoint = api_endpoint
        self.request_type = request_type
        self.request_body = request_body
        self.response = None

    def send_request(self):
        if self.request_type == "GET":
            self.response = requests.get(url = self.api_endpoint)
        elif self.request_type == "POST":
            if "data" in self.request_body and "files" in self.request_body:
                self.response = requests.post(
                                            url = self.api_endpoint, 
                                            data = self.request_body["data"],
                                            files = self.request_body["files"]
                                            )
            elif "data" in self.request_body:
                self.response = requests.post(
                                            url = self.api_endpoint, 
                                            data = self.request_body["data"]
                                            )
            else:
                self.response = requests.post(
                                            url = self.api_endpoint, 
                                            files = self.request_body["files"]
                                            )
            
        return self.response

    @staticmethod
    def call_send_request(client):
        return client.send_request()
