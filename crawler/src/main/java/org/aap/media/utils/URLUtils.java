package org.aap.media.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tools.ant.filters.StringInputStream;

import edu.uci.ics.crawler4j.parser.ExtractedUrlAnchorPair;
import edu.uci.ics.crawler4j.parser.HtmlContentHandler;
import edu.uci.ics.crawler4j.url.URLCanonicalizer;

public class URLUtils {
	public static String fetchHTML(String url) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse response = httpClient.execute(get);
		String html = EntityUtils.toString(response.getEntity(), "UTF-8");
		return html;
	}
	
	
	public static boolean isRelativeURL(String url) {
		if (url != null && url.startsWith("/")) {
			return true;
		}
		return false;
	}
	
	public static String getAbsoluteUrl(String absUrl, String url) {
		if (isRelativeURL(url)) {
			URL urlObj;
			try {
				urlObj = new URL(absUrl);
				String op = urlObj.getProtocol() + "://"+ urlObj.getHost() + url;
				return op;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return url;
	}
	
	public static String getURLMD5(String url)  {
		return DigestUtils.md5Hex(url);
		
	}
	
	
	public static String getHost(String url) {
		URL urlObj;
		try {
			urlObj = new URL(url);
			String host = urlObj.getHost();
			return host;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	public static String getDomain(URL urlObj) {
		if (urlObj == null) {
			return null;
		}
		String host = urlObj.getHost();
		String [] arr = host.split("\\.");
		if (arr.length <= 2) {
			return host;
		}
		String domain = arr[arr.length - 2] + "." +  arr[arr.length-1];
		return domain;
	}
	
	public static String getDomain(String url) {
		URL urlObj;
		try {
			urlObj = new URL(url);
			String host = urlObj.getHost();
			String [] arr = host.split("\\.");
			if (arr.length <= 2) {
				return host;
			}
			String domain = arr[arr.length - 2] + "." +  arr[arr.length-1];
			return domain;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	public static String getSubdomain(String url) {
		URL urlObj;
		try {
			urlObj = new URL(url);
			String host = urlObj.getHost();
			String [] arr = host.split("\\.");
			if (arr.length <= 2) {
				return "";
			}
			int index = 0;
			if (arr[index].equals("www") || arr[index].equals("m")) {
				index++;
				//no subdomain check
				if (arr.length == 3) {
					return "";
				}
			}
			return arr[index];
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	
	public static List<String> getOutgoingURLs(String content, String contextURL) throws Exception {
		HtmlParser htmlParser = new HtmlParser();
		InputStream is = new StringInputStream(content, "utf8");
		Metadata metadata = new Metadata();
		HtmlContentHandler contentHandler = new HtmlContentHandler();
		ParseContext parseContext = new ParseContext();
		htmlParser.parse(is, contentHandler, metadata, parseContext);
		List<String> outgoingUrls = new ArrayList<String>();
		String baseURL = contentHandler.getBaseUrl();
		if (baseURL != null) {
			contextURL = baseURL;
		}

		int urlCount = 0;
		for (ExtractedUrlAnchorPair urlAnchorPair : contentHandler.getOutgoingUrls()) {
			String href = urlAnchorPair.getHref();
			href = href.trim();
			if (href.length() == 0) {
				continue;
			}
			String hrefWithoutProtocol = href.toLowerCase();
			if (href.startsWith("http://")) {
				hrefWithoutProtocol = href.substring(7);
			}
			if (!hrefWithoutProtocol.contains("javascript:") 
					&& !hrefWithoutProtocol.contains("mailto:")
					&& !hrefWithoutProtocol.contains("@")) {
				String canonicalURL = URLCanonicalizer.getCanonicalURL(href, contextURL);
				if (canonicalURL != null) {
					outgoingUrls.add(canonicalURL);
					urlCount++;
				}
			}
		}
		
		return outgoingUrls;
		
	}

}
