from internal_node import InternalNode
import configparser
from csv_file_writer import CsvFileWriter 

config = configparser.ConfigParser()
config.read("config.ini")

DATASET_FILE_NAME = config["DatasetGenerator"]["DATASET_FILE_NAME"]

class DatasetGenerator:

    def __init__(self):
        self.root_node = InternalNode(depth_in_tree = 0, parameter_to_value = {})
        
        data = self.root_node.create_child_nodes()
        csv_file_writer = CsvFileWriter(DATASET_FILE_NAME, ["Metadata", "Data"], data)
        csv_file_writer.write_to_csv()

datasetGenerator = DatasetGenerator()
