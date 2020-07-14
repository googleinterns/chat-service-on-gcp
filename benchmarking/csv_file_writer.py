import csv

class CsvFileWriter:

    def __init__(self, csv_name, csv_col_names, csv_rows):
        self.csv_name = csv_name
        self.csv_col_names = csv_col_names
        self.csv_rows = csv_rows

    def write_to_csv(self):
        with open(self.csv_name, 'w') as csv_file:  
            csv_writer = csv.writer(csv_file)
            csv_writer.writerow(self.csv_col_names)
            csv_writer.writerows(self.csv_rows)
