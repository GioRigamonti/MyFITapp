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
label_num_list = []

for item_data in data:
    for item in item_data['accelerometer']:
        acc_x_list.append(item[0])
        acc_y_list.append(item[1])
        acc_z_list.append(item[2])
        label_list.append(item_data['label'])
        """#label numeriche
        if (item_data['label'] =='stairs down'):
                label_num_list.append(int(1));
        elif (item_data['label'] == 'jogging'):
                label_num_list.append(int(2))
        elif (item_data['label'] == 'sitting'):
                label_num_list.append(int(3))
        elif (item_data['label'] == 'standing'):
                label_num_list.append(int(4))
        elif (item_data['label'] == 'stairs up'):
                label_num_list.append(int(5))
        elif (item_data['label'] == 'walking'):
                label_num_list.append(int(6))
        """
        
dict_acc={'acc_x': acc_x_list, 'acc_y': acc_y_list, 'acc_z': acc_z_list, 
          'label': label_list, 
          #'num_label': label_num_list
          }

# creazione di un dataset da dizionario
dataset = pd.DataFrame.from_dict(dict_acc)
"""#shuffle ds
import sklearn
dataset = sklearn.utils.shuffle(dataset)
dataset = dataset.reset_index()
dataset.drop(['index'], axis=1, inplace=True)
"""

#set dati X e y
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


"""#num_label
X = dataset[['acc_x', 'acc_y', 'acc_z']]
X = np.asarray(X)
y = dataset[['num_label']]
y = np.asarray(y)
"""
ds=np.asmatrix(dataset)
#divisione del dataset in X_train, y_train per il traning e X_test, y_test per 
#la validazione
X_train, X_test, y_train, y_test = train_test_split(
    X,
    y,
    test_size = 0.25,
    #random_state=150,
    #shuffle=True
)

"""#shuffle+iloc
import sklearn
dataset = sklearn.utils.shuffle(dataset)
dataset = dataset.reset_index()
dataset.drop(['index'], axis=1, inplace=True)


test_size = int(len(dataset) * 0.1) # the test data will be 10% (0.1) of the entire data
train = dataset.iloc[:-test_size,:].copy() 
# the copy() here is important, it will prevent us from getting: SettingWithCopyWarning: A value is trying to be set on a copy of a slice from a DataFrame. Try using .loc[row_index,col_indexer] = value instead
test = dataset.iloc[-test_size:,:].copy()
print(train.shape, test.shape)
"""

import matplotlib.pyplot as plt

"""#plot grafico
plt.figure(figsize=(50,4))
plt.plot(train.index, train['label'], label='Train', marker=',', linestyle='')
plt.plot(test.index, test['label'], label='Test', marker=',', linestyle='')
plt.legend();
"""

"""#modello1



#creazione del modello
model = Sequential(name ='model1') 
model.add(Dense(128, activation = 'relu', input_shape=(3,)))
model.add(Dropout(0.5)) 
model.add(Dense(128, activation = 'relu')) 
model.add(Dense(6, activation = 'softmax'))
model.summary()

#configurazione modello per addestramento
model.compile(loss='log_cosh', optimizer='Adam', 
              metrics=['top_k_categorical_accuracy'])

#addestramento del modello
history_callback =model.fit(
    #train,
    X_train, y_train,
                  #validation_split = 1 - 0.8, 
                  epochs = 3, 
                  batch_size = 64, verbose = 2, 
                 validation_data = (X_test, y_test),
                 #validation_data=test
                  callbacks=[cb])
history_callback.history

print("\n\n\nEvaluate on test data")
results = model.evaluate(#test,
    X_test, y_test, 
    batch_size=128)
print("\ntest loss, test acc:", results)

#creazione del modello
from tensorflow.keras.layers import AveragePooling1D, AveragePooling2D, MaxPooling1D
model = Sequential(name ='ADLmodel') 
model.add(Dense(150, activation = 'relu', input_dim=3))
#model.add(Dropout(0.5)) 
model.add(Dense(150, activation = 'relu'))
model.add(Dropout(0.5))
model.add(Dense(6, activation = 'softmax'))
model.summary()

#configurazione modello per addestramento
model.compile(loss='categorical_crossentropy', 
              optimizer='Adam', 
              metrics=['accuracy']
              )

#addestramento del modello
history = model.fit(X_train, y_train,
                   #validation_split = 1 - 0.8, 
                   epochs = 10, 
                   batch_size = 150, 
                   verbose = 2, 
                   validation_data = (X_test, y_test))
history.history

print("\n\n\nEvaluate on test data")
results = model.evaluate(#test,
    X_test, y_test, 
    batch_size=150)
print("\ntest loss, test acc:", results)
"""
print(X_train, X_test.shape, y_train, y_test.shape)
from tensorflow.keras import models
from tensorflow.keras import layers


