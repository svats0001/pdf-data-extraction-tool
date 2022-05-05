import random
import spacy as sp
import en_core_web_sm
from spacy.tokens import DocBin
from spacy.training.example import Example
import sys
import re

try:
    nlp = sp.load(sys.argv[2])
except:
    print("Failed to load model")

try:
    dataFileName = sys.argv[1]
except:
    dataFileName = ""

try:
    f = open(dataFileName, "r")
    pageText = f.read()
    f.close()
except:
    print("Failed to open file")
    pageText = ""

data = re.split("\\. |;|\u2022", pageText)

outputVals = []

for i in range(len(data)):
    doc = nlp(data[i])
    ents = list(doc.ents)
    entsSize = len(ents)
    j = 0
    while j < (entsSize-1):
        if ents[j].label_ == "MEASURE" and ents[j+1].label_ == "VALUE":
            outputVals.append(ents[j].text + " ----- " + ents[j+1].text)
            if j != (entsSize-2):
                if ents[j+2].label_ == "DATE":
                    outputVals[-1] = outputVals[-1] + " ----- " + ents[j+2].text
                    j = j + 3
                    if "\n" in outputVals[-1]:
                        outputVals[-1] = outputVals[-1].replace("\n", " ")
                    continue
            if "\n" in outputVals[-1]:
                outputVals[-1] = outputVals[-1].replace("\n", " ")
            j = j + 2
        else:
            j = j + 1

for sentence in outputVals:
    print(sentence)