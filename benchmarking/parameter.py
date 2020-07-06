class Parameter:

    COUNT = 8

    DURATION_SECONDS = 2

    DATASET_FILE_NAME = "MessageContentDataset.csv"

    DIRECTORY = "./generated_files/"
    
    HIERARCHY = [
        "qps", 
        "prob_of_file",
        "mean_length_of_text",
        "std_dev_length_of_text",
        "mean_length_of_text_with_file",
        "std_dev_length_of_text_with_file",
        "mean_file_size",
        "std_dev_file_size"
        ]

    EXTREMA = {
        "qps": {
            "min": 2, 
            "max": 4
            }, 
        "prob_of_file": {
            "min": 0, 
            "max": 1
            }, 
        "mean_length_of_text": {
            "min": 10, 
            "max": 200
            }, 
        "std_dev_length_of_text": {
            "min": 5, 
            "max": 30
            }, 
        "mean_length_of_text_with_file": {
            "min": 0, 
            "max": 50
            }, 
        "std_dev_length_of_text_with_file": {
            "min": 5, 
            "max": 30
            },
        "mean_file_size": {
            "min": 1, 
            "max": 24
            }, 
        "std_dev_file_size": {
            "min": 1, 
            "max": 10
            }
        }

    STEP_SIZE = {
        "qps": 2, 
        "prob_of_file": 1, 
        "mean_length_of_text": 190, 
        "std_dev_length_of_text": 25, 
        "mean_length_of_text_with_file": 50, 
        "std_dev_length_of_text_with_file": 25,
        "mean_file_size": 23, 
        "std_dev_file_size": 9
        }
