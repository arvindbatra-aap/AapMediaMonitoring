package org.aap.monitoring;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;


import java.text.DateFormat;
import java.util.*;

public class SolrManager {
    public static SolrServer solrServer = new HttpSolrServer("http://localhost:8983/solr");
    public void insertDocument(ResultSet result) {
        try {
        	System.out.println("Inserting a document");
            SolrInputDocument inputDocument = new SolrInputDocument();
            inputDocument.addField("src", result.getString("src"));
            inputDocument.addField("url", result.getString("url"));
            inputDocument.addField("title", result.getString("title"));
            inputDocument.addField("date", result.getDate("publishedDate"));
            inputDocument.addField("image_url", result.getString("imageUrl"));
            inputDocument.addField("content", result.getString("content"));
            inputDocument.addField("author", result.getString("author"));
            inputDocument.addField("category", result.getString("category"));
            inputDocument.addField("comments", result.getString("comments"));
            inputDocument.addField("country", result.getString("country"));
            inputDocument.addField("city", result.getString("city"));
            inputDocument.addField("commentcount", result.getInt("commentCount"));
            solrServer.add(inputDocument);
            solrServer.commit();
        } catch (Exception e) {
            System.err.println("Exception aa gayi baap. Ab kya hoga ??");
            e.printStackTrace();
        }
    }

    public void insertFraudyDocument(String url, String content, Date date) {
        try {
            SolrInputDocument inputDocument = new SolrInputDocument();
            inputDocument.addField("src", "dummysrc");
            inputDocument.addField("url", url);
            inputDocument.addField("title", "dummytitle");
            inputDocument.addField("date", date);
            inputDocument.addField("image_url", "dummy_image_url");
            inputDocument.addField("content", content);
            inputDocument.addField("author", "dummy_author");
            inputDocument.addField("category", "dummy_category");
            inputDocument.addField("comments", "dummy_comments");
            inputDocument.addField("country", "dummy_country");
            inputDocument.addField("city", "dummy_city");
            inputDocument.addField("commentcount", 55);
            solrServer.add(inputDocument);
            solrServer.commit();
            System.out.println("Added a doc");
        } catch (Exception e) {
            System.err.println("Exception aa gayi baap. Ab kya hoga ??");
            e.printStackTrace();
        }
    }


    private SolrQuery getQueryForKeywords(String keywords) {
        return new SolrQuery().setQuery("content:" + keywords);
    }

    private void createDateFilter(SolrQuery solrQuery, Date startDate, Date endDate){
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("GMT+0530"));
        String dateQuery = "date:" + "[" + df.format(startDate) + " TO " + df.format(endDate) + "]";
        System.out.println(dateQuery);
        solrQuery.addFilterQuery(dateQuery);
    }

    public List<Article> getArticlesForKeywords(String keywords) throws SolrServerException {
        QueryResponse response = solrServer.query(getQueryForKeywords(keywords));
        return getArticles(response);
    }

    public List<Article> getArticlesForKeywords(String keywords, Date startDate, Date endDate) throws SolrServerException {
    	SolrQuery solrQuery = getQueryForKeywords(keywords);
    	createDateFilter(solrQuery,startDate,endDate);
        QueryResponse response = solrServer.query(solrQuery);
        return getArticles(response);
    }

    private List<Article> getArticles(QueryResponse response) {
        List<Article> result = new ArrayList<Article>();
        SolrDocumentList object = (SolrDocumentList) response.getResponse().get("response");
        for (SolrDocument doc : object) {
            Article article = Article.getArticleFrom(doc);
            result.add(article);
        }
        return result;
    }

    public ArticleCount getNumArticlesForKeywordsAndDate(String keywords, Date startDate, Date endDate) throws SolrServerException {
        SolrQuery solrQuery = getQueryForKeywords(keywords);
        createDateFilter(solrQuery,startDate,endDate);
        solrQuery.setFacet(true);
        solrQuery.setRows(0);

        solrQuery.set("facet.method", "enum");
        solrQuery.set("facet.limit", "-1");
        solrQuery.addFacetPivotField("date,src");
        solrQuery.addFacetPivotField("src,date");

        QueryResponse response = solrServer.query(solrQuery);
        NamedList<List<PivotField>> pivots = response.getFacetPivot();
        List<PivotField> dateSource = pivots.get("date,src");

        ArticleCount artCountRes  = new ArticleCount();
        Map<String, Map<String, Integer>> dateResults = new HashMap<String, Map<String, Integer>>();
        for (PivotField dateOnly : dateSource) {
            String dateString = ((Date) dateOnly.getValue()).toString();
            if (dateResults.get(dateString) == null ) dateResults.put(dateString, new HashMap<String, Integer>());
            for (PivotField source: dateOnly.getPivot()) {
            	dateResults.get(dateString).put((String) source.getValue(), source.getCount());
            }
        }
        
        List<PivotField> sourceDate = pivots.get("src,date");
        Map<String, Map<String, Integer>> srcResults = new HashMap<String, Map<String, Integer>>();
        for (PivotField sourceOnly : sourceDate) {
            String sourceString = (String) sourceOnly.getValue();
            if (srcResults.get(sourceString) == null ) srcResults.put(sourceString, new HashMap<String, Integer>());
            for (PivotField date: sourceOnly.getPivot()) {
            	srcResults.get(sourceString).put(((Date) date.getValue()).toString(), date.getCount());
            }
        }
        artCountRes.setCountByDate(dateResults);
        artCountRes.setCountBySrc(srcResults);
        return artCountRes;
    }
}
