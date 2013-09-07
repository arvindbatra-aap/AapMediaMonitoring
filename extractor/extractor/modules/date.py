import logging

from extractor.basic import BasicExtractor

class DateExtractor(BasicExtractor):

	def __init__(self, *args, **kwargs):
		super(DateExtractor, self).__init__('DateExtractor', *args, **kwargs)

	def extract(self, datum):
		return {}
