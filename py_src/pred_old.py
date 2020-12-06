# coding=utf-8
import os
import sys
import numpy as np
from sklearn.neighbors import KNeighborsClassifier
from sklearn.naive_bayes import GaussianNB
from sklearn.linear_model import LogisticRegression
from sklearn import svm,metrics
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.externals import joblib
import readData
import xlwt
def cos_sim(vector_a, vector_b):
    vector_a = np.mat(vector_a)
    vector_b = np.mat(vector_b)
    num = float(vector_a * vector_b.T)
    denom = np.linalg.norm(vector_a) * np.linalg.norm(vector_b)
    cos = num / denom
    # normalization to [0,1]
    sim = 0.5 + 0.5 * cos
    return sim

def load_pred(model,train_path,x_test,len_data_test,sheet1,line_num,method):
    if (not os.path.exists(model)):
        train(train_path)
    clf = joblib.load(model)
    clf_pred = clf.predict(x_test)
    clf_proba = clf.predict_proba(x_test)

    sheet1.write(line_num, 0, method)
    # sheet2.write(line_num, 0, method)
    for jj in range(len_data_test):
        sheet1.write(line_num, jj+1, str(clf_pred[jj]) + '(' + str(round(max(clf_proba[jj]), 3)) + ')')
    # for jj in range(1, len_data_test):
    #     sheet2.write(line_num, jj, cos_sim(clf_proba[jj], clf_proba[0]))
    line_num += 1
    return sheet1,line_num

def func(train_path,test_file):
    #readdata
    len_train_file = len(os.listdir(train_path))
    dirs = os.listdir(train_path)

    # write
    try:
        file = xlwt.Workbook()
        sheet1 = file.add_sheet('predict', cell_overwrite_ok=True)
        sheet2 = file.add_sheet('similarity', cell_overwrite_ok=True)
    except IOError:
        return "Error: Failed to create file"

    score_result = [[0 for i in range(6)] for j in range(len_train_file)]
    specialAlleleList = ['DYS385', 'DYF387S1']

    alleleGroupName_test, alleleList_test, data_test= readData.readExcelData(test_file, specialAlleleList,SHUFFLE=False)

    result = [[] for _ in range(len_train_file)]
    line_num = 0
    for file_num in range(1):
        sheet1.write(line_num, 0, dirs[file_num])
        sheet2.write(line_num, 0, dirs[file_num])
        line_num+=1

        train_file = os.path.join(train_path,dirs[file_num])
        print(train_file)
        alleleGroupName, alleleList, alleleData,data,groupName,shuffleIndex = readData.readExcelData(train_file, specialAlleleList,SHUFFLE=True)
        compare_result = [[] for i in range(6)]
        original_compare_result = [[] for i in range(len(data))]

        loop_num = 1
        x_train = alleleData
        x_test = data_test
        y_train = alleleGroupName
        unique_group = np.unique(y_train)

        #knn
        knn = KNeighborsClassifier(1).fit(x_train, y_train)
        knn_pred = knn.predict(x_test)
        knn_proba = knn.predict_proba(x_test)
        result[file_num].append(knn_pred)
        for jj in range(len(data_test)):
            # sheet1.write(line_num, jj, knn_pred[jj])
            sheet1.write(line_num, jj, str(knn_pred[jj])+'('+str(round(max(knn_proba[jj]),3))+')')
        for jj in range(1,len(data_test)):
            sheet2.write(line_num,jj-1,cos_sim(knn_proba[jj],knn_proba[0]))
        line_num+=1

        #naiveBayes
        gnb = GaussianNB().fit(x_train, y_train)
        # predict_score = gnb.predict_proba(x_test)
        gbn_pred = gnb.predict(x_test)
        gbn_proba = gnb.predict_proba(x_test)
        result[file_num].append(gbn_pred)
        for jj in range(len(data_test)):
            sheet1.write(line_num, jj, str(gbn_pred[jj])+'('+str(round(max(gbn_proba[jj]),3))+')')
        for jj in range(1,len(data_test)):
            sheet2.write(line_num,jj-1,cos_sim(gbn_proba[jj],gbn_proba[0]))
        line_num += 1
        #logisticRegression
        lr = LogisticRegression(penalty="l2", C=1, multi_class="ovr", solver="newton-cg",max_iter=1000).fit(x_train, y_train)
        # print(lr.predict_proba(x_test))
        lr_pred = lr.predict(x_test)
        lr_proba = lr.predict_proba(x_test)
        result[file_num].append(lr_pred)
        for jj in range(len(data_test)):
            sheet1.write(line_num, jj, str(lr_pred[jj])+'('+str(round(max(lr_proba[jj]),3))+')')
        for jj in range(1,len(data_test)):
            sheet2.write(line_num,jj-1,cos_sim(lr_proba[jj],lr_proba[0]))
        line_num += 1

        #SVM
        svm_svm = svm.SVC(probability = True)
        svm_svm.fit(x_train, y_train)
        svm_pred = svm_svm.predict(x_test)
        svm_proba = svm_svm.predict_proba(x_test)
        result[file_num].append(svm_pred)
        for jj in range(len(data_test)):
            sheet1.write(line_num, jj, str(svm_pred[jj])+'('+str(round(max(svm_proba[jj]),3))+')')
        for jj in range(1,len(data_test)):
            sheet2.write(line_num,jj-1,cos_sim(svm_proba[jj],svm_proba[0]))
        line_num += 1
        #decesionTree
        dt = DecisionTreeClassifier(criterion='entropy')
        dt.fit(x_train, y_train)
        dt_pred = dt.predict(x_test)
        dt_proba = dt.predict_proba(x_test)
        result[file_num].append(dt_pred)
        for jj in range(len(data_test)):
            sheet1.write(line_num, jj, str(svm_pred[jj])+'('+str(round(max(dt_proba[jj]),3))+')')
            # sheet1.write(line_num, jj, svm_pred[jj])
        for jj in range(1,len(data_test)):
            sheet2.write(line_num,jj-1,cos_sim(dt_proba[jj],dt_proba[0]))
        line_num += 1

        #randomForest
        rf = RandomForestClassifier(n_estimators=170)
        rf.fit(x_train, y_train)
        rf_pred = rf.predict(x_test)
        rf_proba = rf.predict_proba(x_test)
        result[file_num].append(rf_pred)
        for jj in range(len(data_test)):
            sheet1.write(line_num, jj, str(rf_pred[jj])+'('+str(round(max(rf_proba[jj]),3))+')')
        for jj in range(1,len(data_test)):
            sheet2.write(line_num,jj-1,cos_sim(rf_proba[jj],rf_proba[0]))
        line_num += 1
        loop_num+=1

    try:
        file.save('output//Result_'+ train_path.split('\\')[-1]+'_'+test_file.split('\\')[-1].split('.')[0]+'.xlsx')
    except IOError:
        return "Error: File not found or failed to read "
    else:
        print("'result.xlsx' has been written.")
        return "'result.xlsx' has been written."

