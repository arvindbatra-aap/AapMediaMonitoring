import logging
import urllib
import urllib2
import simplejson as json

from extractor.basic import BasicExtractor

class ContentExtractor(BasicExtractor):

	def __init__(self, *args, **kwargs):
		super(ContentExtractor, self).__init__('ContentExtractor', *args, **kwargs)

		def extract(self, datum):
			url = 'http://localhost:2121/api/content'
			req = urllib2.Request(url, json.dumps(datum))
			req.add_header('Content-Type', 'text/plain; charset=utf-8')
			response = urllib2.urlopen(req)
			output = json.loads(response.read())
			if output["status"] == "success":
				return output["content"]
			else:
				return {}

