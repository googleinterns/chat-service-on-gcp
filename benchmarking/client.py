import requests
import logging

class Client:
    """Represents a Client which sends requests.
    
    Responsible for a request(POST/GET) to the given API.

    Attributes:
        api_endpoint: A String for the URL at which to send the request.
        request_type: A String for the HTTP Request Type of the requests to be sent (POST/GET here).
        request_body: A dict containing at least one of the following keys:
                        <ol>
                        <li> data : {textContent: <random_string>} </li>
                        <li> files : {file: (<file_name>, <file_content>, <file mime type>)}
                        </ol>
    """

    def __init__(self, api_endpoint, request_type, request_body):
        """Initializes Client with api_endpoint, request_type and request_body."""
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
