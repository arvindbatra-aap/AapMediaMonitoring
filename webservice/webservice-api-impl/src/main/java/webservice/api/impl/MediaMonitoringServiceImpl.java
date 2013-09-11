package webservice.api.impl;


import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;

import org.aap.monitoring.Article;
import org.aap.monitoring.ArticleCount;
import org.aap.monitoring.SQLManager;
import org.aap.monitoring.SolrManager;
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
	
	public MediaMonitoringServiceImpl(){
		solrManager = new SolrManager();
		try {
			sqlManager = new SQLManager(solrManager);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Collection<Article> getArticles(String keyword, long startDate, long endDate) {
		SolrManager solrManager = new SolrManager();
		try {
			if(startDate == 0 && endDate == 0){
				return solrManager.getArticlesForKeywords(keyword);
			}
			if(endDate == 0 ){
				endDate = new Date().getTime();
			}
			return solrManager.getArticlesForKeywords(keyword, new Date(startDate), new Date(endDate));
		} catch (SolrServerException e) {
			LOG.info("Failed to get articles",e);
		}
		return null;
	}
	
	
	@Override
	public ArticleCount getNumArticles(String keyword, long startDate, long endDate){
		SolrManager solrManager = new SolrManager();
		if(endDate==0){
			endDate = new Date().getTime();
		}
		try {
			return solrManager.getNumArticlesForKeywordsAndDate(keyword, new Date(startDate), new Date(endDate));
		} catch (SolrServerException e) {
			LOG.info("Failed to get articles count",e);
		}
		return null;
	}


	@Override
	public Response triggerIndexer(String date) {
		sqlManager.triggerIndexer(date);
		return null;
	}
}
