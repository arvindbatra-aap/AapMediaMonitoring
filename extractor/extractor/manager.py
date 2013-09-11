import string

from logging import info, error
from lxml import etree

from extractor.modules.xpath import XPathExtractor
from extractor.modules.date import DateExtractor
from extractor.modules.content import ContentExtractor
from extractor.modules.author import AuthorExtractor
from extractor.modules.title import TitleExtractor
from extractor.modules.category import CategoryExtractor
from extractor.modules.comments import CommentsExtractor
from extractor.modules.location import LocationExtractor

from StringIO import StringIO

XPATH_CONFIG = 'conf/xpath.json'

ATTR_EXTRACTORS = {
	'date'		: DateExtractor(),
	'title'		: TitleExtractor(),
	'content'	: ContentExtractor(),
	'author'	: AuthorExtractor(),
	'category'	: CategoryExtractor(),
	'comments'	: CommentsExtractor(),
	'location'	: LocationExtractor()
}

class ExtractionManager(object):

	def __init__(self):
		self._xpath_extractor = XPathExtractor(config_file=XPATH_CONFIG)
		self._parser = etree.HTMLParser()
		info("ExtractionManager started...")

	def _clean(self, val):
		return filter(lambda x: x in string.printable, val).encode('utf-8')

	def extractAll(self, content, url, source, date, html_file):
		# Create a data object to use throughout 
		html = etree.parse(StringIO(content), self._parser)
		datum = {
			'content': html,
			'url'	 : url,
			'source' : source,
			'date'   : date,
			'file'   : html_file
 		}

		# First try to extract from XPath
		extracted = self._xpath_extractor.extract(datum)

		# If Xpath didn't work, then try the more algo-intesive extractors for each missing attribute
		for attr in ATTR_EXTRACTORS.keys():
			if attr not in extracted:
				val = ATTR_EXTRACTORS[attr].extract(datum)
				if val:
					extracted[attr] = self._clean(val)
				else:
					error("Could not extract attribute %s" % (attr))

		return extracted

