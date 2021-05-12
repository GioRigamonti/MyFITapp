# -*- coding: utf-8 -*-
"""
@author: Jessica Maggioni 845389
@author: Giorgia Rigamonti 844619
"""
import zipfile
import json
import tensorflow as tf
import pandas as pd
import numpy as np
from tensorflow.keras.layers import Dense, Dropout
from tensorflow.keras.models import Sequential
from sklearn.model_selection import train_test_split
import matplotlib.pyplot as plt
from sklearn.metrics import accuracy_score, confusion_matrix, precision_recall_fscore_support
import seaborn as sns

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
X_train, X_test, y_train, y_test = train_test_split(
  X,
  y,
  test_size = 0.25
)

#creazione del modello
model = Sequential(name ='model1') 
model.add(Dense(128, activation = 'relu', input_shape=(3,)))
model.add(Dropout(0.5)) 
model.add(Dense(128, activation = 'relu')) 
model.add(tf.keras.layers.AveragePooling2D(
    pool_size = (2,2), strides = None, padding = "valid", data_format = "channels_first"))
model.add(Dense(6, activation = 'softmax'))
model.summary()

#configurazione modello per addestramento
model.compile(loss='log_cosh', optimizer='Adam', 
              metrics=['top_k_categorical_accuracy'])
#loss = 'categorical_crossentropy', optimizer = 'Adam', metrics = ['accuracy']

#addestramento del modello
history = model.fit(X_train, y_train,
                  	validation_split = 1 - 0.8, 
                  	epochs = 10, 
                  	batch_size = 64, 
			verbose = 2, 
                 	validation_data = (X_test, y_test))
history.history

print("\n\n\nEvaluate on test data")
results = model.evaluate(X_test, y_test, 
    			batch_size=128)
print("\ntest loss, test acc:", results)

#matrice di confusione+classification_report
y_pred = model.predict_classes(X_test)

y_test1 = y_test.argmax(axis=-1)

# Creates a confusion matrix
cm = confusion_matrix(y_test1, y_pred) 

# Transform to df for easier plotting
cm_df = pd.DataFrame(cm,
                     index   = ['Downstairs', 'Jogging', 'Sitting', 'Standing', 'Upstairs', 'Walking'], 
                     columns = ['Downstairs', 'Jogging', 'Sitting', 'Standing', 'Upstairs', 'Walking'])

plt.figure(figsize=(10,10))
sns.heatmap(cm_df, annot=True, fmt="d", linewidths=0.5, cmap='Blues', cbar=False, annot_kws={'size':14}, square=True)
plt.title('Kernel \nAccuracy:{0:.3f}'.format(accuracy_score(y_test1, y_pred)))
plt.ylabel('True label')
plt.xlabel('Predicted label')
plt.show()

from sklearn.metrics import classification_report
print(classification_report(y_test1, y_pred, target_names=['Downstairs', 'Jogging', 'Sitting', 'Standing', 'Upstairs', 'Walking']))

"""
# Convert the model
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

# Save the model
with open('ADL_Model.tflite', 'wb') as f:
  f.write(tflite_model)
"""