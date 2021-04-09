# -*- coding: utf-8 -*-
"""
@author: Jessica Maggioni 845389
@author: Giorgia Rigamonti 844619
"""
import zipfile
import json
import csv
#import tensorflow as tf
#import numpy as np
#import pandas as pd
#from tensorflow import keras
#from tensorflow.keras import layers


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

#accesso ad una sola componente del dizionario: data[indice][dictKey]

for item_data in data:
    # creazione dell'intestazione del file .csv
    item = ['accelerometer x ', 'accelerometer y ', 'accelerometer z ','activity']
    #apertura di un file in scrittura per il caricamento dei dati
    data_file = open(item_data['_id']['$oid'] + '.csv', 'w')
    with data_file:
        # creazione del csv writer object 
        csv_writer = csv.writer(data_file, dialect = 'excel')
        #scrittura dell'intestazione del file .cvs
        csv_writer.writerow(item)
        #riempimento del file .csv con tutti i dati relativi all'accelerometro
        for item in item_data['accelerometer']:
            header = item + [item_data['label']]               
            csv_writer.writerow(header)

#chiusura del file .zip
myzip.close()