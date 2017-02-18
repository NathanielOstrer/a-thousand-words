import sys
from bs4 import BeautifulSoup
import requests

class Newspaper:
    """
    Abstract class for scraping a newspaper for its headline images.

    Includes methods that we expect to be implemented for every newspaper.

    Methods are short on purpose, in order to compartmentalize scraping.
    If, for some site, it's hard to collect all the articles, the 
    get_articles() method can be long without making the code hard to read.

    If finding the headline is harder, that method can be longer and the rest 
    of the code can look very similar between newspapers.
    """

    def __init__(self, url):
        self.url = url
        self.html = requests.get(url, timeout=15).content
        self.soup = BeautifulSoup(self.html, 'html.parser')

    def absolute_url(self, url):
        if url.startswith(self.url):
          return url

        if url[0] == '/':
          return self.url + url[1:]
        else:
          return self.url + url

    def refresh(self):
        self.html = requests.get(self.url, timeout=15).content
        self.soup = BeautifulSoup(self.html, 'html.parser')

    def get_articles(self):
        """ Get an array of articles on the homepage. """
        raise NotImplementedError('Abstract Method')

    def get_article(self):
        return self.get_articles()[0]

    def get_headline(self, article):
        """ Get the BS object for the headline. """
        raise NotImplementedError('Abstract Method')

    def get_headlines(self):
        return [self.get_headline(x) for x in self.get_articles()][:9]

    def get_headline_text(self, article):
        return self.get_headline(article).a.string 

    def get_headline_texts(self):
        return [x.a.string if x else '' for x in self.get_headlines()]

    def get_headline_url(self, article):
        return self.absolute_url(self.get_headline(article).a.get('href'))

    def get_headline_urls(self):
        res = [self.absolute_url(headline.a.get('href')) for headline in self.get_headlines()]
        return res

    def get_headline_soup(self):
        page_url = self.get_headline_url(self.get_article())
        html = requests.get(page_url).content
        return BeautifulSoup(html, 'html.parser')

    def get_headline_soups(self):
        res = []
        for page_url in self.get_headline_urls():
          html = requests.get(page_url).content
          res.append(BeautifulSoup(html, 'html.parser'))
        return res

    def get_image_url(self):
        s = self.get_headline_soup()
        return s.img.get('src')

    def get_image_urls(self):
        res = []
        for s in self.get_headline_soups():
          res.append(s.img.get('src'))
        return res

    def get_source(self):
        raise NotImplementedError('Abstract Method')

class NYTimes(Newspaper):
    """
    NY Times headline image scraper.

    Grabs the first article on the homepage (upper left hand corner)
    and finds the url of the image.
    """
    def __init__(self):
        Newspaper.__init__(self, 'http://nytimes.com')

    def absolute_url(self, url):
        return url

    def get_articles(self):
        return self.soup.find_all('article')

    def get_headline(self, article):
        return article

    def get_headlines(self):
        return self.get_articles()[0:9]

    def get_image_url(self):
        s = self.get_headline_soup()
        return s.find('meta', property='og:image').get('content')

    def get_source(self):
        return "nytimes"

class Aljazeera(Newspaper):
    def __init__(self):
        Newspaper.__init__(self, 'http://america.aljazeera.com/')
 
    def get_articles(self):
        return self.soup.find_all('article')

    def get_headline(self, article):
        return self.soup.find('h1', 'topStories-headline')
    
    def is_correct_image(self, tag):
        return tag.get('data-media') == '(min-width: 768px)'
    
    #    def get_image_url(self):
    #        a = self.get_articles()[0].div(self.is_correct_image)[0].get('data-src')
    #       a = a.replace("image.adapt.375.high", "image.adapt.1000.high");
    #   return self.absolute_url(a)
    def get_image_url(self):
        s = self.get_headline_soup()
        return self.absolute_url(s.find('meta', property='og:image').get('content'))

    def get_source(self):
        return "aljazeera"

