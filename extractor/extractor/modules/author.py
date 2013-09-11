import logging

from extractor.basic import BasicExtractor

class AuthorExtractor(BasicExtractor):

	def __init__(self, *args, **kwargs):
		super(AuthorExtractor, self).__init__('AuthorExtractor', *args, **kwargs)

	def extract(self, datum):
		return {}