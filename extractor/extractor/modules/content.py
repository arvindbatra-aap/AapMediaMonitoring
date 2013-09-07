import logging

from extractor.basic import BasicExtractor

class ContentExtractor(BasicExtractor):

	def __init__(self, *args, **kwargs):
		super(ContentExtractor, self).__init__('ContentExtractor', *args, **kwargs)

	def extract(self, datum):
		return {}