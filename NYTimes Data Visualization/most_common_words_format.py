#load data
words = open('all_words.txt').readlines()
words = [word.rstrip() for word in words]

#map out frequency of each word
freq_map = {}

for word in words:
	if not word in freq_map:
		freq_map[word] = 1
	else:
		freq_map[word] = freq_map[word] + 1

#sort words by most common first
most_common = sorted(freq_map, key=freq_map.__getitem__, reverse=True)

#format for d3.js wordcloud
for word in most_common:
	print '{' + "text: '{}', size: {}, href: '{}'".format(word, freq_map[word], 'https://thousand-words.appspot.com/headline/{}'.format(word)) + '},'


