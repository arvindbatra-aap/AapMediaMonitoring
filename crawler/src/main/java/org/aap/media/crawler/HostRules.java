package org.aap.media.crawler;

import org.aap.media.utils.URLUtils;

public class HostRules {
	
	
	
	public static boolean isArticlePage(String url) {
		String domain = URLUtils.getDomain(url);
		if ("thehindu.com".equals(domain)) {
			if (url.endsWith(".ece")) {
				return true;
			}
		}
		
		return false;
	}

}