#creazione del modello
model = Sequential(name ='model1') 
model.add(Dense(128, activation = 'relu', input_dim=3, batch_size=150))
model.add(tf.keras.layers.AveragePooling2D(pool_size=3, strides=(0,1)))
model.add(Dropout(0.5)) 
model.add(Dense(128, activation = 'relu')) 
model.add(Dense(6, activation = 'sigmoid'))
model.summary()


model.compile(loss='categorical_crossentropy', 
              optimizer='adam', 
              metrics=['accuracy'])
"""
"""
history = model.fit(X_train, y_train, 
                    validation_data=(X_test, y_test), 
                    epochs=10, 
                    batch_size=150,
                    verbose = 2)

print("\n\n\nEvaluate on test data")
results = model.evaluate(#test,
    X_test, y_test, 
    batch_size=150)
print("\ntest loss, test acc:", results)


"""#matrice confusione1
#matrice di confusione+classification_report
from sklearn.metrics import accuracy_score, confusion_matrix, precision_recall_fscore_support
import seaborn as sns
import numpy as np
#y_pred = model.predict_classes(X_test)
y_pred = np.argmax(model.predict(X_test), axis=-1)

y_test1 = y_test.argmax(axis=-1)

# Creates a confusion matrix
cm = confusion_matrix(y_test1, y_pred) 

# Transform to df for easier plotting
cm_df = pd.DataFrame(cm,
                     index   = ['stairs down', 'jogging', 'sitting', 'standing', 'stairs up', 'walking'], 
                     columns = ['stairs down', 'jogging', 'sitting', 'standing', 'stairs up', 'walking'])

plt.figure(figsize=(10,10))
sns.heatmap(cm_df, annot=True, fmt="d", linewidths=0.5, cmap='Blues', 
            cbar=False, annot_kws={'size':14}, square=True)
plt.title('Kernel \nAccuracy:{0:.3f}'.format(accuracy_score(y_test1, y_pred)))
plt.ylabel('True label')
plt.xlabel('Predicted label')
plt.show()

from sklearn.metrics import classification_report
print(classification_report(y_test1, y_pred, 
                            target_names=['stairs down', 'jogging', 'sitting', 'standing', 'stairs up', 'walking']))

"""

"""
labels=['stairs down', 'jogging', 'sitting', 'standing', 'stairs up', 'walking']
import itertools
import numpy as np
import sklearn.metrics
from sklearn.metrics import confusion_matrix

def plot_confusion_matrix(cm, classes,
                          normalize=False,
                          title='Confusion matrix',
                          cmap=plt.cm.Blues):
    if normalize:
        cm = cm.astype('float') / cm.sum(axis=1)[:, np.newaxis]

    plt.imshow(cm, interpolation='nearest', cmap=cmap)
    plt.title(title)
    plt.colorbar()
    tick_marks = np.arange(len(classes))
    plt.xticks(tick_marks, classes, rotation=90)
    plt.yticks(tick_marks, classes)

    fmt = '.2f' if normalize else 'd'
    thresh = cm.max() / 2.
    for i, j in itertools.product(range(cm.shape[0]), range(cm.shape[1])):
        plt.text(j, i, format(cm[i, j], fmt),
                 horizontalalignment="center",
                 color="white" if cm[i, j] > thresh else "black")

    plt.tight_layout()
    plt.ylabel('True label')
    plt.xlabel('Predicted label')



y_pred = model.predict(X_test)
# calculate overall accuracty of the model
accuracy = sklearn.metrics.accuracy_score(y_true=y_test, y_pred = y_pred)
# store accuracy in results
print('---------------------')
print('|      Accuracy      |')
print('---------------------')
print('\n    {}\n\n'.format(accuracy))
    
    
    # confusion matrix
cm = sklearn.metrics.confusion_matrix(y_test, y_pred)
print('--------------------')
print('| Confusion Matrix |')
print('--------------------')
print('\n {}'.format(cm))
        
    # plot confusin matrix
plt.figure(figsize=(8,8))
plt.grid(b=False)
plot_confusion_matrix(cm, classes=labels, normalize=True, title='Normalized confusion matrix', cmap = plt.cm.Greens)
plt.show()
    
     # get classification report
print('-------------------------')
print('| Classifiction Report |')
print('-------------------------')
classification_report = sklearn.metrics.classification_report(y_test, y_pred)
    # store report in results
print(classification_report)
"""
    

