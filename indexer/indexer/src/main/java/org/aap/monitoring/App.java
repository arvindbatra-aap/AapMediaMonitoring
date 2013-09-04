package org.aap.monitoring;

import java.sql.SQLException;


/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws SQLException {
        SQLManager sqlManager = new SQLManager(new SolrManager());
        sqlManager.triggerIndexer("\"2013-09-04\"");
 //       SolrManager solrManager = new SolrManager();
 //       solrManager.insertFraudyDocument();
    }
}
