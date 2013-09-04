package org.aap.media.crawler;

import org.aap.media.utils.URLUtils;

public class HostRules {
	
	
	
	public static boolean isArticlePage(String url) {
		String domain = URLUtils.getDomain(url);
		String host = URLUtils.getHost(url);
		if ("thehindu.com".equals(domain)) {
			if (url.endsWith(".ece")) {
				return true;
			}
		}
		if ("timesofindia.indiatimes.com".equals(host)) {
			if (url.contains("articleshow")) {
				return true;
			}
		}
		if ("hindustantimes.com".equals(domain)) {
			url = url.toLowerCase();
			if (url.contains("article1")) {
				return true;
			}
		}
		if ("indianexpress.com".equals(domain)) {
			if (url.contains("/news/")) {
				return true;
			}
			
		}
		
		return false;
	}

}
