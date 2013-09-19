import string

from logging import info, error
import time
from lxml import etree
import datetime

from extractor.modules.xpath import XPathExtractor
from extractor.modules.date import DateExtractor
from extractor.modules.content import ContentExtractor
from extractor.modules.author import AuthorExtractor
from extractor.modules.title import TitleExtractor
from extractor.modules.category import CategoryExtractor
from extractor.modules.comments import CommentsExtractor
from extractor.modules.location import LocationExtractor
from extractor.parsedatetime import parsedatetime as pdt
from StringIO import StringIO
import extractor.dateUtils.natty as Nat

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
		self.nat = Nat.Natty()
		self._xpath_extractor = XPathExtractor(config_file=XPATH_CONFIG)
		self._parser = etree.HTMLParser()
		info("ExtractionManager started...")

	def _clean(self, val):
		return filter(lambda x: x in string.printable, val).encode('utf-8')

	def _isValidDate(self, date):
		try:
			valid_date = time.strptime(date, '%Y-%m-%d')
			return True
		except ValueError:
			return False
		return False

	def extractAll(self, content, url, source, date, html_file):
		# Create a data object to use throughout 
		html = etree.parse(StringIO(content), self._parser)
		datum = {
			'content': html,
			'url'	 : url,
			'source' : source,
			'date'   : date,
			'file'   : html_file,
			'extracted' : None
 		}

		# First try to extract from XPath
		extracted = self._xpath_extractor.extract(datum)
		datum['extracted'] = extracted

		# If Xpath didn't work, then try the more algo-intesive extractors for each missing attribute
		for attr in ATTR_EXTRACTORS.keys():
			if attr not in extracted or ('date' == attr and not self._isValidDate(extracted['date'])):
				val = ATTR_EXTRACTORS[attr].extract(datum)
				if val:
					extracted[attr] = self._clean(val)

		#Special check for date
		if 'date' in extracted and (not self._isValidDate(extracted['date'])):
			#try using natty
			nattyDate = self.nat.extract_date(extracted['date'])
			if self._isValidDate(nattyDate):
				extracted['date'] = nattyDate
			else:
				#try using the datetimeparse lib
				invalidDate = extracted['date']
				c = pdt.Constants()
				p = pdt.Calendar(c)
				result,retVal = p.parse(invalidDate)
				#http://stackoverflow.com/questions/1810432/handling-the-different-results-from-parsedatetime
				if retVal in (1,2,3):
					edt = datetime.datetime( *result[:6] ).strftime('%Y-%m-%d')
					extracted['date'] = edt
		return extracted

