from logging import info

class BasicExtractor(object):

	def __init__(self, extractor):
		self._name = extractor
		info("Initializing %s ..."  % extractor)

	def extract(self, datum):
		raise NotImplementedError("Method extract() is not implemented in %s" % self._name)    