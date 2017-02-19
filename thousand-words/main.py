#!/usr/bin/env python
#
# Copyright 2007 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
import webapp2
from google.appengine.ext import vendor
import scraper
import random

import word_img_map, img_to_tags_map

vendor.add('lib')

class MainHandler(webapp2.RequestHandler):
    def get(self):
        self.response.write("""
        	<html>
  <head>
    <title>NYT 2016 Word Cloud</title>
    <script src="lib/d3/d3.js" charset="utf-8"></script>
    <script src="lib/d3/d3.layout.cloud.js"></script>
    <script src="d3.wordcloud.js"></script>
    <script src="words.js"></script>
  </head>
  <body style="text-align: center; background-color: black; overflow: hidden;">
    <div id='wordcloud'></div>
    <script>
      d3.wordcloud()
        .size([window.innerWidth, window.innerHeight])
        .fill(d3.scale.ordinal().range(["red", "white", "blue"]))
        .words(words)
        .font("Impact")
        .spiral("archimedean")
        .start();
    </script>
  </body>
</html>




        	""")

class GetNews(webapp2.RequestHandler):
	def get(self):
		subscriptions = ['the-washington-post', 'the-new-york-times', 'associated-press', 'bbc-news', 'cnn', 'the-wall-street-journal', 'espn', 'bloomberg', 'business-insider', 'engadget', 'independent', 'national-geographic', 'reddit-r-all', 'the-economist', 'ign']

		articles = {}

		for sub in subscriptions:
			try:
				print 'sub = {}'.format(sub)
	
				article = scraper.getnews(sub)
				source = article['source']
				del article['source']
				articles[source] = article
			except:
				print "jinkees there's no article!"

		self.response.write(str(articles).replace("u'", "'").replace("'", '"'))

class Headline(webapp2.RequestHandler):
	def get(self):
		phrase = self.request.url.split('/')[-1]
		
		images = word_img_map.word_img_map[phrase]

		val = random.randint(0, len(images)-1)

		image = images[val]

		self.response.write("""

			<html>

				<body style="background-color: black">

					<center>

						<img src="/{}">

						<p> Also: </p>

					</center>

				</body>

			</html>


			""".format(image))

app = webapp2.WSGIApplication([
	('/getnews.json', GetNews),
	(r'/.+', Headline),
	('/', MainHandler),
], debug=True)
