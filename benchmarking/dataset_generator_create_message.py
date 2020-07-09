"""Contains the DatasetGeneratorForCreateMessage class.

Creates a DatasetGeneratorForCreateMessage object.
Instantiates this object's InternalNode variable. 
Begins the generation of dataset containing message
content for making calls to the createMessage API.
"""

from internal_node import InternalNode
import configparser
from csv_file_writer import CsvFileWriter 

config = configparser.ConfigParser()
config.read("config.ini")

DATASET_FILE_NAME = config["DatasetGenerator"]["DATASET_FILE_NAME"]

class DatasetGeneratorForCreateMessage:
    """Generator of the dataset of requests to createMessage.
    
    Responsible for generating a dataset of message content 
    for sending requests to the createMessage API.

    Attributes:
        root_node: An InternalNode object representing the 
                root of the threading tree for dataset generation.
    """
    
    def __init__(self):
        """Intializes CsvFileWriter with csv_name, csv_col_names, csv_rows."""

        self.root_node = InternalNode(depth_in_tree = 0, parameter_to_value = {})
        
        data = self.root_node.create_child_nodes()
        csv_file_writer = CsvFileWriter(DATASET_FILE_NAME, ["Metadata", "Data"], data)
        csv_file_writer.write_to_csv()

datasetGenerator = DatasetGeneratorForCreateMessage()
