import os
import os.path

from extractor.manager import ExtractionManager
from logging import info, error, getLogger, INFO, ERROR
import datetime
import json
import MySQLdb
import string 
db = MySQLdb.connect(host="localhost", # your host, usually localhost
                     user="root", # your username
                      passwd="aapmysql00t", # your password
                      db="AAP") # name of the data base
db.charset="utf8"
db.autocommit(True)

cur = db.cursor() 

EXTRACT_PATH = '/root/crawl-raw/2013-09-11/www.indianexpress.com'

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

					#Check if the url exists in the table
					cur.execute("SELECT * FROM ARTICLE_TBL WHERE id='"+hashed+"'")

					for row in cur.fetchall() :
						print "DATA->"
						print row[0]
						#continue; 
					print "item NOT FOUND in the DB"
 
					content = open(file, 'r').read()
					info("URL: %s" % url)
					extracted = manager.extractAll(content, url, source, date, file)
					attr_count += len(extracted.keys())
					itemFile = '.'.join(file.split('.')[:-1]) + ".item"
					print itemFile
					if os.path.exists(itemFile):
						itemFH = open(itemFile)
						print itemFH
						itemData = json.load(itemFH)
						print itemData
						itemFH.close()

					print extracted
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
						repl=lambda x: string.replace(x,"'","\\'")
						query="INSERT IGNORE INTO ARTICLE_TBL (URL, ID, TITLE, CONTENT, publishedDate, src) VALUES ('%s', '%s', '%s', '%s', '%s', '%s');" % (repl(url), repl(hashed), repl(title), repl(content), repl(date1), repl(src)) 
						cur.execute(query.encode('cp1252'))
					except UnicodeEncodeError:
						print "Unicode issue in query "
						continue
					except Exception:
						print query
						raise

info("Extraction completed. Totally extracted %d attributes from %s HTML dump files." % (attr_count, file_count))

