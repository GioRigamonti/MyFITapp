# -*- coding: utf-8 -*-
"""
@author: Jessica Maggioni 845389
@author: Giorgia Rigamonti 844619
"""

  
from tflite_support import flatbuffers
from tflite_support import metadata as _metadata
from tflite_support import metadata_schema_py_generated as _metadata_fb
import os


# Creates model info.

model_meta = _metadata_fb.ModelMetadataT()
model_meta.name = "ADL model classifier"
model_meta.description = ("Human Action Classificator")
model_meta.author = "Jessica Maggioni"
model_meta.author = "Giorgia Rigamonti"

# Creates input info.
input_meta = _metadata_fb.TensorMetadataT()

# Creates output info.
output_meta = _metadata_fb.TensorMetadataT()

input_meta.name = "Accelerometer coordinates"
input_meta.description = ("Accelerometer coordinates as float numbers")
input_meta.content = _metadata_fb.ContentT()


# Creates output info.
output_meta = _metadata_fb.TensorMetadataT()
output_meta.name = "Labels"
output_meta.description = "Labels of Human Actions"
output_meta.content = _metadata_fb.ContentT()

label_file = _metadata_fb.AssociatedFileT()
label_file.name = os.path.basename("labels.txt")
label_file.description = "Labels for the human action detected"
label_file.type = _metadata_fb.AssociatedFileType.TENSOR_AXIS_LABELS
output_meta.associatedFiles = [label_file]


# Creates subgraph info.
subgraph = _metadata_fb.SubGraphMetadataT()
subgraph.inputTensorMetadata = [input_meta]
subgraph.outputTensorMetadata = [output_meta]
model_meta.subgraphMetadata = [subgraph]

b = flatbuffers.Builder(0)
b.Finish(
    model_meta.Pack(b),
    _metadata.MetadataPopulator.METADATA_FILE_IDENTIFIER)
metadata_buf = b.Output()

populator = _metadata.MetadataPopulator.with_model_file("./ADL_Model.tflite")
populator.load_metadata_buffer(metadata_buf)
populator.load_associated_files(["./labels.txt"])
populator.populate()


model_file = "./ADL_Model.tflite"
model_basename = os.path.basename(model_file)
export_model_path = os.path.join("./", model_basename)  
displayer = _metadata.MetadataDisplayer.with_model_file(export_model_path)
export_json_file = os.path.join("./", os.path.splitext("./ADL_Model.tflite")[0] + ".json")
json_file = displayer.get_metadata_json()
with open(export_json_file, "w") as f:
    f.write(json_file)




