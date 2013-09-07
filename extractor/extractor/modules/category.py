import logging

from extractor.basic import BasicExtractor

class CategoryExtractor(BasicExtractor):

	def __init__(self, *args, **kwargs):
		super(CategoryExtractor, self).__init__('CategoryExtractor', *args, **kwargs)

	def extract(self, datum):
		return {}