def train(train_path):
    len_train_file = len(os.listdir(train_path))
    dirs = os.listdir(train_path)
    specialAlleleList = ['DYS385', 'DYF387S1']

    for file_num in range(len_train_file):
        train_file = os.path.join(train_path,dirs[file_num])
        save_model_path ='output/train_model/'+ train_path.split('\\')[-1]+'/';
        if not os.path.exists(save_model_path):
            os.makedirs(save_model_path)
        alleleGroupName, alleleList, alleleData,data,groupName,shuffleIndex = readData.readExcelData(train_file, specialAlleleList,SHUFFLE=True)

        x_train = alleleData
        y_train = alleleGroupName

        #knn
        knn = KNeighborsClassifier(1).fit(x_train, y_train)
        joblib.dump(knn, save_model_path+dirs[file_num].split('.')[0]+'_knn.model')

        #naiveBayes
        gnb = GaussianNB().fit(x_train, y_train)
        joblib.dump(gnb, save_model_path+dirs[file_num].split('.')[0]+'_naiveBayes.model')

        #logisticRegression
        lr = LogisticRegression(penalty="l2", C=1, multi_class="ovr", solver="newton-cg",max_iter=1000).fit(x_train, y_train)
        joblib.dump(lr, save_model_path+dirs[file_num].split('.')[0]+'_logisticRegression.model')

        #SVM
        svm_svm = svm.SVC(probability = True).fit(x_train, y_train)
        joblib.dump(svm_svm, save_model_path+dirs[file_num].split('.')[0]+'_svm.model')

        #decesionTree
        dt = DecisionTreeClassifier(criterion='entropy').fit(x_train, y_train)
        joblib.dump(dt, save_model_path+dirs[file_num].split('.')[0]+'_decesionTree.model')

        #randomForest
        rf = RandomForestClassifier(n_estimators=170).fit(x_train, y_train)
        joblib.dump(rf, save_model_path+dirs[file_num].split('.')[0]+'_randomForest.model')

