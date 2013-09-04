package org.aap.media.crawler;

public interface URLStatusInterface {
	
	public boolean bootstrap(String crawlDir);
	public boolean isCrawled(String url);
	public boolean isInProgress(String url);
	public URLStatus getInfo(String url);
	public void setInProgress(String url);
	public void setCrawled(String url);

}

