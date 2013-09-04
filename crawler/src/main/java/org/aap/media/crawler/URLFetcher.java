package org.aap.media.crawler;



import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.aap.media.utils.URLUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.fetcher.CustomFetchStatus;
import edu.uci.ics.crawler4j.fetcher.PageFetchResult;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.ParseData;
import edu.uci.ics.crawler4j.parser.Parser;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;

public class URLFetcher implements Runnable {
	protected static final Logger logger = Logger.getLogger(URLFetcher.class
			.getName());
	final Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|ico|jpe?g"
			+ "|png|tiff?|mid|mp2|mp3|mp4" + "|wav|avi|mov|mpeg|ram|m4v|pdf"
			+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$", Pattern.CASE_INSENSITIVE);

	final Pattern URL_DISCARD_PATTERN1 = Pattern.compile(".*/(rss|blogs?|photo)/.*",
			Pattern.CASE_INSENSITIVE);
	final Pattern URL_DISCARD_PATTERN2 = Pattern.compile(".*(blog\\.).*",
			Pattern.CASE_INSENSITIVE);

	private SitemapCrawlController mCrawlController;
	private PageFetcher mPageFetcher;
	private URLStatusInterface mUrlStatus;
	private RobotstxtServer mRobotstxtServer;
	private Parser mParser;
	private boolean toStopProcessing = false;
	HTMLWriter htmlWriter = null;

	public URLFetcher(SitemapCrawlController controller) {
		mCrawlController = controller;
		mUrlStatus = controller.getUrlStatus();
		mPageFetcher = controller.getPageFetcher();
		mRobotstxtServer = controller.getRobotstxtServer();
		mParser = new Parser(mCrawlController.getCrawlConfig());
		toStopProcessing = false;
		if (mCrawlController.getCrawlConfig().getCrawlStorageFolder().length() > 0) {
    	htmlWriter = new HTMLWriter(mCrawlController.getCrawlConfig().getCrawlStorageFolder());
    }

	}

	public void stopProcessing() {
		toStopProcessing = true;
	}

	private boolean isSeenBefore(String url) {
		if (this.mUrlStatus.isInProgress(url) || this.mUrlStatus.isCrawled(url)) {
			return true;
		}
		return false;
	}

	private boolean toFilterURL(String url) {

		if (FILTERS.matcher(url).matches()) {
			return true;
		}
		if (URL_DISCARD_PATTERN1.matcher(url).matches()
				|| URL_DISCARD_PATTERN2.matcher(url).matches()) {
			return true;
		}
		URL urlObj;
		try {
			urlObj = new URL(url);
			String path = urlObj.getPath();
			if (FILTERS.matcher(path).matches()) {
				return true;
			}
			return false;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		}

	}
	
	
	private boolean toCrawlUrl(WebURL urlObj, WebURL parentURLObj) {
		
		String url = urlObj.getURL();
		if (toFilterURL(url)) {
			return false;
		}
		if (parentURLObj != null && url != null) {
			String parentDomain = URLUtils.getDomain(parentURLObj.getURL());
			String urlDomain = URLUtils.getDomain(url);
			if (parentDomain == null || !parentDomain.equalsIgnoreCase(urlDomain)) {
				return false;
			}
		}
		if (isSeenBefore(url)) {
			if (logger.isDebugEnabled()) {
				logger.debug(urlObj.toString() + " ignored as it is seen before");
			}
			return false;
		}
		if (urlObj.getDepth() > mCrawlController.getCrawlConfig()
				.getMaxDepthOfCrawling()) {
			return false;
		}
		return true;

	}

	private void addToQueue(WebURL webURL) {
		mUrlStatus.setInProgress(webURL.getURL());
		mCrawlController.getUrlQueue().add(webURL);
	}

	private void postProcessorCrawledURL(Page page) throws MalformedURLException {
		String url = page.getWebURL().getURL();
		mUrlStatus.setCrawled(page.getWebURL().getURL());
		if (HostRules.isArticlePage(url)) {
			if (htmlWriter != null) {
				htmlWriter.write(url, page);
			}
		}
		

	}

	/**
	 * This function is called if the content of a url could not be fetched.
	 * 
	 * @param webUrl
	 */
	protected void onContentFetchError(WebURL webUrl) {
		logger.error("onContentFetchError " + webUrl.toString());
		this.mUrlStatus.setCrawled(webUrl.getURL());
	}

