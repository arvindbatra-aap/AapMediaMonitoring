import logging

from extractor.basic import BasicExtractor

class LocationExtractor(BasicExtractor):

	def __init__(self, *args, **kwargs):
		super(LocationExtractor, self).__init__('LocationExtractor', *args, **kwargs)

	def extract(self, datum):
		return {}