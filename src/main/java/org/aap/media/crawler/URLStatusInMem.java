package org.aap.media.crawler;


import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;


public class URLStatusInMem implements URLStatusInterface{
	protected static final Logger logger = Logger.getLogger(URLStatusInMem.class.getName());
	private ConcurrentHashMap<String, URLStatus> statusMap = new ConcurrentHashMap<String, URLStatus>();
	

	
	public boolean bootstrap()  {
		logger.info("bootstrap called");
		return true;
	}
	
	public boolean isCrawled(String url) {
		url = url.toLowerCase();
		if (statusMap.containsKey(url) ) {
			URLStatus status = statusMap.get(url);
			if(status.status == URLStatus.STATUS_CRAWLED) {
				return true;
			}
		}
		return false;
	}

	public boolean isInProgress(String url) {
		url = url.toLowerCase();
		if (statusMap.containsKey(url) ) {
			URLStatus status = statusMap.get(url);
			if(status.status == URLStatus.STATUS_IN_PROGRESS) {
				return true;
			}
		}
		return false;
	
	}

	public URLStatus getInfo(String url) {
		url = url.toLowerCase();
		if (statusMap.containsKey(url) ) {
			URLStatus status = statusMap.get(url);
			return status;
		}
		return null;
	}

	public void setInProgress(String url) {
		url = url.toLowerCase();
		URLStatus status = null;
		if (statusMap.containsKey(url)) {
			status = statusMap.get(url);
		}
		else {
			status = new URLStatus(url);
		}
		status.status = URLStatus.STATUS_IN_PROGRESS;
		long now = System.currentTimeMillis();
		status.addToProgressTimestamp = now;
		statusMap.put(url, status);
		
	}

	public void setCrawled(String url) {
		url = url.toLowerCase();
		URLStatus status = null;
		if (statusMap.containsKey(url)) {
			status = statusMap.get(url);
		}
		else {
			status = new URLStatus(url);
		}
		status.status = URLStatus.STATUS_CRAWLED;
		long now = System.currentTimeMillis();
		status.crawledTimestamp = now;
		//logger.info("Setting status=CRAWLED for " + url );
		statusMap.put(url, status);
	}
}
