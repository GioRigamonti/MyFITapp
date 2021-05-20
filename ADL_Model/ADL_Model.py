# -*- coding: utf-8 -*-
"""
@author: Jessica Maggioni 845389
@author: Giorgia Rigamonti 844619
"""

import zipfile
import json

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import scipy.stats as stats
import matplotlib.colors as mcolors

import tensorflow as tf
from tensorflow.keras import Sequential
from tensorflow.keras.layers import Flatten, Dense, Dropout, Conv2D
from tensorflow.keras.optimizers import Adam

from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler, LabelEncoder
from mlxtend.plotting import plot_confusion_matrix
from sklearn import metrics


file_name = 'MOTIONSENSE'
# apertura dell'archivio per estrazione
with zipfile.ZipFile(file_name + '.zip') as myzip:
    with myzip.open(file_name + '.json') as myfile:
        # lettura e conversione da bytecode a str e eliminazione degli spazi(\r\n)
        r = myfile.read().decode('utf-8').strip() 

# chiusura del file .zip
myzip.close()
     
fieldsToDelete = ['_id', 'db_id', 'type', 'sensors', 'config_id', 'gyroscope']
 
# crea una lista di dizionari, uno per ogni elemento della stringa letta dal file .json
json_data = json.loads(r) # type(data) = list

# estrazione della frequenza dal dataset
freq_acc=[]
for item_data in json_data:
    for item in item_data['sensors']:
        if (item['type'] == 'accelerometer'):
            freq_acc.append(item['frequency'])
freq_ds = stats.mode(freq_acc)[0][0]

# elimina i campi inutilizzati, mantiene solo label, accelerometer 
for data_item in json_data: 
    for i in fieldsToDelete:
        del data_item[i] 

# creazione dizionario per il dataframe
acc_x_list = []
acc_y_list = []
acc_z_list = []
label_list = []

for item_data in json_data:
    for item in item_data['accelerometer']:
        acc_x_list.append(item[0])
        acc_y_list.append(item[1])
        acc_z_list.append(item[2])
        label_list.append(item_data['label'])
        
dict_acc={'acc_x': acc_x_list, 'acc_y': acc_y_list, 'acc_z': acc_z_list, 
          'label': label_list}

# creazione di un dataset da dizionario
data = pd.DataFrame.from_dict(dict_acc)
data['label'].value_counts()
activities = data['label'].value_counts().index

# creazione di un grafico per mostrare quanti campioni sono presenti per ogni attività
data['label'].value_counts().plot(kind='bar',
                                color= mcolors.TABLEAU_COLORS,
                                title='Suddivisione dati MOTIONSENSE per Activity')
plt.show()
"""
# creazione di un grafico per vedere l'andamento dell'accelerometro in ogni attività
def plot_activity(activity, df):
    dataframe = df[df['label'] == activity][['acc_x', 'acc_y', 'acc_z']][:200]
    axis = dataframe.plot(subplots=True, figsize=(16, 12), 
                     title=activity)
    for ax in axis:
        ax.legend(loc='lower left', bbox_to_anchor=(1.0, 0.5))

        
for i in activities:
    plot_activity(i, data)
"""

# bilanciamento dei dati
activity_min = data['label'].value_counts().min()

walking = data[data['label']=='walking'].head(activity_min).copy()
sitting = data[data['label']=='sitting'].head(activity_min).copy()
standing = data[data['label']=='standing'].head(activity_min).copy()
stairs_up = data[data['label']=='stairs up'].head(activity_min).copy()
jogging = data[data['label']=='jogging'].head(activity_min).copy()
stairs_down = data[data['label']=='stairs down'].head(activity_min).copy()

balanced_data = pd.DataFrame()
balanced_data = balanced_data.append([walking, sitting, standing, stairs_up, 
                                      jogging, stairs_down])

# conversione delle label in notazione numerica
# 'jogging' = 0
# 'sitting' = 1
# 'stairs down' = 2
# 'stairs up' = 3
# 'standing' = 4
# 'walking'= 5
label = LabelEncoder()
balanced_data['label'] = label.fit_transform(balanced_data['label'])
balanced_data.head()
string_array=label.classes_

# divisione dei dati in input e output
X = balanced_data[['acc_x', 'acc_y', 'acc_z']]
y = balanced_data['label']

# standardizzazione dei dati
stand_data = StandardScaler()
X = stand_data.fit_transform(X)

