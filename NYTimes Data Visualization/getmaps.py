tags = open('tags.txt').readlines()

tags = [r.rstrip().replace('[', '').replace(']', '').replace("'", '') for r in tags]

map = {}

# indiv. tags -> images
for tag in tags:
	img = tag.split(' = ')[1]
	bits = tag.split(' = ')[0].split(', ')

	if tag.split(' = ')[0] == '':
		continue

	for bit in bits:
		if not bit in map:
			map[bit] = [img]
		else:
			l = map[bit]
			l.append(img)
			map[bit] = l

#print map


#images -> a list of tags
map = {}

for tag in tags:
	img = tag.split(' = ')[1]
	bits = tag.split(' = ')[0].split(', ')

	if tag.split(' = ')[0] == '':
		continue

	map[img] = bits

print map