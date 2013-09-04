package org.aap.media.crawler;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.aap.media.utils.ConfigUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;



public class CrawlController {
	protected static final Logger logger = Logger.getLogger(CrawlController.class.getName());
	private ConcurrentLinkedQueue<WebURL> mUrlQueue = new ConcurrentLinkedQueue<WebURL>();
	
	public static final int DEFAULT_NUM_THREADS = 1;
	public static final int DEFAULT_CRAWL_DEPTH = 1;
	int numThreads = DEFAULT_NUM_THREADS;
	public int crawlDepth = DEFAULT_CRAWL_DEPTH;
	
	private PageFetcher mPageFetcher;
	private URLStatusInterface mUrlStatus;
	private RobotstxtServer mRobotstxtServer;
	private CrawlConfig mCrawlConfig;
	
	
	public ConcurrentLinkedQueue<WebURL> getUrlQueue() {
		return mUrlQueue;
	}
	
	
	
	public URLStatusInterface getUrlStatus() {
		return mUrlStatus;
	}

	

	public CrawlConfig getCrawlConfig() {
		return mCrawlConfig;
	}

	public PageFetcher getPageFetcher() {
		return mPageFetcher;
	}

	public RobotstxtServer getRobotstxtServer() {
		return mRobotstxtServer;
	}

	
	private void initSeedUrls(String file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		String line = null;
		while ((line=reader.readLine()) != null) {
			String url = line.trim();
			if (url.startsWith("#")) {
				continue;
			}
			logger.info("Seeded url :" + url);
			mUrlQueue.add(getDefaultWebUrlObj(url));
		}
	}
	
	
	
	public CrawlController() throws FileNotFoundException {
		String configFile = "src/main/resources/crawl_config.properties";
		InputStream configIS = new FileInputStream(configFile);
		Properties configProps = new Properties();
		try {
			configProps.load(configIS);
		} catch (IOException e1) {
			logger.error("error in loading config file, returning");
			e1.printStackTrace();
			return;
		}
		numThreads = ConfigUtils.getConfigValueAsInt(configProps, "num_threads", -1);
		crawlDepth = ConfigUtils.getConfigValueAsInt(configProps, "crawl_depth", -1);
		int ignoreBootstrap = ConfigUtils.getConfigValueAsInt(configProps, "ignore_bootstrap", 1);
		String seedUrlsFile = ConfigUtils.getConfigValueAsString(configProps, "seed_urls_file", "");
		String crawlDir = ConfigUtils.getConfigValueAsString(configProps, "crawl_dir", null);
		logger.info("Num threads: " + numThreads);
		logger.info("Crawl Depth: " + crawlDepth);
		logger.info("To ignore bootstrap :" + ignoreBootstrap);
		logger.info("Seed urls file: " + seedUrlsFile);
		
		logger.info("Crawl dir" + crawlDir);
		try {
			initSeedUrls(seedUrlsFile);
		} catch (IOException e) {
			logger.error("Cant find seed urls file");
			e.printStackTrace();
		}
		
		mCrawlConfig = new CrawlConfig();
		mCrawlConfig.setFollowRedirects(true);
		if (crawlDir != null && crawlDir.length() != 0) {
			mCrawlConfig.setCrawlStorageFolder(crawlDir);
		}
		mCrawlConfig.setMaxDepthOfCrawling(crawlDepth);
		mUrlStatus = new URLStatusInMem();
		if (ignoreBootstrap == 0) {
			boolean bootstrapSuccess = mUrlStatus.bootstrap();
			if (!bootstrapSuccess) {
				logger.error("Bootstrap failed, exiting");
				System.exit(-1);
			}
		} else {
			logger.info("Not bootstrapping");
		}
		mPageFetcher = new PageFetcher(mCrawlConfig);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
    mRobotstxtServer = new RobotstxtServer(robotstxtConfig, mPageFetcher);

	}

	
	
	
	public static WebURL  getDefaultWebUrlObj(String url) {
		WebURL webURL = new WebURL();
		webURL.setURL(url);
		webURL.setDepth((short) 0);
		return webURL;
	}
	
	public void initQueue() {
		
	}
	
	public Page fetchOnePage(String url) {
		mCrawlConfig.setMaxDepthOfCrawling(0);
		URLFetcher uf = new URLFetcher(this);
		Page page = uf.processPage(getDefaultWebUrlObj(url));
		return page;
		
	}
	
	
	public void startCrawlers() {
		final List<Thread> threads = new ArrayList<Thread>();
		final List<URLFetcher> crawlers = new ArrayList<URLFetcher>();
		
		for (int i = 0; i < numThreads; i++) {
			URLFetcher uf = new URLFetcher(this);
			Thread thread = new Thread(uf, "Crawler " + i);
			thread.start();
			crawlers.add(uf);
			threads.add(thread);
			logger.info("Crawler " + i + " started.");
		}
		
		//try to see if there is any new data in queue
		int attempts = 0;
		while (true) {
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			WebURL obj = mUrlQueue.peek();
			if (obj == null) {
				attempts += 1;
			}
			else {
				attempts = 0;
			}
			logger.info("No data attempt #" + attempts);
			if (attempts >= 5) {
				break;
			}
		}
		//time to shutdown.
		for (int i = 0; i < crawlers.size(); i++) {
			crawlers.get(i).stopProcessing();
			logger.info("Shutting down crawler " + i);
		}
		
		for (int i = 0; i < threads.size(); i++) {
			try {
				
				threads.get(i).join();
				logger.info("Shutting down thread " + i + " " + threads.get(i).getName());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		shutdown();
	}
	
	
	
	public void shutdown() {
		System.out.println("Shutting down");
		
		mPageFetcher.shutDown();
		
	}
	
	public static void main(String [] args) throws IOException {
		// Set up a simple configuration that logs on the console.
	    PropertyConfigurator.configure("src/main/resources/log4j.properties");
		logger.debug("Hello world");
		CrawlController crawlController = new CrawlController();
		crawlController.initQueue();
		crawlController.startCrawlers();
	}

}
