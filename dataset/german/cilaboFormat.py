import pandas as pd

ln = "\n"

# =============================================================================
# 
# =============================================================================
def cilaboFormat(dataName, classLabel, a):
    print(dataName)
    fileName = dataName + "_processed.csv"
    df = pd.read_csv(fileName)
    attributes = list(df.keys())
    attributes.remove(classLabel)
    attributes.remove("a")

    dataSize = len(df)
    Ndim = len(attributes)
    Cnum = len(df[classLabel].unique())
    
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
    
    outputFile = dataName + "_cilabo.csv"
    with open(outputFile, mode="w") as f:
        f.write(line)
    print("")
    print("---Done---")
# =============================================================================


## Adult
dataName = "adult"
classLabel = "income-per-year"
a = "race"
# cilaboFormat(dataName, classLabel, a)


## German
dataName = "german"
classLabel = "credit"
a = "age"
cilaboFormat(dataName, classLabel, a)

## ProPublica
dataName = "propublica-recidivism"
classLabel = "two_year_recid"
a = "race"
cilaboFormat(dataName, classLabel, a)