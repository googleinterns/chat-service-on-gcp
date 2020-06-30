"""
module for various random string generating functions
"""

import random
import string


def alpha_numeric_fixed_length(length):
    return ''.join(random.choices(string.ascii_letters + string.digits, k=length))


def alpha_numeric_variable_length(min_length, max_length):
    return alpha_numeric_fixed_length(random.randint(min_length, max_length))


def numeric_fixed_length(length):
    return ''.join(random.choices(string.digits, k=length))
