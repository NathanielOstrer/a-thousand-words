from google.appengine.ext import db

class Headline(db.Model):
	url = db.StringProperty(multiline=True)
	headline = db.StringProperty(multiline=True)
	image = db.StringProperty(multiline=True)
	source = db.StringProperty(multiline=True)
	time = db.DateTimeProperty()