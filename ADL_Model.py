# -*- coding: utf-8 -*-
"""
@author: Jessica Maggioni 845389
@author: Giorgia Rigamonti 844619
"""
import zipfile
import json


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

#chiusura del file .zip
myzip.close()