"""#conversione tflite
# Convert the model
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

# Save the model
with open('ADL_Model.tflite', 'wb') as f:
  f.write(tflite_model)
"""

# In[1]:


import tensorflow as tf
from tensorflow.keras import Sequential
from tensorflow.keras.layers import Flatten, Dense, Dropout, BatchNormalization
from tensorflow.keras.layers import Conv2D, MaxPool2D
from tensorflow.keras.optimizers import Adam
print(tf.__version__)


# In[2]:


import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler, LabelEncoder


# In[3]:


import zipfile
import json

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

dataset.head()


# In[4]:


dataset.shape


# In[5]:


dataset.info()


# In[6]:


dataset.isnull().sum()


# In[7]:


dataset['label'].value_counts()


# In[8]:


activities = dataset['label'].value_counts().index
activities


# In[9]:


walking = dataset[dataset['label']=='walking'].head(134354).copy()
sitting = dataset[dataset['label']=='sitting'].head(134354).copy()
standing = dataset[dataset['label']=='standing'].head(134354).copy()
stairs_up = dataset[dataset['label']=='stairs up'].head(134354).copy()
jogging = dataset[dataset['label']=='jogging'].head(134354).copy()
stairs_down = dataset[dataset['label']=='stairs down'].copy()

balanced_data = pd.DataFrame()
balanced_data = balanced_data.append([walking, sitting, standing, stairs_up, jogging, stairs_down])
balanced_data.shape


# In[10]:


balanced_data['label'].value_counts()


# In[11]:


balanced_data.head()


# In[12]:


label = LabelEncoder()
balanced_data['label'] = label.fit_transform(balanced_data['label'])
balanced_data.head()
string_array=label.classes_


# In[13]:


X = balanced_data[['acc_x', 'acc_y', 'acc_z']]
y = balanced_data['label']


# In[14]:


scaler = StandardScaler()
X = scaler.fit_transform(X)

scaled_X = pd.DataFrame(data = X, columns = ['acc_x', 'acc_y', 'acc_z'])
scaled_X['label'] = y.values

scaled_X.head()


# In[15]:


import scipy.stats as stats


# In[16]:


Fs = 50
frame_size = Fs*3 # 150
hop_size = Fs*2 # 100


# In[17]:


def get_frames(df, frame_size, hop_size):

    N_FEATURES = 3

    frames = []
    labels = []
    for i in range(0, len(df) - frame_size, hop_size):
        x = df['acc_x'].values[i: i + frame_size]
        y = df['acc_y'].values[i: i + frame_size]
        z = df['acc_z'].values[i: i + frame_size]
        
        # Retrieve the most often used label in this segment
        label = stats.mode(df['label'][i: i + frame_size])[0][0]
        frames.append([x, y, z])
        labels.append(label)

    # Bring the segments into a better shape
    frames = np.asarray(frames).reshape(-1, frame_size, N_FEATURES)
    labels = np.asarray(labels)

    return frames, labels

X, y = get_frames(scaled_X, frame_size, hop_size)

X.shape, y.shape


# In[18]:


134354*6/100


# In[19]:


X_train, X_test, y_train, y_test = train_test_split(X, y, test_size = 0.25, random_state = 0, stratify = y)


# In[20]:


X_train.shape, X_test.shape, y_train.shape, y_test.shape


# In[21]:


print(X_train.shape[0], X_train.shape[1], X_train.shape[2])
print(X_test.shape[0], X_test.shape[1], X_test.shape[2])

