import os
import csv
from parse_tree import Classification, IO

info = {}
def load_info():
    global info
    with open('src/build/manual_info.tsv', newline='') as csvfile:
        reader = csv.reader(csvfile, delimiter='\t', quotechar='|')
        next(reader)
        info = {row[0]: {'class': Classification.from_str(row[1]), 'io': IO.from_str(row[2])} for row in reader}


def classification(name):
    return info[name]['class']

def io(name):
    return info[name]['io']

