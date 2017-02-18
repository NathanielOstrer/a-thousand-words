#import tweepy
from fuzzywuzzy import fuzz
from scraper import *
#import models

class Rank:
    """
    Ranking Algorithm class
    """
    def __init__(self):
        self.location = 1
    #def trending(self):
    #   auth = tweepy.OAuthHandler(constants.CONSUMER_KEY, constants.CONSUMER_SECRET)
    #   auth.set_access_token(constants.ACCESS_KEY, constants.ACCESS_SECRET)
    #   api = tweepy.API(auth)
#
    #   #set trends place
    #   trends1 = api.trends_place(self.location)
    #   data = trends1[0] 
#
    #   # grab the trends
    #   trends = data['trends']
    #   # grab the name from each trend
    #   twitter_trending = [trend['name'] for trend in trends]
    #   # put all the names together with a ' ' separating them
    #   return twitter_trending

    def news_headlines_images(self):
        nytimes = NYTimes()
        #aljazeera = Aljazeera()
        #cnn = CNN()
        #bbc = BBC()
        #independent = Independent()
        #timemagazine = TimeMagazine()

        headlines = []
        headlines.extend(nytimes.get_headline_texts())
        
        images = []
        images.extend(nytimes.get_image_urls())

        urls = []
        urls.extend(nytimes.get_headline_urls())

        res = []
        for headline, image, url in zip(headlines, images, urls):
            if headline is not None and image is not None and url is not None:
                res.append([headline, image, url])
        ranked = []
    
        for r1 in res:
            add = 1
            for r2 in ranked:
                #try:
                    print fuzz.token_set_ratio(r1[0], r2[0])
                    if fuzz.token_set_ratio(r1[0], r2[0]) > 50:
                        print r1
                        print "\n"
                        add = 0
                        break
                    break
                #except:
                #print 'some error'
            if add:
                ranked.append(r1);
        
        return ranked


if __name__ == '__main__':
    rank = Rank()
    print rank.news_headlines_images()