	/**
	 * This function is called if there has been an error in parsing the content.
	 * 
	 * @param webUrl
	 */
	protected void onParseError(WebURL webUrl) {
		logger.error("onParseError " + webUrl.toString());
		this.mUrlStatus.setCrawled(webUrl.getURL());
	}

	protected void onHeaderFetchError(WebURL webUrl) {
		logger.error("onHeaderFetchError " + webUrl.toString());
		
		this.mUrlStatus.setCrawled(webUrl.getURL());
	}

	private void processRedirectPage(PageFetchResult fetchResult, WebURL curURL) {
		String movedToUrl = fetchResult.getMovedToUrl();
		if (movedToUrl == null) {
			return;
		}
		WebURL webURL = new WebURL();
		webURL.setURL(movedToUrl);
		webURL.setParentDocid(curURL.getParentDocid());
		webURL.setParentUrl(curURL.getParentUrl());
		webURL.setDepth(curURL.getDepth());
		webURL.setDocid(-1);
		webURL.setAnchor(curURL.getAnchor());
		if (!toCrawlUrl(webURL, curURL)) {
			return;
		}
		if (mRobotstxtServer.allows(webURL)) {
			addToQueue(webURL);
		}
	}

	public Page processPage(WebURL curURL) {
		
		if (curURL == null) {
			return null;
		}
		
		PageFetchResult fetchResult = null;
		try {

			fetchResult = mPageFetcher.fetchHeader(curURL);
			int statusCode = fetchResult.getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY
						|| statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
					if (this.mCrawlController.getCrawlConfig().isFollowRedirects()) {
						processRedirectPage(fetchResult, curURL);
					}
				} else if (fetchResult.getStatusCode() == CustomFetchStatus.PageTooBig) {
					logger
							.info("Skipping a page which was bigger than max allowed size: "
									+ curURL.getURL());
				} else {
					// some error. atleast add to the statusMap
					onHeaderFetchError(curURL);
				}
				return null;
			}

			if (!curURL.getURL().equals(fetchResult.getFetchedUrl())) {
				if (isSeenBefore(fetchResult.getFetchedUrl())) {
					return null;
				}
				curURL.setURL(fetchResult.getFetchedUrl());
			}

			Page page = new Page(curURL);
			if (!fetchResult.fetchContent(page)) {
				onContentFetchError(curURL);
				return null;
			}
			if (!mParser.parse(page, curURL.getURL())) {
				onParseError(curURL);
				return null;
			}

			ParseData parseData = page.getParseData();
			if (parseData instanceof HtmlParseData) {
				HtmlParseData htmlParseData = (HtmlParseData) parseData;
				for (WebURL webURL : htmlParseData.getOutgoingUrls()) {
					webURL.setParentUrl(curURL.getURL());

					if (this.isSeenBefore(webURL.getURL())) {
						continue;
					} else {
						webURL.setDepth((short) (curURL.getDepth() + 1));
						if (toCrawlUrl(webURL, curURL) && mRobotstxtServer.allows(webURL)) {
							// logger.debug("adding to queue " + webURL.getURL());
							addToQueue(webURL);
						} else {
							// log for debug
						}
					}
				}
			}
			try {
				postProcessorCrawledURL(page);
			} catch (Exception e) {
				logger.error("Exception while running the visit method. Message: '"
						+ e.getMessage() + "' at " + e.getStackTrace()[0]);
			}

			return page;
		} catch (Exception e) {
			logger.error(e.getMessage() + ", while processing: " + curURL.getURL());
			e.printStackTrace();
		} finally {
			if (fetchResult != null) {
				fetchResult.discardContentIfNotConsumed();
			}
		}
		return null;
	}

	public void run() {
		while (true) {
			// check if queue has data
			if (toStopProcessing) {
				logger.info("To Stop processing is true , exiting");
				break;
			}
			WebURL obj = this.mCrawlController.getUrlQueue().peek();
			if (obj == null) {
				try {
					Thread.sleep(2000);
					continue;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			List<WebURL> toProcessList = new ArrayList<WebURL>();
			int numItemsToDequeue = 50;
			for (int i = 0; i < numItemsToDequeue; i++) {
				WebURL webURL = this.mCrawlController.getUrlQueue().poll();
				if (webURL == null) {
					break;
				}
				toProcessList.add(webURL);
			}
			for (int i = 0; i < toProcessList.size(); i++) {
				processPage(toProcessList.get(i));
			}
			toProcessList.clear();
		}
		logger.info("Cleaning up Fetcher");
		cleanup();
	}

	public void cleanup() {

	}

}
