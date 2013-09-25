# Identify the script directory
base_dir=$( cd $(dirname $0) ; pwd -P )

# Rebuild the indexer 
cd $base_dir/indexer/indexer
#mvn clean install

# Rebuild the webservice
cd $base_dir/webservice
#mvn clean package 

tomcat_webapp_dir="/var/lib/tomcat6/webapps/"
# Copy the war to the tomcat webapps folder
cp webservice-webapp/target/ROOT.war $tomcat_webapp_dir

# Stop tomcat6.
/etc/init.d/tomcat6 stop

# Cleanup the webconsole app as preparation to rebuild the app.
rm -rf $tomcat_webapp_dir/ROOT/*

# Extract the war into app folder.
/usr/bin/unzip -q $tomcat_webapp_dir/ROOT.war -d $tomcat_webapp_dir/ROOT

# Copy jars into war folder
mv $tomcat_webapp_dir/ROOT/WEB-INF/lib/mysql-connector-java*.jar /usr/share/tomcat6/lib/

# Start tomcat server.
/etc/init.d/tomcat6 start
