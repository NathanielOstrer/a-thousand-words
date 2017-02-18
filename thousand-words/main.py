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


vendor.add('lib')

class MainHandler(webapp2.RequestHandler):
    def get(self):
        def get(self):
		self.response.out.write("""
			<html>
	<head>
		<title>News in Pictures</title>
		<link rel="stylesheet" href="./style.css">
		<meta http-equiv="refresh" content="600; URL=/">
        <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate"/>
        <meta http-equiv="Pragma" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>
        <!-- start Mixpanel --><script type="text/javascript">(function(e,b){if(!b.__SV){var a,f,i,g;window.mixpanel=b;b._i=[];b.init=function(a,e,d){function f(b,h){var a=h.split(".");2==a.length&&(b=b[a[0]],h=a[1]);b[h]=function(){b.push([h].concat(Array.prototype.slice.call(arguments,0)))}}var c=b;"undefined"!==typeof d?c=b[d]=[]:d="mixpanel";c.people=c.people||[];c.toString=function(b){var a="mixpanel";"mixpanel"!==d&&(a+="."+d);b||(a+=" (stub)");return a};c.people.toString=function(){return c.toString(1)+".people (stub)"};i="disable time_event track track_pageview track_links track_forms register register_once alias unregister identify name_tag set_config people.set people.set_once people.increment people.append people.union people.track_charge people.clear_charges people.delete_user".split(" ");
for(g=0;g<i.length;g++)f(c,i[g]);b._i.push([a,e,d])};b.__SV=1.2;a=e.createElement("script");a.type="text/javascript";a.async=!0;a.src="undefined"!==typeof MIXPANEL_CUSTOM_LIB_URL?MIXPANEL_CUSTOM_LIB_URL:"file:"===e.location.protocol&&"//cdn.mxpnl.com/libs/mixpanel-2-latest.min.js".match(/^\/\//)?"https://cdn.mxpnl.com/libs/mixpanel-2-latest.min.js":"//cdn.mxpnl.com/libs/mixpanel-2-latest.min.js";f=e.getElementsByTagName("script")[0];f.parentNode.insertBefore(a,f)}})(document,window.mixpanel||[]);
mixpanel.init("e503d1b1277055fd50bfe762ae627763");</script><!-- end Mixpanel -->
<script type="text/javascript">
mixpanel.track("visit");
</script>
	</head>
	<body>
			""")
		headlines = models.Headline.gql("WHERE time != DATE('2015-01-01') ORDER BY time DESC").fetch(limit=6)

		for headline in headlines:
			self.response.out.write('<div class="img" style="background-image: url(\'' + str(headline.image) + '\');">')
			self.response.out.write('<a href="' + str(headline.url) + '">')
			self.response.out.write('<span class="text-content">')
			self.response.out.write('<span>')
			self.response.out.write('<span class="headline">')
   		 	self.response.out.write(str(headline.headline))
			self.response.out.write('</span>')
			self.response.out.write('<span class="blurb">')
			#self.response.out.write('Blurb goes here')
   		 	self.response.out.write('</span>')
   		 	self.response.out.write('<span class=logo>')
   		 	self.response.out.write('<img src="/' + str(headline.source) +'.png" alt="">')
   		 	self.response.out.write('</span>')
   		 	self.response.out.write('</span>')
			self.response.out.write('</span>')
			self.response.out.write('</a>')
			self.response.out.write('</div>')
			self.response.out.write('\n')

		self.response.out.write("""
		</body>
		</html>
			""")

class GetNews(webapp2.RequestHandler):
	def get(self):
		subscriptions = ['the-washington-post', 'the-new-york-times', 'associated-press', 'bbc-news', 'cnn', 'the-wall-street-journal']

		articles = {}

		for sub in subscriptions:
			article = scraper.getnews(sub)
			source = article['source']
			del article['source']
			articles[source] = article

		self.response.write(str(articles).replace("u'", "'").replace("'", '"'))


app = webapp2.WSGIApplication([
	('/', MainHandler),
	('/getnews', GetNews)
], debug=True)
