import urllib, urllib2, requests

urls = open('results-20170218-144713.csv').readlines()

for url in urls:
	urllib.urlretrieve(url, url.split('/')[-1][:-1])

