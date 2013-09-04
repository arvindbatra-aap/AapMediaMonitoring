package webservice.api.impl;


import java.util.Collection;

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
}
