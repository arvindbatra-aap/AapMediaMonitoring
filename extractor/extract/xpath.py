import json
import re
import string
from lxml import etree
from StringIO import StringIO

class XPathExtractor(object):

	def __init__(self, config_file):
		self._config = json.loads(open(config_file, 'r').read())
		self._sources = self._config.keys()
		self._parser = etree.HTMLParser()

		# Keep compiled regexes
		for source in self._sources:
			for attr in self._config[source]:
				if self._config[source][attr].has_key('validate'):
					self._config[source][attr]['validate'] = re.compile(self._config[source][attr]['validate'])

	def extract(self, content, source):
		extracted = {}
		source = source.lower()
		if source not in self._sources: 
			return extracted

		for attr in self._config[source].keys():
			# Extract
			html = etree.parse(StringIO(content), self._parser)
			node = html.find(self._config[source][attr]['xpath'])

			# Validate
			if node == None or not node.text: continue			
			data = filter(lambda x: x in string.printable, node.text).encode('utf-8')
			match = self._config[source][attr]['validate'].match(data)
			
			if match and match.group() == data:
				extracted[attr] = data

		return extracted




					






