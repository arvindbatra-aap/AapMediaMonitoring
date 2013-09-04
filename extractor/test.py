from extract.xpath import XPathExtractor

file = 'toi.html'

ex = XPathExtractor(config_file='conf/xpath.json')
print ex.extract(open(file).read(), 'TIMES of India')