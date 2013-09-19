package webservice.api.impl;


import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;

import org.aap.monitoring.Article;
import org.aap.monitoring.ArticleCount;
import org.aap.monitoring.SQLManager;
import org.aap.monitoring.SolrManager;
import org.aap.monitoring.WordCloud;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Service;

import webservice.api.MediaMonitoringService;


@Service( "media#default" )
public class MediaMonitoringServiceImpl
    implements MediaMonitoringService
{
	private static Logger LOG = Logger.getLogger(MediaMonitoringServiceImpl.class);
	private SolrManager solrManager;
	private SQLManager sqlManager;
	private int THRESHOLD = 4;
	
	public MediaMonitoringServiceImpl(){
		solrManager = new SolrManager();
		try {
			sqlManager = new SQLManager(solrManager);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Collection<Article> getArticles(String keyword, long startDate, long endDate, String src,  int start, int count) {
		try {
			if(startDate == 0 && endDate == 0){
				return solrManager.getArticlesForKeywords(keyword, src, start, count);
			}
			if(endDate == 0 ){
				endDate = new Date().getTime();
			}
			return solrManager.getArticlesForKeywords(keyword, new Date(startDate), new Date(endDate), src, start, count);
		} catch (SolrServerException e) {
			LOG.info("Failed to get articles",e);
		}
		return null;
	}
	
	
	@Override
	public ArticleCount getNumArticles(String keyword, long startDate, long endDate, String src,  int start, int count){
		if(endDate==0){
			endDate = new Date().getTime();
		}
		try {
			return solrManager.getNumArticlesForKeywordsAndDate(keyword, new Date(startDate), new Date(endDate), src, start, count);
		} catch (SolrServerException e) {
			LOG.info("Failed to get articles count",e);
		}
		return null;
	}


	@Override
	public Response triggerIndexer(String date) {
		try {
			sqlManager.triggerIndexer(date);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Collection<Article> getArticlesFromSolr(String solrQuery, int start, int count) {
		try {
			return solrManager.getArticlesForSolrQuery(solrQuery,start,count);
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Integer> getWordCloud(String query, String src, long startDate,  long endDate,int count) {
		WordCloud wc = new WordCloud(solrManager);
		Map<String, Integer> wordCount = wc.getWordCloud(query, null, null, src,count);
		return filterMap(wordCount);
	}
	
	private Map<String,Integer> filterMap(Map<String,Integer> map){
		Map<String ,Integer> newMap = new HashMap<String, Integer>();
		for(String key: map.keySet()){
			if(map.get(key) > THRESHOLD){
				newMap.put(key,map.get(key));
			}
		}
		return newMap;
	}
}
