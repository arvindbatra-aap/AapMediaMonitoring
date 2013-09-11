import os

from extractor.manager import ExtractionManager
from logging import info, error, getLogger, INFO, ERROR
from datetime import *

EXTRACT_PATH = 'crawl-raw'

# Set Log Level to Info
getLogger().setLevel(INFO)

# Counters
attr_count = 0
file_count = 0

# Set Start Date of extraction
date_7_days_ago = (datetime.datetime.now() - datetime.timedelta(days=7)).date()

manager = ExtractionManager()
for root, dirs, files in os.walk(EXTRACT_PATH):
	
	if len(dirs) == 0 and len(files) > 0:
		(date, source) = root.split("/")[1:3]

		if (datetime.datetime.strptime(date, '%Y-%m-%d').date() > date_7_days_ago):

			for file in files:
				if file.endswith(".html"):
					file = root+'/'+file
					info("Processing dump file: %s" % file)
					file_count += 1

					url = open(file.split(".")[0]+".url", 'r').read()
					#url = "url%d" % file_count
					content = open(file, 'r').read()
					info("URL: %s" % url)
					extracted = manager.extractAll(content, url, source, date, file)
					attr_count += len(extracted.keys())
					print extracted
					print "---"

info("Extraction completed. Totally extracted %d attributes from %s HTML dump files." % (attr_count, file_count))

