import urllib2 as ul

from date import *

bad_title_phrase_list = ["Hindustan Times"]
# not working due to bad title match

# works

# times of of India
# url = "http://timesofindia.indiatimes.com/india/Curfew-relaxed-in-Muzaffarnagar-clashes-erupt-in-Baghpat/articleshow/22490767.cms"
# url = "http://timesofindia.indiatimes.com/india/Indians-less-happy-than-Pakistanis-Bangladeshis-UN-report/articleshow/22492120.cms?"

#hindustan times
# url = "http://www.hindustantimes.com/Entertainment/Bollywood/Bollywood-films-misguided-portrayal-of-rape/Article1-1120836.aspx?htse0022"
# url = "http://www.hindustantimes.com/Entertainment/Bollywood/Krrish-3-Hrithik-Roshan-shows-old-dance-moves-in-new-song/Article1-1120037.aspx"
# url = "http://www.hindustantimes.com/Entertainment/Reviews/Anupama-Chopra-s-review-Madras-Cafe/Article1-1112074.aspx?htse0022"

# zee news
# url = "http://zeenews.india.com/news/haryana/economically-weak-general-category-people-to-get-reservation_875928.html"
# url = "http://zeenews.india.com/news/haryana/haryana-assembly-slams-abhay-chautala-for-selfish-conduct_875840.html"

# decan herald
# url = "http://www.deccanherald.com/content/356522/krishnappa-elected-jds-state-party.html"
url = "http://www.deccanherald.com/content/356377/she-wanted-them-hanged-burned.html"

# the hindu
# url = "http://www.thehindu.com/news/international/world/afghans-summon-us-envoy-over-civil-war-remark/article5117104.ece?homepage=true"
# url = "http://www.thehindu.com/news/resources/prime-minister-manmohan-singhs-independence-day-speech/article5025006.ece?homepage=true"

# indian express
# url = "http://www.indianexpress.com/news/modi-for-pm-candidate-rajnath-fails-to-persuade-adamant-advani/1167838/"
# url = "http://www.indianexpress.com/news/watch-hrithik-roshans-rocking-moves-in-krrish-3-song-raghupati-raghav-teaser/1167595/?rheditorpick"

html_text = ul.urlopen(url).read()

de = DateExtractor()
date = de.extract({"source" : html_text})
print "\n", "Extracted date : ", date, "\n" 