X_train = X_train.reshape(X_train.shape[0], X_train.shape[1], X_train.shape[2], 1)
X_test = X_test.reshape(X_test.shape[0], X_test.shape[1], X_test.shape[2], 1)


# In[22]:


X_train[0].shape, X_test[0].shape


# In[23]:


model = Sequential()
model.add(Conv2D(32, (2, 2), activation = 'relu', input_shape = X_train[0].shape))
model.add(MaxPool2D((2, 1)))
#model.add(Dropout(0.1))

model.add(Conv2D(64, (2, 2), activation='relu'))
model.add(Dropout(0.2))

model.add(Flatten())

model.add(Dense(128, activation = 'relu'))
#model.add(Dropout(0.5))

model.add(Dense(6, activation='softmax'))
"""
model = Sequential()
model.add(Conv2D(128, (12, 1), input_shape = X_train[0].shape, activation='relu', padding="valid"))
model.add(MaxPool2D((2, 1), (2, 1)))
model.add(Conv2D(128, (8, 1), activation='relu', padding="valid"))
model.add(MaxPool2D((2, 1), (2, 1)))
model.add(Conv2D(256, (4, 1), activation='relu', padding="valid"))
model.add(MaxPool2D((2, 1), (2, 1)))
model.add(Dropout(0.2))
model.add(Flatten())
model.add(Dense(128, activation='relu'))
model.add(Dense(128, activation='relu'))
model.add(Dense(6, activation='softmax'))
"""


# In[24]:


model.compile(optimizer='Adam', loss = 'sparse_categorical_crossentropy', metrics = ['accuracy'])

epochs=100
callback = tf.keras.callbacks.EarlyStopping(monitor='loss', patience=2)

from datetime import datetime

train_start_time = datetime.now()
print('training the model..')
history = model.fit(X_train, y_train, epochs = epochs, validation_data= (X_test, y_test), verbose=2, callbacks=[callback])
#history=model.fit(X_train, y_train, validation_split=1 - 0.8, epochs=epochs, batch_size=64, verbose=2, callbacks=[callback])
train_end_time = datetime.now()
print('Done \n \n')
print('training_time(HH:MM:SS.ms) - {}\n\n'.format(train_end_time - train_start_time))


# In[25]:


def plot_learningCurve(history, epochs):
  # Plot training & validation accuracy values
  epoch_range = range(1, epochs+1)
  plt.plot(epoch_range, history.history['accuracy'])
  plt.plot(epoch_range, history.history['val_accuracy'])
  plt.title('Model accuracy')
  plt.ylabel('Accuracy')
  plt.xlabel('Epoch')
  plt.legend(['Train', 'Val'], loc='upper left')
  plt.show()

  # Plot training & validation loss values
  plt.plot(epoch_range, history.history['loss'])
  plt.plot(epoch_range, history.history['val_loss'])
  plt.title('Model loss')
  plt.ylabel('Loss')
  plt.xlabel('Epoch')
  plt.legend(['Train', 'Val'], loc='upper left')
  plt.show()

plot_learningCurve(history, len(history.history['loss']))


# In[26]:


from mlxtend.plotting import plot_confusion_matrix
from sklearn.metrics import confusion_matrix


# In[27]:


print('Predicting test data')
test_start_time = datetime.now()
y_pred = np.argmax(model.predict(X_test), axis=-1)
test_end_time = datetime.now()
print('Done \n \n')
print('testing time(HH:MM:SS:ms) - {}\n\n'.format(test_end_time - test_start_time))


# In[28]:


# calculate overall accuracty of the model
from sklearn import metrics
accuracy = metrics.accuracy_score(y_true=y_test, y_pred=y_pred)
# store accuracy in results
print('---------------------')
print('|      Accuracy      |')
print('---------------------')
print('\n    {}\n\n'.format(accuracy))


# In[29]:


mat = confusion_matrix(y_test, y_pred)
plot_confusion_matrix(conf_mat=mat, class_names=label.classes_, show_normed=True, show_absolute=True, figsize=(7,7), colorbar=True)


# In[30]:


# get classification report
print('-------------------------')
print('| Classifiction Report |')
print('-------------------------')
classification_report = metrics.classification_report(y_test, y_pred, target_names=string_array)
print(classification_report)