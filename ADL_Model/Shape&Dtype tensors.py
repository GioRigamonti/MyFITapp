# -*- coding: utf-8 -*-
"""
ADL_Model's tensors shape and dtype
"""

import tensorflow as tf

interpreter = tf.lite.Interpreter(model_path="./ADL_Model.tflite")
interpreter.allocate_tensors()

# Print input shape and type
print(interpreter.get_input_details()[0]['shape']) 
print(interpreter.get_input_details()[0]['dtype'])

# Print output shape and type
print(interpreter.get_output_details()[0]['shape']) 
print(interpreter.get_output_details()[0]['dtype'])