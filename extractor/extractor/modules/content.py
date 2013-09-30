import logging
import urllib
import urllib2
import json as simplejson
from lxml import etree

from extractor.basic import BasicExtractor

class ContentExtractor(BasicExtractor):

	def __init__(self, *args, **kwargs):
		super(ContentExtractor, self).__init__('ContentExtractor', *args, **kwargs)

	def extract(self, datum):
		input_data = {}
		input_data["content"] = datum['raw_html']
		input_data["url"] = datum['url']
		url = 'http://localhost:2121/api/content'
		req = urllib2.Request(url, simplejson.dumps(input_data))
		req.add_header('Content-Type', 'text/plain; charset=utf-8')
		response = urllib2.urlopen(req)
		output = simplejson.loads(response.read())
		if output["status"] == "success":
			return output["content"]
		else:
			return {}
