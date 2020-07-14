import ast
import csv
import pandas as pd
from csv_file_writer import CsvFileWriter 
import configparser

config = configparser.ConfigParser()
config.read('config.ini')

class StatisticalAnalyst:

    def __init__(self, dataset_file_path, results_file_path, metadata_attribute_name, metadata_params, data_attribute_name, percentiles, percentile_names):
        self.dataset_file_path = dataset_file_path
        self.results_file_path = results_file_path
        self.metadata_attribute_name = metadata_attribute_name
        self.metadata_params = metadata_params
        self.data_attribute_name = data_attribute_name
        self.percentiles = percentiles
        self.percentile_names = percentile_names

    def __get_metadata_param_values_for_entry(self, metadata):
        metadata_param_values = []

        for metadata_param in self.metadata_params:
            metadata_param_values.append(metadata[metadata_param])

        return metadata_param_values

    def __get_given_percentiles_of_data_for_entry(self, data):
        sorted_data = data.copy()
        sorted_data.sort()
        data_count = len(sorted_data)
        percentile_values = []

        for percentile in self.percentiles:
            percentile_values.append(sorted_data[round(data_count * percentile / 100) - 1])

        return percentile_values

    def perform_statistical_analysis(self):
        dataset_df = pd.read_csv(self.dataset_file_path)
        result_entries = []

        for entry_id in range(len(dataset_df)):
            metadata = ast.literal_eval(dataset_df[self.metadata_attribute_name][entry_id])
            data = ast.literal_eval(dataset_df[self.data_attribute_name][entry_id])

            result_entry = []

            result_entry.extend(self.__get_metadata_param_values_for_entry(metadata))
            result_entry.extend(self.__get_given_percentiles_of_data_for_entry(data))

            result_entries.append(result_entry)

        result_col_names = self.metadata_params.copy()
        result_col_names.extend([percentile_name + " Percentile" for percentile_name in self.percentile_names])

        csv_file_writer = CsvFileWriter(self.results_file_path, result_col_names, result_entries)
        csv_file_writer.write_to_csv()


statistical_analyst_for_create_message = StatisticalAnalyst(
                                            config["BatchClientDriverForCreateMessage"]["CREATE_MESSAGE_RESPONSE_FILE_NAME"],
                                            config["BatchClientDriverForCreateMessage"]["CREATE_MESSAGE_LATENCY_FILE_NAME"],
                                            "Metadata",
                                            ast.literal_eval(config["InternalNode"]["PARAMETER_HIERARCHY"]),
                                            "Latency",
                                            [50, 95, 99],
                                            ["50th", "95th", "99th"]
                                            )
statistical_analyst_for_create_message.perform_statistical_analysis()

statistical_analyst_for_get_attachment = StatisticalAnalyst(
                                            config["BatchClientDriverForGetAttachment"]["GET_ATTACHMENT_RESPONSE_FILE_NAME"],
                                            config["BatchClientDriverForGetAttachment"]["GET_ATTACHMENT_LATENCY_FILE_NAME"],
                                            "Metadata",
                                            ast.literal_eval(config["InternalNode"]["PARAMETER_HIERARCHY"]),
                                            "Latency",
                                            [50, 95, 99],
                                            ["50th", "95th", "99th"]
                                            )
statistical_analyst_for_get_attachment.perform_statistical_analysis()


