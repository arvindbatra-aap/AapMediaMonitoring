trigDate=$1
cd /root/prod/api/AapMediaMonitoring/indexer/indexer
export MAVEN_OPTS="-Xmx256m -Dfile.encoding=UTF8"
/root/packages/apache-maven-3.1.0/bin/mvn clean package exec:java -Dexec.mainClass=org.aap.monitoring.TriggerMain -Dexec.args="trigDate" 2>&1 | tee logs/$trigDate.log
