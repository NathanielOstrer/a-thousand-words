import sys
import requests
import constants
import json

def nytimes():
    request_url = "https://api.nytimes.com/svc/topstories/v2/home.json?api-key={}".format(constants.NYTIMES_API_KEY)

    print request_url

    r = requests.get(request_url)

    json = r.json()

    status = json['status'] #should be OK - do some error checking :p 

    results = json['results']

    num_results = json['num_results']

    results = results[:3] # limit to top 3 results


    processed = []

    for res in results:
        short_url = res['short_url']
        title = res['title']
        abstract = res['abstract']
        multimedia = res['multimedia']

        multimedia = sorted(multimedia, key=lambda x: -x['width'])

        image_url = multimedia[0]['url']

        processed.append({'short_url' : short_url, 'title' : title, 'abstract' : abstract, 'image_url' : image_url})

    return processed

if __name__ == "__main__":
    print nytimes()