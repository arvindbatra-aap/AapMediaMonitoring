package org.aap.monitoring;

import java.sql.ResultSet;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class SolrManager {
    public static SolrServer solrServer = new HttpSolrServer("http://localhost:8983/solr");

    public void insertDocument(ResultSet result) {
        try {
            SolrInputDocument inputDocument = new SolrInputDocument();
            inputDocument.addField("src", result.getString("src"));
            inputDocument.addField("url", result.getString("url"));
            inputDocument.addField("title", result.getString("title"));
            inputDocument.addField("date", result.getDate("date"));
            inputDocument.addField("image_url", result.getString("image_url"));
            inputDocument.addField("content", result.getString("content"));
            inputDocument.addField("author", result.getString("author"));
            inputDocument.addField("category", result.getString("category"));
            inputDocument.addField("comments", result.getString("comments"));
            inputDocument.addField("country", result.getString("country"));
            inputDocument.addField("city", result.getString("city"));
            inputDocument.addField("commentcount", result.getInt("commentcount"));
            solrServer.add(inputDocument);
            solrServer.commit();
        } catch (Exception e) {
            System.err.println("Exception aa gayi baap. Ab kya hoga ??");
            e.printStackTrace();
        }
    }

    public void insertFraudyDocument(ResultSet result) {
        try {
            SolrInputDocument inputDocument = new SolrInputDocument();
            inputDocument.addField("src", result.getString("src"));
            inputDocument.addField("url", result.getString("url"));
            inputDocument.addField("title", result.getString("title"));
            inputDocument.addField("date", result.getDate("date"));
            inputDocument.addField("image_url", result.getString("image_url"));
            inputDocument.addField("content", result.getString("content"));
            inputDocument.addField("author", result.getString("author"));
            inputDocument.addField("category", result.getString("category"));
            inputDocument.addField("comments", result.getString("comments"));
            inputDocument.addField("country", result.getString("country"));
            inputDocument.addField("city", result.getString("city"));
            inputDocument.addField("commentcount", result.getInt("commentcount"));
            solrServer.add(inputDocument);
            solrServer.commit();
        } catch (Exception e) {
            System.err.println("Exception aa gayi baap. Ab kya hoga ??");
            e.printStackTrace();
        }
    }
}
