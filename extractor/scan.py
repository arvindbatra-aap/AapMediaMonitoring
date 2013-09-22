import os
import os.path
import sys, traceback

from extractor.manager import ExtractionManager
from logging import info, error, getLogger, INFO, ERROR, DEBUG
import datetime
import json
import MySQLdb
import string 
import hashlib

db = MySQLdb.connect(host="localhost", # your host, usually localhost
					 user="root", # your username
					  passwd="aapmysql00t", # your password
					  db="AAP") # name of the data base

db.charset="utf8"
db.autocommit(True)

cur = db.cursor() 

#EXTRACT_PATH = "/root/crawl-raw/2013-09-19/www.washingtonpost.com"
#EXTRACT_PATH = '/root/crawl-raw/2013-09-20/'
EXTRACT_PATH = '/root/crawl-raw/'

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
		(date, source) = root.split("/")[-2:]
		if (datetime.datetime.strptime(date, '%Y-%m-%d').date() > date_7_days_ago):
			for file in files:
				if file.endswith(".html"):
					file = root+'/'+file
					info("Processing dump file: %s" % file)
					file_count += 1

					url = open('.'.join(file.split('.')[:-1])+ ".url", 'r').read()
					hashed = file.split('/')[-1].split('.')[-2]

					content = open(file, 'r').read()
					info("URL: %s" % url)
					extracted = manager.extractAll(content, url, source, date, file)
					attr_count += len(extracted.keys())
					itemFile = '.'.join(file.split('.')[:-1]) + ".item"
					print itemFile
					if os.path.exists(itemFile):
						itemFH = open(itemFile)
						print itemFH
						try:
							itemData = json.load(itemFH)
							if 'canonical_url' not in extracted and 'url' in itemData:
								extracted['url'] = itemData['url']
							if 'title' not in extracted and 'title' in itemData:
								extracted['title'] = itemData['title']
							if 'content' not in extracted and 'description' in itemData:
								extracted['content'] = itemData['description']
							if 'date' not in extracted and 'publishedDate' in itemData:
								extracted['date'] = itemData['publishedDate']
						except Exception:
							print "Failed loading json for ", itemFile	
						itemFH.close()

					#print extracted
					print "---"


					title=''
					url=''
					content=''
					date1=''
					src=source
					if 'title' in extracted:
						title=extracted['title']
					if 'canonical_url' in extracted:
						url=extracted['canonical_url']
					else: 
						if 'url' in extracted:
							url=extracted['url']
					if 'content' in extracted:
						content=extracted['content']
					if 'date' in extracted:
						date1=extracted['date']
					else:
						print "Date not found"
						continue
					
					try:
						md5Obj = hashlib.md5()
						md5Obj.update(title)	
						titleMd5 = md5Obj.hexdigest()
						repl=lambda x: MySQLdb.escape_string(x)
						query="INSERT IGNORE INTO ARTICLE_TBL (URL, ID, TITLE, TITLE_MD5, CONTENT, publishedDate, src) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s');" % (repl(url), repl(hashed), repl(title), repl(titleMd5), repl(content), repl(date1), repl(src)) 
						cur.execute(query)
					except UnicodeEncodeError:
						print "Unicode encode issue in query ", file
						print '-'*60
						traceback.print_exc(file=sys.stdout)
						print '-'*60
						continue
					except UnicodeDecodeError:
						print "Unicode decode issue in query ", file
						print '-'*60
						traceback.print_exc(file=sys.stdout)
						print '-'*60
						continue
					except Exception:
						print query
						raise

info("Extraction completed. Totally extracted %d attributes from %s HTML dump files." % (attr_count, file_count))

