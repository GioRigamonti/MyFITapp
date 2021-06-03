# -*- coding: utf-8 -*-
"""
Created on Thu Jun  3 17:05:45 2021

@author: Giorgia
"""

  
from tflite_support import flatbuffers
from tflite_support import metadata as _metadata
from tflite_support import metadata_schema_py_generated as _metadata_fb
import os


# Creates model info.

model_meta = _metadata_fb.ModelMetadataT()
model_meta.name = "ADL model classifier"
model_meta.description = ("Identify the Activty related to Human Action")
model_meta.author = "Jessica & Giorgia"

# Creates input info.
input_meta = _metadata_fb.TensorMetadataT()

# Creates output info.
output_meta = _metadata_fb.TensorMetadataT()

input_meta.name = "Accelerometer coord."
input_meta.description = ("Coordinate acc")
input_meta.content = _metadata_fb.ContentT()

# Creates output info.
output_meta = _metadata_fb.TensorMetadataT()
output_meta.name = "Labels"
output_meta.description = "Labels of Human Actions"
output_meta.content = _metadata_fb.ContentT()
label_file = _metadata_fb.AssociatedFileT()
label_file.name = os.path.basename("labels.txt")
label_file.description = "Labels for objects that the model can recognize."
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
populator.load_associated_files(["C:/Users/giorg/Documents/MyFITapp/ADL_Model/labels.txt"])
populator.populate()




