package org.aap.media.crawler;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;

import org.aap.media.utils.AppConstants;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


public class URLStatusInMem implements URLStatusInterface{
	protected static final Logger logger = Logger.getLogger(URLStatusInMem.class.getName());
	private ConcurrentHashMap<String, URLStatus> statusMap = new ConcurrentHashMap<String, URLStatus>();
	
	private void bootstrapDir(File dirFile) {
		for (File file : dirFile.listFiles()) {
			String fname = file.getName();
			if (fname.endsWith(".url")) {
				BufferedReader reader;
				try {
					reader = new BufferedReader(new FileReader(file));
					String url = reader.readLine();
					setCrawled(url);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public boolean bootstrap(String crawlDir)  {
		logger.info("bootstrap called");
		File dirFile = new File(crawlDir);
		if (dirFile == null || !dirFile.isDirectory()) {
			logger.error("No dir exists " + crawlDir);
			return true;
		}
		for (File dateDir : dirFile.listFiles()) {
			for (File domainDir : dateDir.listFiles()) {
				bootstrapDir(domainDir);
			}
		}
		logger.info("Bootstrapped " + statusMap.size() + " urls");
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
