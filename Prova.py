# -*- coding: utf-8 -*-
#creazione del modello
"""
@author: Jessica Maggioni 845389
@author: Giorgia Rigamonti 844619
"""
import zipfile
import json
#import tensorflow as tf
import pandas as pd
import numpy as np
from tensorflow.keras.layers import Dense, Dropout 
#from tensorflow.keras.optimizers import RMSprop 
from tensorflow.keras.layers import Conv2D
from tensorflow.keras.layers import MaxPool2D
from tensorflow.keras.layers import Flatten
#from tensorflow.keras.layers import *
from tensorflow.keras.models import Sequential
from sklearn.model_selection import train_test_split


file_name = 'MOTIONSENSE'
#apertura dell'archivio per estrazione
with zipfile.ZipFile(file_name + '.zip') as myzip:
    with myzip.open(file_name + '.json') as myfile:
        #lettura e conversione da bytecode a str e eliminazione degli spazi(\r\n)
        r = myfile.read().decode('utf-8').strip() 

#chiusura del file .zip
myzip.close()
           
fieldsToDelete = ['db_id','type','sensors','config_id', 'gyroscope']
 
#crea una lista di dizionari, uno per ogni elemento della stringa letta dal file .json
data = json.loads(r) #type(data) = list

#elimina i campi inutilizzati, mantiene solo _id, label, accelerometer 
for data_item in data: 
    for i in fieldsToDelete:
        del data_item[i] 

#creazione dizionario per il dataframe
acc_x_list = []
acc_y_list = []
acc_z_list = []
label_list = []

for item_data in data:
    for item in item_data['accelerometer']:
        acc_x_list.append(item[0])
        acc_y_list.append(item[1])
        acc_z_list.append(item[2])
        label_list.append(item_data['label'])
        
dict_acc={'acc_x': acc_x_list, 'acc_y': acc_y_list, 'acc_z': acc_z_list, 
          'label': label_list}

# creazione di un dataset da dizionario
dataset = pd.DataFrame.from_dict(dict_acc)

"""
#alternativa per un unico file csv con tutti i dati
# creazione dell'intestazione del file .csv
item = ['accelerometer x ', 'accelerometer y ', 'accelerometer z ','activity']
#apertura di un file in scrittura per il caricamento dei dati
data_file = open(file_name + '.csv', 'w')
with data_file:
# creazione del csv writer object (in alternativa aggiungere delimiter = '\t')
    csv_writer = csv.writer(data_file, dialect = 'excel') 
    #scrittura dell'intestazione del file .cvs
    csv_writer.writerow(item)
    #riempimento del file .csv con tutti i dati relativi all'accelerometro
    for item_data in data:
        for item in item_data['accelerometer']:
            header = item + [item_data['label']]              
            csv_writer.writerow(header)
"""

# convalida e addestramento del set di dati
label = pd.get_dummies(dataset['label'])
label.columns = ['label_' + str(x) for x in label.columns]
ds = pd.concat([dataset, label], axis=1)

# drop old label
ds.drop(['label'], axis=1, inplace=True)

X = ds[['acc_x', 'acc_y', 'acc_z']]

#Conversione dei dati in un tensore
X = np.asarray(X)
y = ds[['label_stairs down', 'label_jogging',
        'label_sitting','label_standing','label_stairs up', 'label_walking']]
y = np.asarray(y)

#divisione del dataset in X_train, y_train per il traning e X_test, y_test per 
#la validazione
#X_train, X_test, y_train, y_test = train_test_split(
#  X,
#  y,
#  test_size = 0.25
#)

 
#creazione del modello
model = Sequential()
model.add(Conv2D(128, (2, 12), activation ='relu', padding ="valid"))
model.add(MaxPool2D((2, 2)))
model.add(Conv2D(128, (2, 8), activation='relu', padding ="valid"))
model.add(MaxPool2D((2, 2)))
model.add(Conv2D(256, (2, 4), activation='relu', padding ="valid"))
model.add(MaxPool2D((2, 2)))
model.add(Dropout(0.2))
model.add(Flatten())
model.add(Dense(128, activation='relu'))
model.add(Dense(128, activation='relu'))
model.add(Dense(6, activation='softmax'))
model.compile(loss ='categorical_crossentropy', optimizer ='adam', metrics = ['accuracy'])
    
model.fit(X, y, validation_split = 1 - 0.8, epochs = 10, batch_size = 64, verbose = 2)


#model.add(Dense(128, activation = 'relu', input_shape=(3,)))
#model.add(Dropout(0.5)) 
#model.add(Dense(128, activation = 'relu'))
#model.add(Dense(6, activation = 'softmax'))


#configurazione modello per addestramento
#model.compile(loss = 'categorical_crossentropy',     
#              optimizer = 'adam', 
#              metrics = ['accuracy'])

#addestramento del modello
#history = model.fit(
#   X, y, 
#   validation_split = 1 - 0.8, 
#   epochs = 10, 
#   batch_size = 64, 
#   verbose = 2
#)
   
#   batch_size = 128, 
#  epochs = 50, 
#   verbose = 2, 
#   validation_data = (X_test, y_test)
#)









