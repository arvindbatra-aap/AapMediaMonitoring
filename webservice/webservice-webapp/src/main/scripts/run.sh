cp $1 /usr/share/tomcat6/webapps/ROOT.war 
# Stop tomcat6.
/etc/init.d/tomcat6 stop

# Cleanup the webconsole app as preparation to rebuild the app.
rm -rf /usr/share/tomcat6/webapps/ROOT/*

# Extract the war into app folder.
/usr/bin/unzip -q /usr/share/tomcat6/webapps/ROOT.war -d /usr/share/tomcat6/webapps/ROOT

# Copy jars into war folder
mv /usr/share/tomcat6/webapps/ROOT/WEB-INF/lib/mysql-connector-java*.jar /usr/share/tomcat6/lib/

# Start tomcat server.
/etc/init.d/tomcat6 start