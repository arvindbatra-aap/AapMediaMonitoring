package webservice.api.impl;


import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.ws.rs.QueryParam;

import webservice.api.MediaMonitoringService;

import org.aap.monitoring.Article;
import org.aap.monitoring.SolrManager;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Service;


@Service( "media#default" )
public class MediaMonitoringServiceImpl
    implements MediaMonitoringService
{
	private static Logger LOG = Logger.getLogger(MediaMonitoringServiceImpl.class);
	@Override
	public Collection<Article> getArticles(String keyword) {
		SolrManager solrManager = new SolrManager();
		try {
			return solrManager.getArticlesForKeywords(keyword);
		} catch (SolrServerException e) {
			LOG.info("Failed to get articles",e);
		}
		return null;
	}
	
	
	@Override
	public Map<String, Map<String, Integer>> getNumArticles(String keyword){
		SolrManager solrManager = new SolrManager();
		Date sD = new Date();
		Date eD = new Date(sD.getTime() - 10*24*1000*60*60);
		try {
			//return solrManager.getArticlesForKeywords(keyword);
			return solrManager.getNumArticlesForKeywordsAndDate(keyword, sD, eD);
		} catch (SolrServerException e) {
			LOG.info("Failed to get articles count",e);
		}
		return null;
	}
}
