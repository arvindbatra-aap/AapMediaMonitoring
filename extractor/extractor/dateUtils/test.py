import urllib2 as ul

from date import *

bad_title_phrase_list = ["Hindustan Times"]
# not working due to bad title match

# works

# times of of India
'''url = "http://timesofindia.indiatimes.com/india/Curfew-relaxed-in-Muzaffarnagar-clashes-erupt-in-Baghpat/articleshow/22490767.cms"
html_text = ul.urlopen(url).read()
de = DateExtractor()
extract = de.extract({"raw_html" : html_text})
print "\n", "Extracted date : ", extract, "\n" 
'''

"""
url = "http://timesofindia.indiatimes.com/india/Indians-less-happy-than-Pakistanis-Bangladeshis-UN-report/articleshow/22492120.cms?"
html_text = ul.urlopen(url).read()
de = DateExtractor()
extract = de.extract({"raw_html" : html_text})
print "\n", "Extracted date : ", extract, "\n" 
#hindustan times
url = "http://www.hindustantimes.com/Entertainment/Bollywood/Bollywood-films-misguided-portrayal-of-rape/Article1-1120836.aspx?htse0022"
html_text = ul.urlopen(url).read()
de = DateExtractor()
extract = de.extract({"raw_html" : html_text})
print "\n", "Extracted date : ", extract, "\n" 
url = "http://www.hindustantimes.com/Entertainment/Bollywood/Krrish-3-Hrithik-Roshan-shows-old-dance-moves-in-new-song/Article1-1120037.aspx"
html_text = ul.urlopen(url).read()
de = DateExtractor()
extract = de.extract({"raw_html" : html_text})
print "\n", "Extracted date : ", extract, "\n" 
url = "http://www.hindustantimes.com/Entertainment/Reviews/Anupama-Chopra-s-review-Madras-Cafe/Article1-1112074.aspx?htse0022"
html_text = ul.urlopen(url).read()
de = DateExtractor()
extract = de.extract({"raw_html" : html_text})
print "\n", "Extracted date : ", extract, "\n" 
# zee news
url = "http://zeenews.india.com/news/haryana/economically-weak-general-category-people-to-get-reservation_875928.html"
html_text = ul.urlopen(url).read()
de = DateExtractor()
extract = de.extract({"raw_html" : html_text})
print "\n", "Extracted date : ", extract, "\n" 
url = "http://zeenews.india.com/news/haryana/haryana-assembly-slams-abhay-chautala-for-selfish-conduct_875840.html"
html_text = ul.urlopen(url).read()
de = DateExtractor()
extract = de.extract({"raw_html" : html_text})
print "\n", "Extracted date : ", extract, "\n" 
# decan herald
url = "http://www.deccanherald.com/content/356522/krishnappa-elected-jds-state-party.html"
html_text = ul.urlopen(url).read()
de = DateExtractor()
extract = de.extract({"raw_html" : html_text})
print "\n", "Extracted date : ", extract, "\n" 
url = "http://www.deccanherald.com/content/356377/she-wanted-them-hanged-burned.html"
html_text = ul.urlopen(url).read()
de = DateExtractor()
extract = de.extract({"raw_html" : html_text})
print "\n", "Extracted date : ", extract, "\n" 
# the hindu
url = "http://www.thehindu.com/news/international/world/afghans-summon-us-envoy-over-civil-war-remark/article5117104.ece?homepage=true"
html_text = ul.urlopen(url).read()
de = DateExtractor()
extract = de.extract({"raw_html" : html_text})
print "\n", "Extracted date : ", extract, "\n" 

url = "http://www.thehindu.com/news/resources/prime-minister-manmohan-singhs-independence-day-speech/article5025006.ece?homepage=true"
html_text = ul.urlopen(url).read()
de = DateExtractor()
extract = de.extract({"raw_html" : html_text})
print "\n", "Extracted date : ", extract, "\n" 

# indian express
url = "http://www.indianexpress.com/news/modi-for-pm-candidate-rajnath-fails-to-persuade-adamant-advani/1167838/"
html_text = ul.urlopen(url).read()
de = DateExtractor()
extract = de.extract({"raw_html" : html_text})
print "\n", "Extracted date : ", extract, "\n" 
"""

url = "http://economictimes.indiatimes.com/news/politics-and-nation/arvind-kejriwal-cites-survey-to-exude-confidence-about-assembly-polls/articleshow/22719793.cms"
html_text = ul.urlopen(url).read()
de = DateExtractor()
extract = de.extract({"raw_html" : html_text})
print "\n", "Extracted date : ", extract, "\n" 
