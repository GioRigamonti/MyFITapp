# -*- coding: utf-8 -*-
"""
@author: Jessica Maggioni 845389
@author: Giorgia Rigamonti 844619
"""

import json
file_path= './MOTIONSENSE.json'
fieldsToDelete = ['db_id','type','sensors','config_id']

with open(file_path) as f:
    r = f.read() #estrae la stringa del file .json

#crea una lista di dizionari, uno per ogni elemento della stringa letta dal file .json
data = json.loads(r) #type(data) = list

#elimina i campi inutilizzati, mantiene solo _id, label, accelerometer, gyroscope
for data_item in data: 
    for i in fieldsToDelete:
        del data_item[i] 

#accesso ad una sola componente del dizionario: data[indice][dictKey]