import logging

from extractor.basic import BasicExtractor

class TitleExtractor(BasicExtractor):

	def __init__(self, *args, **kwargs):
		super(TitleExtractor, self).__init__('TitleExtractor', *args, **kwargs)

	def extract(self, datum):
		return {}