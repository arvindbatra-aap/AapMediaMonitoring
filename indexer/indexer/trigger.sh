trigDate=$1
cd /root/prod/api/AapMediaMonitoring/indexer/indexer
export MAVEN_OPTS="-Xmx256m -Dfile.encoding=UTF8"
mvn exec:java  -Dlog4j.configuration=file:/root/prod/api/AapMediaMonitoring/indexer/indexer/trigger.log4j -Dexec.mainClass=org.aap.monitoring.TriggerMain -Dexec.args="$trigDate" 2>&1 | tee logs/$trigDate.log
