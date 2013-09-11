package webservice.api.impl;


import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.ws.rs.QueryParam;

import webservice.api.MediaMonitoringService;

import org.aap.monitoring.Article;
import org.aap.monitoring.ArticleCount;
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
	public ArticleCount getNumArticles(String keyword, Date startDate, Date endDate){
		SolrManager solrManager = new SolrManager();
		Date eD = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(eD);
		cal.add(Calendar.DAY_OF_MONTH, -41);
		Date sD  = cal.getTime();
		try {
			//return solrManager.getArticlesForKeywords(keyword);
			System.out.println(sD + " " + eD);
			return solrManager.getNumArticlesForKeywordsAndDate(keyword, sD, eD);
		} catch (SolrServerException e) {
			LOG.info("Failed to get articles count",e);
		}
		return null;
	}
}
