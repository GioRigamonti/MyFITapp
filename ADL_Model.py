# -*- coding: utf-8 -*-
"""
@author: Jessica Maggioni 845389
@author: Giorgia Rigamonti 844619
"""
import zipfile
import json
import csv
import tensorflow as tf
import numpy as np
import pandas as pd
from tensorflow import keras
from tensorflow.keras import layers
from tensorflow.keras.layers.experimental.preprocessing import Normalization
from tensorflow.keras.layers.experimental.preprocessing import CategoryEncoding
from tensorflow.keras.layers.experimental.preprocessing import StringLookup
     

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
#for data_item in data: 
 #   for i in fieldsToDelete:
  #      del data_item[i] 

#accesso ad una sola componente del dizionario: data[indice][dictKey]
#conversione dei dati in un file.csv
#for item_data in data:
    # creazione dell'intestazione del file .csv
 #   item = ['accelerometer x ', 'accelerometer y ', 'accelerometer z ','activity']
    #apertura di un file in scrittura per il caricamento dei dati
  #  data_file = open(item_data['_id']['$oid'] + '.csv', 'w')
   # with data_file:
        # creazione del csv writer object 
    #    csv_writer = csv.writer(data_file, dialect = 'excel')
        #scrittura dell'intestazione del file .cvs
     #   csv_writer.writerow(item)
        #riempimento del file .csv con tutti i dati relativi all'accelerometro
      #  for item in item_data['accelerometer']:
       #     header = item + [item_data['label']]               
        #    csv_writer.writerow(header)

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
DICT_TO_DATAFRAME
a=data[0]

#print(pd.DataFrame(pd.DataFrame(list(data[0].items()), columns=['a', 'b'])))

b=pd.DataFrame.from_dict(a['accelerometer'])

c = pd.Series(data=[])

for i in range(0,len(a['accelerometer'])):
    c[i]=a['label']

indx=b.shape[1]
b.insert(loc=indx,column='altra', value=c)
#b=pd.DataFrame({'a' : a.keys() , 'b' : a.values() })

print(b)
"""

#zip_name = 'MOTIONSENSE.zip'
#apertura dell'archivio per estrazione
#with zipfile.ZipFile(zip_name) as myzip:
#   file_name = myzip.namelist()[0]
#   ms = pd.read_json(file_name)
 #  ms.head()

#df = pd.DataFrame(ms.data, columns = ms.feature_names)
# Convert datatype to float
#df = df.astype(float)
# append "target" and name it "label"
#df['label'] = ms.target
# Use string label instead
#df['label'] = df.label.replace(dict(enumerate(ms.target_names)))
# label -> one-hot encoding
#label = pd.get_dummies(df['label']) #concatena 'label' al nome delle attività
#label.columns = ['label_' + str(x) for x in label.columns]
#df = pd.concat([df, label], axis=1)
# drop old label
#df.drop(['label'], axis=1, inplace=True)

# Creating X and y
#X = df[['accelerometer x ', 'accelerometer y ', 'accelerometer z ']]
# Convert DataFrame into np array
#X = np.asarray(X)
#y = df[['stairs down ', ' jogging ', 'sitting ', 'standing ', 'stairs up ', 'walking ']]
# Convert DataFrame into np array
#y = np.asarray(y)

#X_train, X_test, y_train, y_test = train_test_split(
 # X,
  #y,
  #test_size = None
#)

#print (len(X_train))
#print (len(X_test))
#print (len(y_train))
#print (len(y_test))


file_url = '5fbaa22cab33096d8049d73c.csv' #file_name + '.csv'
dataframe = pd.read_csv(file_url)

dataframe.shape
dataframe.head()
val_dataframe = dataframe.sample(replace = False)
train_dataframe = dataframe.drop(val_dataframe.index)

print(
    "Using %d samples for training and %d for validation"
    % (len(train_dataframe), len(val_dataframe)))


def dataframe_to_dataset(dataframe):
    dataframe = dataframe.copy()
    labels = dataframe.pop('activity')
    ds = tf.data.Dataset.from_tensor_slices((dict(dataframe), labels))
    ds = ds.shuffle(buffer_size=len(dataframe))
    return ds

train_ds = dataframe_to_dataset(train_dataframe)
val_ds = dataframe_to_dataset(val_dataframe)

for x, y in train_ds.take(1):
    print("Input:", x)
    print("Activity:", y)

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



def encode_string_categorical_feature(feature, name, dataset):
# Create a StringLookup layer which will turn strings into integer indices
    index = StringLookup()

# Prepare a Dataset that only yields our feature
    feature_ds = dataset.map(lambda x, y: y[name])
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


#Categorical features encoded as float64

acc_x = keras.Input(shape=(1,), name = 'accelerometer x ', dtype = 'float64')
acc_y = keras.Input(shape=(1,), name = 'accelerometer y ', dtype = 'float64')
acc_z = keras.Input(shape=(1,), name = 'accelerometer z ', dtype = 'float64')


# Categorical feature encoded as string
activity = keras.Input(shape=(1,), name = 'activity', dtype = 'string')



all_inputs = [
    acc_x,
    acc_y,
    acc_z,
    activity
]

# Integer categorical features
acc_x_encoded = encode_numerical_feature(acc_x, 'accelerometer x ', train_ds)
acc_y_encoded = encode_numerical_feature(acc_y, 'accelerometer y ', train_ds)
acc_z_encoded = encode_numerical_feature(acc_z, 'accelerometer z ', train_ds)



# String categorical features
activity_encode = encode_string_categorical_feature(activity, 'activity', train_ds)
all_features = layers.concatenate(
    [
        acc_x_encoded,
        acc_y_encoded,
        acc_z_encoded,
        activity_encode
    ]
)

x = layers.Dense(32, activation = 'relu')(all_features) 
x = layers.Dropout(0.5)(x)
output = layers.Dense(6, activation = 'sigmoid')(x)
model = keras.Model(all_inputs, output)
model.compile(optimizer = 'SGD', loss = 'binary_crossentropy', metrics = ['accuracy'])

keras.utils.plot_model(model, show_shapes = True, show_dtype = True,
                        show_layer_names = True, rankdir = 'LR')

#model.fit(train_ds, epochs=50, validation_data=val_ds)




#chiusura del file .zip
myzip.close()