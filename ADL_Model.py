# -*- coding: utf-8 -*-
"""
@author: Jessica Maggioni 845389
@author: Giorgia Rigamonti 844619
"""
import zipfile
import json
import tensorflow as tf
#import numpy as np
import pandas as pd
from tensorflow import keras
from tensorflow.keras import layers
from tensorflow.keras.models import load_model
from tensorflow.keras.layers.experimental.preprocessing import Normalization
from tensorflow.keras.layers.experimental.preprocessing import CategoryEncoding

     

file_name = 'MOTIONSENSE'
#apertura dell'archivio per estrazione
with zipfile.ZipFile(file_name + '.zip') as myzip:
    with myzip.open(file_name + '.json') as myfile:
        #lettura e conversione da bytecode a str e eliminazione degli spazi(\r\n)
        r = myfile.read().decode('utf-8').strip() 
           


fieldsToDelete = ['db_id','type','sensors','config_id', 'gyroscope']
 


#alternativa senza zip
#with open(file_path) as f:
 #   r = f.read() #estrae la stringa del file .json


#crea una lista di dizionari, uno per ogni elemento della stringa letta dal file .json
data = json.loads(r) #type(data) = list

#elimina i campi inutilizzati, mantiene solo _id, label, accelerometer 
for data_item in data: 
    for i in fieldsToDelete:
        del data_item[i] 

"""
#DICT_TO_DATAFRAME breve
df = pd.DataFrame()

for data_dict in data:
    df_1 = pd.DataFrame.from_dict(data_dict['accelerometer'])
    df_1.columns = ['acc_x', 'acc_y', 'acc_z']
    label_list = []
    id_list = []

    for i in range(0,len(data_dict['accelerometer'])):
        label_list.append(data_dict['label'])
        id_list.append(data_dict['_id']['$oid'])

    indx=df_1.shape[1]
    df_1.insert(loc = indx, column = 'label', value = label_list)
    df_1.insert(loc = indx+1, column = 'id', value = id_list)
    df= df.append(df_1)
"""


"""   
#DICT_TO_DATAFRAME giusto


acc_x = []
acc_y = []
acc_z = []
label_list = []
id_list = []

for item_data in data:
    for item in item_data['accelerometer']:
        acc_x.append(item[0])
        acc_y.append(item[1])
        acc_z.append(item[2])
        
dict = {'Acc_x': acc_x,
        'Acc_y': acc_y,
        'Acc_z': acc_z
        }

# creating a dataframe from list
df = pd.DataFrame.from_dict(dict)


for data_dict in data:   
    for i in range(0,len(data_dict['accelerometer'])):
        label_list.append(data_dict['label'])
        id_list.append(data_dict['_id']['$oid'])
        
indx = df.shape[1]
df.insert(loc = indx, column = 'label', value = label_list)
df.insert(loc = indx+1, column = 'id', value = id_list)
"""





#accesso ad una sola componente del dizionario: data[indice][dictKey]

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







dataframe = df


dataframe.shape
dataframe.head()
val_dataframe = dataframe.sample(replace = False, random_state = 1337, axis = 0)

train_dataframe = dataframe.drop(val_dataframe.index)


print("Using %d samples for training and %d for validation"
    % (len(train_dataframe), len(val_dataframe)))


def dataframe_to_dataset(dataframe):
    dataframe = dataframe.copy()
    labels = dataframe.pop('label')
    dataframe.pop('id')
    ds = tf.data.Dataset.from_tensor_slices((dict(dataframe), labels))
    ds = ds.shuffle(buffer_size=len(dataframe))
    return ds

train_ds = dataframe_to_dataset(train_dataframe)
val_ds = dataframe_to_dataset(val_dataframe)

for x, y in train_ds.take(2):
    print("Input:\n", x)
    print()
    print("Activity:\n", y)
    print()



train_ds = train_ds.batch(32)
val_ds = val_ds.batch(32)


def encode_numerical_feature(feature, name, dataset):
    # Create a Normalization layer for our feature
    normalizer = Normalization()

    # Prepare a Dataset that only yields our feature
    feature_ds = dataset.map(lambda x, y: x[name])
    feature_ds = feature_ds.map(lambda x: tf.expand_dims(x, -1))

    # Learn the statistics of the data
    normalizer.adapt(feature_ds)

    # Normalize the input feature
    encoded_feature = normalizer(feature)
    return encoded_feature


"""
def encode_string_categorical_feature(feature, name, dataset):
# Create a StringLookup layer which will turn strings into integer indices
    index = StringLookup()

# Prepare a Dataset that only yields our feature
    feature_ds = dataset.map(lambda x, y: x[name])
    feature_ds = feature_ds.map(lambda x: tf.expand_dims(x, -1))

# Learn the set of possible string values and assign them a fixed integer index
    index.adapt(feature_ds)

# Turn the string input into integer indices
    encoded_feature = index(feature)

# Create a CategoryEncoding for our integer indices
    encoder = CategoryEncoding(output_mode = "binary")

# Prepare a dataset of indices
    feature_ds = feature_ds.map(index)

# Learn the space of possible indices
    encoder.adapt(feature_ds)

# Apply one-hot encoding to our indices
    encoded_feature = encoder(encoded_feature)
    return encoded_feature

"""

#Categorical features encoded as float64

acc_x = keras.Input(shape=(1,), name = 'acc_x', dtype = 'float64')
acc_y = keras.Input(shape=(1,), name = 'acc_y', dtype = 'float64')
acc_z = keras.Input(shape=(1,), name = 'acc_z', dtype = 'float64')


# Categorical feature encoded as string
activity = keras.Input(shape=(1,), name = 'label', dtype = 'string')



all_inputs = [
    acc_x,
    acc_y,
    acc_z,
    #activity
]

# Integer categorical features
acc_x_encoded = encode_numerical_feature(acc_x, 'acc_x', train_ds)
acc_y_encoded = encode_numerical_feature(acc_y, 'acc_y', train_ds)
acc_z_encoded = encode_numerical_feature(acc_z, 'acc_z', train_ds)



# String categorical features
#activity_encode = encode_string_categorical_feature(activity, 'activity', train_ds)
all_features = layers.concatenate(
    [
        acc_x_encoded,
        acc_y_encoded,
        acc_z_encoded,
        #activity_encode
    ]
)

x = layers.Dense(32, activation = 'sigmoid')(all_features)

x = layers.Dropout(0.5)(x)

output = layers.Dense(6, activation = 'sigmoid')(x)

model = keras.Model(all_inputs, output)

model.compile('SGD', 'categorical_crossentropy', metrics = ["accuracy"])

#keras.utils.plot_model(model, show_shapes = True, rankdir = 'LR')

model.save('./model.tf')
#model.fit(train_ds, epochs=50, validation_data = val_ds)



#chiusura del file .zip
#myzip.close()