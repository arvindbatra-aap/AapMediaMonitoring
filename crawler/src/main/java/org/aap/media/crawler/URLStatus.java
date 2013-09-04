package org.aap.media.crawler;


public class URLStatus {
	public static int STATUS_NOT_SEEN = 0;
	public static int STATUS_IN_PROGRESS = 1;
	public static int STATUS_CRAWLED = 2;
	
	public String url;
	public int status;
	public long addToProgressTimestamp = 0;
	public long crawledTimestamp = 0;
	
	
	public URLStatus(String pURL) {
		url = pURL;
		status = STATUS_NOT_SEEN;
	}
	

}
