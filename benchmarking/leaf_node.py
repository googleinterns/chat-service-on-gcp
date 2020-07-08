from parameter import Parameter
import os
import csv
import random
import string
import numpy as np
import uuid
from concurrent.futures import ThreadPoolExecutor
from concurrent.futures import as_completed
import time
import threading

class LeafNode:
    """Represents the leaf nodes of the threaded tree.
    
    Responsible for generating the data set of message content which conforms 
    to the parameter values. 
    Responsible for sending requests to the createMessage API.

    Attributes:
        parameter_to_value: A dictionary mapping parameter names to values for all 
                            parameters.
    """
   
    def __init__(self, parameter_to_value):
        """Initializes Leaf Node with parameter_to_value."""

        self.parameter_to_value = parameter_to_value.copy() 
        
    def __get_qps(self):
        return self.parameter_to_value["qps"]

    def __get_prob_of_file(self):
        return self.parameter_to_value["prob_of_file"]

    def __get_mean_length_of_text(self):
        return self.parameter_to_value["mean_length_of_text"]

    def __get_std_dev_length_of_text(self):
        return self.parameter_to_value["std_dev_length_of_text"]

    def __get_mean_length_of_text_with_file(self):
        return self.parameter_to_value["mean_length_of_text_with_file"]

    def __get_std_dev_length_of_text_with_file(self):
        return self.parameter_to_value["std_dev_length_of_text_with_file"]

    def __get_mean_file_size(self):
        return self.parameter_to_value["mean_file_size"]

    def __get_std_dev_file_size(self):
        return self.parameter_to_value["std_dev_file_size"]

    def __sample_normal_file_size_generate_random_file_content(self, mean_file_size, std_dev_file_size):
        file_size = min(25, 
                        round(np.random.normal(
                                loc = mean_file_size, 
                                scale = std_dev_file_size
                                )
                            )
                        )
        file_name = "{}_{}_{}_".format(
                                    file_size, 
                                    mean_file_size, 
                                    std_dev_file_size
                                    ) + str(uuid.uuid4()) + ".bin"

        return file_name, os.urandom(file_size)

    def __sample_normal_length_of_text_generate_random_text(self, mean_length_of_text, std_dev_length_of_text):
        length_of_text = round(np.random.normal(
                                    loc = mean_length_of_text, 
                                    scale = std_dev_length_of_text
                                    )
                                )

        return "".join(
                    random.choices(
                        string.ascii_letters + 
                        string.digits + 
                        string.punctuation + 
                        string.whitespace, k = length_of_text
                        )
                    )    

    def __generate_message_content_with_file_with_text(self, message_count):
        message_content_with_file_with_text = []
        mean_length_of_text_with_file = self.__get_mean_length_of_text_with_file()
        std_dev_length_of_text_with_file = self.__get_std_dev_length_of_text_with_file()
        mean_file_size = self.__get_mean_file_size()
        std_dev_file_size = self.__get_std_dev_file_size()

        for count in range(0, message_count):
            message_content = {}
            message_content["textContent"] = self.__sample_normal_length_of_text_generate_random_text(
                                                    mean_length_of_text_with_file, 
                                                    std_dev_length_of_text_with_file
                                                    )
            file_name, file_content = self.__sample_normal_file_size_generate_random_file_content(mean_file_size, std_dev_file_size)
            message_content["file"] = (file_name, file_content)
            
            message_content_with_file_with_text.append(message_content)

        return message_content_with_file_with_text

    def __generate_message_content_with_file_without_text(self, message_count):
        message_content_with_file_without_text = []
        mean_file_size = self.__get_mean_file_size()
        std_dev_file_size = self.__get_std_dev_file_size()

        for count in range(0, message_count):
            message_content = {}
            file_name, file_content = self.__sample_normal_file_size_generate_random_file_content(mean_file_size, std_dev_file_size)
            message_content["file"] = (file_name, file_content)
            
            message_content_with_file_without_text.append(message_content)

        return message_content_with_file_without_text

    def __generate_message_content_without_file(self, message_count):
        message_content_without_file = []
        mean_length_of_text = self.__get_mean_length_of_text()
        std_dev_length_of_text = self.__get_std_dev_length_of_text()

        for count in range(0, message_count):
            message_content = {}
            message_content["textContent"] = self.__sample_normal_length_of_text_generate_random_text(
                                                        mean_length_of_text, 
                                                        std_dev_length_of_text
                                                        )

            message_content_without_file.append(message_content)

        return message_content_without_file

    def __get_message_count(self):
        """Counts the number of messages of each type to be generated.

        Returns:
            A dictionary containing keys for the following:
                <ol>
                <li> Total number of messages </li>
                <li> Number of messages without file </li>
                <li> Total number of messages with file </li>
                <li> Number of messages with file but without text </li>
                <li> Number of messages with file and text </li>
                </ol>
        """
        qps = self.__get_qps()
        prob_of_file = self.__get_prob_of_file()

        message_count = {}
        message_count["total"] = qps
        message_count["without_file"] = round((1 - prob_of_file) * message_count["total"])
        message_count["with_file"] = {}
        message_count["with_file"]["total"] = message_count["total"] - message_count["without_file"]
        message_count["with_file"]["without_text"] = round(message_count["with_file"]["total"] / 2)
        message_count["with_file"]["with_text"] = message_count["with_file"]["total"] - message_count["with_file"]["without_text"]

        return message_count

    def generate_message_content_dataset(self):
        """Generates the message content data set conforming to the given parameters.
        
        Returns:
            A list containing:
                <ol>
                <li> A dict mapping each parameter to its value. </li>
                <li> A list of message content consisting of t entries (t is duration in seconds).
                    Each entry: A list of message content of qps number of messages.
                    Each message content: A dictionary containing at least one of the keys:
                        <ol>
                        <li> textContent: value = a randomly generated string </li>
                        <li> file: value = a randomly generated binary file </li>
                        </ol>
                </li>
                </ol>
        """
        message_count = self.__get_message_count()
        
        message_content_all_batches = []

        for batch_number in range(0, Parameter.DURATION_SECONDS):
            message_content_for_batch = []
            message_content_for_batch.extend(self.__generate_message_content_without_file(message_count["without_file"]))
            message_content_for_batch.extend(self.__generate_message_content_with_file_without_text(message_count["with_file"]["without_text"]))
            message_content_for_batch.extend(self.__generate_message_content_with_file_with_text(message_count["with_file"]["with_text"]))

            message_content_all_batches.append(message_content_for_batch)

        return [self.parameter_to_value, message_content_all_batches]

    @staticmethod
    def call_generate_message_content_dataset(leaf_node):
        """Calls the instance method generate_message_content_dataset."""
        return leaf_node.generate_message_content_dataset()
