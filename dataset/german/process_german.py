# -*- coding: utf-8 -*-

import pandas as pd
import numpy as np
import sys

ln = "\n"
info = ""

dataset = "german"
fileName = dataset+"_categorical-binsensitive.csv"
sensitive = pd.read_csv(fileName)

fileName = dataset+".csv"
raw = pd.read_csv(fileName)
info = info + "raw file: " + fileName + ln + ln

classLabel = "credit"
a = "age"
binA = sensitive[a]

attributes = list(raw.keys())
attributes.remove(classLabel)

# Informations
dataSize = 0
Ndim = 0
Cnum = 0

# 欠損値をnp.nanに置換して削除
nolack = raw.replace("?", np.nan).dropna()
dataSize = len(nolack)
Ndim = len(attributes)

dic = {}
info = info + "attributes -----" + ln
for key in attributes:
    print(key)
    dic[key] = []
    
    # numeric or categoric
    isnumeric = ""
    if str(nolack[key][0]).isnumeric():
        isnumeric = "numeric"
    else:
        isnumeric = "categorical"
    info = info + key + ":" + isnumeric + ln
    
    if isnumeric == "numeric":
        array = nolack[key]
        maxValue = float(max(array))
        minValue = float(min(array))
        for x in array:
            normX = (float(x) - minValue)/(maxValue - minValue)
            dic[key].append(normX)
    
    elif isnumeric == "categorical":
        # mapping
        unique = list(nolack[key].unique())
        labels = []
        for i in range(len(unique)):
            labels.append(str(-(i+1)))
        # information
        for original, label in zip(unique, labels):
            info = info + str(original) + ":" + label + ","
        info = info + ln
        # data labeling
        array = list(nolack[key])
        for x in array:
            index = unique.index(str(x))
            label = labels[index]
            dic[key].append(label)
    info = info + ln

info = info + ln

# Classes
info = info + "class -----" + ln
info = info + classLabel + ln
dic[classLabel] = []
unique = list(nolack[classLabel].unique())
Cnum = len(unique)
labels = []
for i in range(len(unique)):
    labels.append(str(i))
for original, label in zip(unique, labels):
    info = info + str(original) + ":" + label + ","
array = list(nolack[classLabel])
for x in array:
    index = unique.index(x)
    label = labels[index]
    dic[classLabel].append(label)

# sensitive attribute as a
dic['a'] = []
array = list(sensitive[a])
for x in array:
    dic['a'].append(x)


# output dataset information
outputFile = dataset+"_info.txt"
with open(outputFile, mode="w") as f:
    f.write(info)

# output for pandas format
df = pd.DataFrame(dic)
outputFile = dataset+"_processed.csv"
df.to_csv(outputFile, index=False)

# sys.exit(0)
# output for cilabo format
line = ""
# header
line = line + str(dataSize) + "," + str(Ndim) + "," + str(Cnum) + ln

for i in range(len(df)):
    pattern = df.iloc[i]
    # Progress    
    if i % 1000 == 0:
        print(".", end="")
    # Attributes
    for key in attributes:
        line = line + str(pattern[key]) + ","
    # a
    line = line + str(pattern["a"]) + ","
    # class label
    line = line + str(pattern[classLabel])
    
    line = line + ln
outputFile = dataset+"_cilabo.csv"
with open(outputFile, mode="w") as f:
    f.write(line)
print("")
print("---Done---")


    
