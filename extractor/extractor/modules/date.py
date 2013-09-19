import sys
import logging

from extractor.basic import BasicExtractor

import extractor.dateUtils.date as gen_date_extractor 

class DateExtractor(BasicExtractor):

	def __init__(self, *args, **kwargs):
		self.genericDateExtractor = gen_date_extractor.DateExtractor()
		super(DateExtractor, self).__init__('DateExtractor', *args, **kwargs)

	def extract(self, datum):
		date = self.genericDateExtractor.extract(datum)
		logging.info("Genrically extracted date : " + date)
		return date
