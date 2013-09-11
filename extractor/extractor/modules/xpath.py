import json
import re
import string

from extractor.basic import BasicExtractor
from logging import info, error

class XPathExtractor(BasicExtractor):

	def __init__(self, config_file, *args, **kwargs):
		super(XPathExtractor, self).__init__('BasicExtractor', *args, **kwargs)

		self._config = json.loads(open(config_file, 'r').read())
		self._sources = self._config.keys()

		# Keep compiled regexes
		for source in self._sources:
			for attr in self._config[source]:
				if self._config[source][attr].has_key('validate'):
					self._config[source][attr]['validate'] = re.compile(self._config[source][attr]['validate'])

		info("Loaded XPath codes from config file: %s" % config_file)

	def extract(self, datum):
		html = datum['content']
		source = datum['source']
		file = datum['file']

		extracted = {}
		source = source.lower()
		foundSourceName = None
		for _source in self._sources:
			if source.endswith(_source):
				foundSourceName = _source
		
		if foundSourceName == None:
		 error("Source %s not found in Xpath config!" % source)
		 return extracted

		for attr in self._config[foundSourceName].keys():
			# Extract
			xpath = self._config[foundSourceName][attr]['xpath']
			nodes = html.xpath(xpath)
			# Validate
			data = "" 
			if nodes == None or len(nodes) == 0: continue
			for item in nodes:
				if item is None: continue
				if hasattr(item, 'text') and item.text != None:
					data += " " + item.text.encode("utf-8")
				elif hasattr(item,'is_text'): 
					data += str(item.encode("utf-8"))
			data = data.strip()
				
			if len(data) == 0: continue

			if self._config[foundSourceName][attr].has_key('validate'):
				regex = self._config[foundSourceName][attr]['validate'] 
				match = regex.match(data)
				
				if match and match.group() == data:
					extracted[attr] = data
				else:
					error("Validation failed for attribute %s with XPath:[%s] against Regex:[%s]" % (attr, xpath, regex))
			else:
				extracted[attr] = data

		return extracted





