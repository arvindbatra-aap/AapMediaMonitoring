import logging

from extractor.basic import BasicExtractor

class CommentsExtractor(BasicExtractor):

	def __init__(self, *args, **kwargs):
		super(CommentsExtractor, self).__init__('CommentsExtractor', *args, **kwargs)

	def extract(self, datum):
		return {}