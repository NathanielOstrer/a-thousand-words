#import system libraries
import io
import os
import sys
import time

#import cloud vision library
from google.cloud import vision

#initialize client
vision_client = vision.Client()

#get the list of all the image files in directory
files = [r.rstrip().split('/')[-1] for r in open('results-20170218-144713.csv').readlines()]


for file_name in files:
	#sleep to not go over cloud vision API rate limit
    time.sleep(.2)    

    #label image
    with io.open(file_name, 'rb') as image_file:
        content = image_file.read()
    image = vision_client.image(content=content)
    labels = image.detect_labels()
    s = []
    
    #format labels as a list
    for label in labels:
        s.append(str(label.description))
        
    print str(s) + ' = ' + file_name
