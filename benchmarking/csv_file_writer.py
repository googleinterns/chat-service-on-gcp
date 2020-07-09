import csv

class CsvFileWriter:
    """Represents a file writer for CSVs.
    
    Responsible writing the given data in a 
    CSV with the given file name and column names.

    Attributes:
        csv_name: A String for name of CSV to be written to.
        csv_col_names: A list of String column names of the CSV.
        csv_rows: A list of rows to be written to the CSV.
    """

    def __init__(self, csv_name, csv_col_names, csv_rows):
        """Intializes CsvFileWriter with csv_name, csv_col_names and csv_rows."""
        self.csv_name = csv_name
        self.csv_col_names = csv_col_names
        self.csv_rows = csv_rows

    def write_to_csv(self):
        with open(self.csv_name, 'w') as csv_file:  
            csv_writer = csv.writer(csv_file)
            csv_writer.writerow(self.csv_col_names)
            csv_writer.writerows(self.csv_rows)
