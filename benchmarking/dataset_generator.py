from internal_node import InternalNode
import configparser
import csv

config = configparser.ConfigParser()
config.read("config.ini")

DATASET_FILE_NAME = config["DatasetGenerator"]["DATASET_FILE_NAME"]

class DatasetGenerator:

    def __init__(self):
        self.root_node = InternalNode(depth_in_tree = 0, parameter_to_value = {})
        data = self.root_node.create_child_nodes()

        with open(DATASET_FILE_NAME, 'w') as csv_file:  
            csv_writer = csv.writer(csv_file)
            csv_writer.writerow(["Metadata", "Data"])
            csv_writer.writerows(data)

datasetGenerator = DatasetGenerator()
