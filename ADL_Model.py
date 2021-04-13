# -*- coding: utf-8 -*-
"""
@author: Jessica Maggioni 845389
@author: Giorgia Rigamonti 844619
"""
import zipfile
import json
import tensorflow as tf
import pandas as pd
from tensorflow import keras
from tensorflow.keras import layers
#from tensorflow.keras.models import load_model   #per caricamento modello
from tensorflow.keras.layers.experimental.preprocessing import Normalization


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

# creazione di un dataframe da dizionario
dataframe = pd.DataFrame.from_dict(dict_acc)

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
val_dataframe = dataframe.sample(replace = False, random_state = 1337, 
                                 axis = 0)
train_dataframe = dataframe.drop(val_dataframe.index)

print("Using %d samples for training and %d for validation" 
      % (len(train_dataframe), len(val_dataframe)))

#generazione del dataset a partire dal dataframe
def dataframe_to_dataset(dataframe):
    dataframe = dataframe.copy()
    labels = dataframe.pop('label')
    ds = tf.data.Dataset.from_tensor_slices((dict(dataframe), labels))
    ds = ds.shuffle(buffer_size = len(dataframe))
    return ds

train_ds = dataframe_to_dataset(train_dataframe)
val_ds = dataframe_to_dataset(val_dataframe)

#restistuzione tupla da dataset
for x, y in train_ds.take(2):
    print("Input:\n", x)
    print("Activity:", y)
    print()

#batch del dataset
train_ds = train_ds.batch(32)
val_ds = val_ds.batch(32)

#normalizzazone dei dati numerici
def encode_numerical_feature(feature, name, dataset):
    # creazione livello di normalizzazione
    normalizer = Normalization()

    # preparazione del dataset
    feature_ds = dataset.map(lambda x, y: x[name])
    feature_ds = feature_ds.map(lambda x: tf.expand_dims(x, -1))

    # apprentimento delle statistiche dei dati
    normalizer.adapt(feature_ds)

    # normalizzazione funzione in input
    encoded_feature = normalizer(feature)
    
    return encoded_feature

#codifica delle caratteristiche
acc_x = keras.Input(shape=(1,), name = 'acc_x', dtype = 'float64')
acc_y = keras.Input(shape=(1,), name = 'acc_y', dtype = 'float64')
acc_z = keras.Input(shape=(1,), name = 'acc_z', dtype = 'float64')

all_inputs = [acc_x, acc_y, acc_z]

acc_x_encoded = encode_numerical_feature(acc_x, 'acc_x', train_ds)
acc_y_encoded = encode_numerical_feature(acc_y, 'acc_y', train_ds)
acc_z_encoded = encode_numerical_feature(acc_z, 'acc_z', train_ds)

all_features = layers.concatenate([acc_x_encoded, acc_y_encoded, acc_z_encoded])

#creazione del modello
x = layers.Dense(32, activation = 'sigmoid')(all_features)
x = layers.Dropout(0.5)(x)

output = layers.Dense(1, activation = 'sigmoid')(x)

model = keras.Model(all_inputs, output)

#configurazione modello per addestramento
model.compile('SGD', 'categorical_crossentropy', metrics = ["accuracy"])

#addestramento del modello
model.fit(x = train_ds, epochs = 50, verbose = 2, validation_data = val_ds, shuffle = True)

#salvataggio modello
model.save('./model.tf')