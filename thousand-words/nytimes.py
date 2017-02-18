import requests
key = "de5bc6b35f07ba0f0a3eb4d4e273145f:4:63029019"

def getTimesHeadlinePic(indx=0):
  r = requests.get('http://api.nytimes.com/svc/topstories/v1/home.json', params = {'api-key': key})
  data = r.json()
  image = None
  max_height = 0
  max_width = 0
  for pic in data['results'][indx]['multimedia']:
    if pic['format'] == 'Normal':
      image = pic
      break

  big = intersection(image['url'], data['results'][indx]['multimedia'][0]['url']) + 'master675.jpg'
  d = {'url': image['url'], 'big_img': big, 'alt': image['caption']}
  return d

def intersection(s1, s2):
  s = ""
  for x in xrange(0, len(s1)):
    if s1[x] == s2[x]:
      s = s = s + s1[x]
    else:
      return s


img = getTimesHeadlinePic(3)

print img
    
