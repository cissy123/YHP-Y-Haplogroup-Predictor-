import numpy as np
import random
import xlrd

def readExcelData(fileName, SHUFFLE=False):

    alleleData = list()
    alleleDataIndex = list()
    alleleDataNullIndex = list()
    newAlleleData = list()
    specialIndex = list()
    shuffleGroupName = list()

    ###open###
    wb = xlrd.open_workbook(fileName)
    table = wb.sheets()[0]
    # readData
    nrows = table.nrows
    for i in range(1, nrows):
        if '' not in table.row_values(i)[3:] and ' ' not in table.row_values(i)[3:]:
            alleleData.append(table.row_values(i)[3:])
            alleleDataIndex.append(i)
        else:
            alleleDataNullIndex.append(i)

    groupName = readTableElement(table.col_values(2), alleleDataIndex)
    alleleList = table.row_values(0)[3:]
    sampleName = readTableElement(table.col_values(0), alleleDataIndex)


    ##log.txt##
    if len(alleleDataNullIndex) > 0:
        log = open('log/log.txt', 'w', encoding='utf-8')
        for i in alleleDataNullIndex:
            log.write(str(i + 1) + ' : ' + table.col_values(0)[i] + '\n')
        log.close()
    ##log.txt##


    for i in range(len(groupName)):
        for j in range(len(alleleList)):
            if alleleData[i][j] in ('na', ' na', ' na '):
                alleleData[i][j] = -999
                newAlleleData.append(alleleData[i][j])
            elif type(alleleData[i][j]) == str:
                s = alleleData[i][j].split(',')

                if j in specialIndex:
                    if len(s) > 2:
                        alleleData[i][j] = -999
                        newAlleleData.append(-999)
                        newAlleleData.append(-999)
                        continue
                    newAlleleData.append(float(s[0]))
                    if s[-1] == '':
                        newAlleleData.append(float(s[0]))
                    else:
                        newAlleleData.append(float(s[-1]))
                else:
                    newAlleleData.append(float(s[0]))
            elif j in specialIndex:
                newAlleleData.append(alleleData[i][j])
                newAlleleData.append(alleleData[i][j])
            else:
                newAlleleData.append(alleleData[i][j])


    alleleNum = len(alleleList)
    data = np.array(newAlleleData).reshape(int(len(groupName)), alleleNum)

    #shuffle
    if SHUFFLE:
        cc = list(zip(data, [i for i in range(len(groupName))]))
        random.Random(len(groupName)).shuffle(cc)
        shuffleData,shuffleIndex = zip(*cc)

        for i in shuffleIndex:
            shuffleGroupName.append(groupName[i])

        return shuffleGroupName, alleleList, shuffleData,data,groupName,shuffleIndex
    else:
        return sampleName,groupName,alleleList,data


def readTableElement(table, index):
    element = list()
    for i in index:
        element.append(str(table[i]).strip())
    return element
