import sys
import constants
import json as jsonlib
import urllib2

def getnews(source):
    request_url = "https://newsapi.org/v1/articles?source={}&sortBy=top&apiKey={}".format(source, constants.NEWS_API_KEY)

    print request_url

    r = urllib2.urlopen(request_url).read()

    json = jsonlib.loads(r)

    source = json['source']
    article = json['articles'][0] #just get the top article for each news orginization I suppose


    description = article['description']
    title = article['title']
    url = article['url']
    author = article['author']

    return {"source" : source, "description" : article["description"].encode('ascii', 'ignore').replace("'", "`").replace('"', '`'),
            "title" : article["title"].encode('ascii', 'ignore').replace("'", "`").replace('"', '`'), "url" : article["url"],
            "author" : article["author"].encode('ascii', 'ignore').replace("'", "`").replace('"', '`'), "image" : article["urlToImage"],
            "publishedAt" : article["publishedAt"].encode('ascii', 'ignore').replace("'", "`").replace('"', '`') }

if __name__ == "__main__":
    #print nytimes()
    print getnews('the-washington-post')