def test(train_path,test_file):
    len_train_file = len(os.listdir(train_path))
    dirs = os.listdir(train_path)
    save_model_path = 'output/train_model/' + train_path.split('\\')[-1] + '/'
    save_result = 'output//Result_'+ train_path.split('\\')[-1]+'_'+test_file.split('\\')[-1].split('.')[0]+'.xlsx'
    try:
        file = xlwt.Workbook()
        sheet1 = file.add_sheet('predict', cell_overwrite_ok=True)
        # sheet2 = file.add_sheet('similarity', cell_overwrite_ok=True)
    except IOError:
        return "Error: Failed to create file"

    specialAlleleList = ['DYS385', 'DYF387S1']
    sampleName_test,alleleGroupName_test, alleleList_test, data_test= readData.readExcelData(test_file, specialAlleleList,SHUFFLE=False)

    line_num = 0
    for file_num in range(len_train_file):
        sheet1.write(line_num, 0, dirs[file_num])
        line_num+=1
        # sheet2.write(line_num, 0, dirs[file_num])
        for data_num in range(len(data_test)):
            sheet1.write(line_num, data_num+1,str(sampleName_test[data_num]))
            # sheet2.write(line_num, data_num+1, str(sampleName_test[data_num]))
        line_num+=1
        x_test = data_test

        #knn
        model = os.path.join(save_model_path,dirs[file_num].split('.')[0]+'_knn.model')
        sheet1, line_num=load_pred(model,train_path,x_test,len(data_test),sheet1,line_num,"knn")

        #naiveBayes
        model = os.path.join(save_model_path,dirs[file_num].split('.')[0]+'_naiveBayes.model')
        sheet1, line_num=load_pred(model,train_path,x_test,len(data_test),sheet1,line_num,"naiveBayes")

        #logisticRegression
        model = os.path.join(save_model_path, dirs[file_num].split('.')[0] + '_logisticRegression.model')
        sheet1, line_num=load_pred(model,train_path,x_test,len(data_test),sheet1,line_num,"logisticRegression")

        #SVM
        model = os.path.join(save_model_path, dirs[file_num].split('.')[0] + '_svm.model')
        sheet1, line_num=load_pred(model,train_path,x_test,len(data_test),sheet1,line_num,"svm")

        #decesionTree
        model = os.path.join(save_model_path, dirs[file_num].split('.')[0] + '_decesionTree.model')
        sheet1, line_num=load_pred(model,train_path,x_test,len(data_test),sheet1,line_num,"decesionTree")

        #randomForest
        model = os.path.join(save_model_path, dirs[file_num].split('.')[0] + '_randomForest.model')
        sheet1, line_num=load_pred(model,train_path,x_test,len(data_test),sheet1,line_num,"randomForest")
    try:
        file.save(save_result)
    except IOError:
        return "Error: File not found or failed to read "
    else:
        print(save_result+" has been written.")
        return save_result+" has been written."

def compute(compared_file,input_file):
    save_result = 'output//Result_'+ compared_file.split('\\')[-1].split('.')[0]+'_'+input_file.split('\\')[-1].split('.')[0]+'.xlsx'
    try:
        file = xlwt.Workbook()
        sheet1 = file.add_sheet('similarity', cell_overwrite_ok=True)
    except IOError:
        return "Error: Failed to create file"

    specialAlleleList = ['DYS385', 'DYF387S1']
    sampleName_input, _, alleleList_input, data_input= readData.readExcelData(input_file, specialAlleleList,SHUFFLE=False)
    sampleName_compared, _, alleleList_compared, data_compared= readData.readExcelData(compared_file, specialAlleleList,SHUFFLE=False)

    # sheet1.write(0, 0, "")
    for i in range(len(data_compared)):
        sheet1.write(i+1, 0, sampleName_compared[i])
        for j in range(len(data_input)):
            sheet1.write(0, j+1, sampleName_input[j])
            sheet1.write(i+1, j+1, str(round(cos_sim(data_compared[i],data_input[j]), 5)))
    try:
        file.save(save_result)
    except IOError:
        return "Error: File not found or failed to read "
    else:
        print(save_result+" has been written.")
        return save_result+" has been written."

if __name__ == "__main__":
    path = "./20181021/"
    # train(".\\20181021")
    # test(".\\20181021","example_file.xlsx")
    # compute("example_file.xlsx","example_file.xlsx")
    # func(".\\20181021","example_file.xlsx")
    if(sys.argv[1]=="train"):
        train(sys.argv[2])
    elif(sys.argv[1]=="test"):
        test(sys.argv[2],sys.argv[3])
    elif(sys.argv[1]=="compute"):
        compute(sys.argv[2],sys.argv[3])