stand_X = pd.DataFrame(data = X, columns = ['acc_x', 'acc_y', 'acc_z'])
stand_X['label'] = y.values


# suddivisione dei frame in base alla frequenza
# suppondendo la frequenza sia 50Hz, si considerano 150 osservazioni per 
# finestra e 100 di overlap
Fs = freq_ds
frame_size = Fs*3
ov_size = Fs*2

def dividi_frames(df, frame_size, ov_size):

    N_FEATURES = 3

    frames = []
    labels = []
    for i in range(0, len(df) - frame_size, ov_size):
        # divisione in frame da 150
        x = df['acc_x'].values[i: i + frame_size] 
        y = df['acc_y'].values[i: i + frame_size]
        z = df['acc_z'].values[i: i + frame_size]
        
        # Selezione della label del frame più usata
        label = stats.mode(df['label'][i: i + frame_size])[0][0]
        frames.append([x, y, z])
        labels.append(label)

    # Reshape del frame e conversione in array
    frames = np.asarray(frames).reshape(-1, frame_size, N_FEATURES)
    labels = np.asarray(labels)

    return frames, labels

X, y = dividi_frames(stand_X, frame_size, ov_size)


# suddivisione dei dati tra TRAIN e TEST
test_size = 0.25
random_state = 0
X_train, X_test, y_train, y_test = train_test_split(X, y, 
                                                    test_size = test_size, 
                                                    random_state = random_state, 
                                                    stratify = y)

# ridimensionamento di X_train e X_test 
X_train = X_train.reshape(X_train.shape[0], X_train.shape[1], X_train.shape[2], 1)
X_test = X_test.reshape(X_test.shape[0], X_test.shape[1], X_test.shape[2], 1)

# creazione del modello
model = Sequential(name = 'ADL_Model')
model.add(Conv2D(32, (2, 2), activation = 'relu', input_shape = X_train[0].shape))
model.add(Dropout(0.1))

model.add(Conv2D(64, (2, 2), activation='relu'))
model.add(Dropout(0.2))

model.add(Flatten())

model.add(Dense(128, activation = 'relu'))
model.add(Dropout(0.5))

model.add(Dense(6, activation='softmax'))

# addestramento del modello
model.compile(optimizer=Adam(learning_rate = 0.001), loss = 'sparse_categorical_crossentropy', metrics = ['accuracy'])
model.summary()

# training del modello
epochs=100
callback = tf.keras.callbacks.EarlyStopping(monitor='loss', patience=2)
print('\n\ntraining del modello..\n')
history = model.fit(X_train, y_train, epochs = epochs, validation_data= (X_test, y_test), verbose=2, callbacks=[callback])

# stampa del grafico del training
def plot_grafico_training(history, epochs):
  # Plot training & valori della validation accuracy 
  epoch_range = range(1, epochs+1)
  plt.plot(epoch_range, history.history['accuracy'])
  plt.plot(epoch_range, history.history['val_accuracy'])
  plt.title('ADL_Model accuracy')
  plt.ylabel('Accuracy')
  plt.xlabel('Epoche')
  plt.legend(['Train', 'Val'], loc='upper left')
  plt.show()

  # Plot training & valori della validation loss
  plt.plot(epoch_range, history.history['loss'])
  plt.plot(epoch_range, history.history['val_loss'])
  plt.title('ADL_Model loss')
  plt.ylabel('Loss')
  plt.xlabel('Epoche')
  plt.legend(['Train', 'Val'], loc='upper left')
  plt.show()

plot_grafico_training(history, len(history.history['loss']))

# generazione delle previsioni
y_pred = np.argmax(model.predict(X_test), axis=-1)

# calcolo dell'accuracy del modello
accuracy = metrics.accuracy_score(y_true=y_test, y_pred=y_pred)
print('Accuracy:\n    {}\n\n'.format(accuracy))

# creazione della matrice di confusione
mat = metrics.confusion_matrix(y_test, y_pred)
plot_confusion_matrix(conf_mat=mat, class_names=label.classes_, 
                      show_normed=True, show_absolute=True, figsize=(7,7), 
                      colorbar=True)

# generazione del report del modello
classification_report = metrics.classification_report(y_test, y_pred, target_names=string_array)
print('classification report:\n\n' + classification_report)

# conversione del modello
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

# salvataggio del modello
with open('ADL_Model.tflite', 'wb') as f:
  f.write(tflite_model)