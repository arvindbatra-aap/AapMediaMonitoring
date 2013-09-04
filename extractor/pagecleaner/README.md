Building and running page cleaner:

mvn package exec:java -Dexec.mainClass=org.aap.monitoring.pagecleaner.APIServer

POST mode:

curl -X POST -d @input.html http://0.0.0.0:2121/api/content --header "Content-Type:text/html" (where input.html contains the raw article html)

GET mode:

http://localhost:2121/api/content?url=http://www.thehindu.com/news/national/lok-sabha-passes-pension-bill/article5092823.ece?homepage=true
