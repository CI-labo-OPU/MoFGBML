# -*- coding: utf-8 -*-
"""
Created on Tue Nov 30 15:59:46 2021

@author: kawano
"""

import pandas as pd
import statistics


folder = 'results\\FAN2021\\iris\\'
sep = "\\"
columns = ["errorRate", "ruleNum"]

Dataset = 'iris'

"""
fuction SummaryOneTrial
this function return results(max rule number, min error rate for train, min error rate for train)
for one trial.
@param Dataset : String
@param trial : String , it is the trial number. 
return dictionary of: 
           max the number of rule,
           min error rate for the training data,
           min error rate for the test data
"""
def SummaryOneTrial(Dataset, trial):
    df_FUN = pd.read_csv(folder + "trial_" + trial + sep + 'FUN.csv', header = None)
    df_FUN.columns = columns
    maxRuleNum = max(df_FUN.ruleNum)
    TraError = min(df_FUN.errorRate)
    
    df_results = pd.read_csv(folder + "trial_" + trial + sep + 'results.csv')
    TstError = min(df_results.test)
    
    return {"ruleNum" : maxRuleNum, "TraError" : TraError, "TstError" : TstError}

# make trial number rr = {0,1,2}, trial = {0,1,...9}
trial = [str(rr) + str(cc) for rr in range(3) for cc in range(10)]

results = list(map(lambda x : SummaryOneTrial(Dataset, x), trial))

ruleNum = statistics.mean([results[trial]["ruleNum"] for trial in range(len(results))])

TraError = statistics.mean([results[trial]["TraError"] for trial in range(len(results))])

TstError = statistics.mean([results[trial]["TstError"] for trial in range(len(results))])


# ----print result ----
print("Average the number of rule : " + str(ruleNum))

print("Average error rate for the training data : " + str(TraError))

print("Average error rate for the test data : " + str(TstError))