class CNN(Newspaper):
    def __init__(self):
        Newspaper.__init__(self, 'http://cnn.com/')
    def get_articles(self):
        return self.soup.find_all('article')
    def get_headline(self, article):
        return self.soup.find('h3', 'cd__headline')
    def get_headline_text(self, article):
        return self.get_headline_soup().find('h1', 'pg-headline').string
        
        #tex = [soup for soup in self.get_headline_soup().find('article').find('p', 'zn-body__paragraph').stripped_strings]
        #str = tex[1];
        #for i in range(2, len(tex), 1):
            #str += " " + tex[i]
        #return str
    
    def get_image_url(self):
        s = self.soup.find_all('article')[0].find_all('img')[0].get('data-src-full16x9')
        return s

    def get_source(self):
        return "cnn"

class WashingtonPost(Newspaper):
    def __init__(self):
        Newspaper.__init__(self, 'https://www.washingtonpost.com/')
    def get_articles(self):
        return self.soup.find_all('div', 'headline')

    def get_headline(self, article):
        return article

    def get_image_url(self):
        return self.get_headline_soup().find('div', id='article-body').img.get('src')

    def get_source(self):
        return "washingtonpost"

class Spiegel(Newspaper):
    def __init__(self):
        Newspaper.__init__(self, 'http://www.spiegel.de/international/')

    def get_articles(self):
        return ['apple', 'banana', 'carrot']

    def get_headline_text(self, article):
        return self.soup.find('h2', {'class' : 'article-title '}).a['title']

    def get_headline_url(self, article):
        return 'http://www.spiegel.de' + self.soup.find('h2', {'class' : 'article-title '}).a['href']

    def get_image_url(self):
        return self.soup.find('div', {'class' : 'image-buttons-panel clearfix'}).img['src']

    def get_source(self):
        return "spiegel"

class BBC(Newspaper):
    def __init__(self):
        Newspaper.__init__(self, 'http://www.bbc.com/')

    def get_articles(self):
        return self.soup.find_all('li', 'media-list__item')
    
    def get_headline(self, article):
        #return article.find('div', 'media__content').find('h3', 'media__title')
        return article.find('div', 'media__content')
    
    def get_headline_text(self, article):
        return self.get_headline(article).find('p', 'media__summary').string
    
    def get_image_url(self):
        return self.get_headline_soup().find('meta', property='og:image').get('content')

    def get_source(self):
        return "bbc"

class Independent(Newspaper):
    def __init__(self):
        Newspaper.__init__(self, 'http://www.independent.co.uk/')
    def get_articles(self):
        return self.soup.find_all('article');
    def get_headline(self, article):
        return article
    def get_headline_text(self, article):
        return self.get_headline(article).h1.text
    def get_image_url(self):
        return self.get_headline_soup().find('meta', property='og:image').get('content')

    def get_source(self):
        return "independent"

class TimeMagazine(Newspaper):
    def __init__(self):
        Newspaper.__init__(self, 'http://time.com/')
    def absolute_url(self, url):
        return url
    def get_articles(self):
        return self.soup.find_all('article')
    def get_headline(self, article):
        return article
    def get_headline_text(self, article):
        return self.get_headline(article).p.string.replace('\n', '').replace('\t', '')
        #def get_image_url(self):
        #return self.get_headline(self.get_article()).img.get('data-srcset')
    def get_image_url(self):
        s = self.get_headline_soup()
        return s.find('meta', property='og:image').get('content')

    def get_source(self):
        return "time"
        
def getFirstPictures():
    sources  = [
        BBC(),
        NYTimes(),
        TimeMagazine(),
        Independent(),
        CNN(),
        Spiegel()
    ]

    d = []
    for source in sources:
        headline = source.get_headline_text(source.get_article())
        headline = headline if headline is not None else ''
        headline = headline.encode('ascii', 'ignore')
        #print headline
        headline = headline.replace('\n', ' ').strip()
        s = [
        headline,
        source.get_image_url(), 
        source.get_headline_url(source.get_article()),
        source.get_source()
        ]
        # print source.url
        d.append(s)
    return d

if __name__ == '__main__':
    i = Spiegel()
    print i.get_headline()
    print i.get_image_url()
    print i.get_headline_url()