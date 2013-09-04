package org.aap.media;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class GoogleNewsFeedCrawler {
    public static void main(String[] args) throws IllegalArgumentException, MalformedURLException,
                    FeedException, IOException {
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input
                        .build(new XmlReader(
                                        new URL(
                                                        "http://news.google.com/news?pz=1&cf=all&ned=us&hl=en&q=%22aam+aadmi+party%22+OR+%22arvind+kejriwal%22&ncl=d6h8A3PoOAskShMk60p443tgcC24M&cf=all&output=rss")));
        for (Object entry : feed.getEntries()) {
            if (entry instanceof SyndEntry) {
                SyndEntry syndEntry = (SyndEntry) entry;
                URL link = new URL(syndEntry.getLink());
                URL siteURL = new URL(splitQuery(link).get("url"));
                System.out.println(siteURL);

                String htmlFileName = getURLMD5(siteURL) + ".html";
                File htmlFile = new File(htmlFileName);
                if (!htmlFile.exists()) {
                    PrintWriter htmlFileWriter = new PrintWriter(htmlFile);
                    htmlFileWriter.write(fetchHTML(siteURL));
                    htmlFileWriter.close();

                    PrintWriter urlFileWriter = new PrintWriter(getURLMD5(siteURL) + ".url");
                    urlFileWriter.write(siteURL.toString() + "\n");
                    urlFileWriter.close();
                }
            }
        }
    }

    public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                            URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    public static String fetchHTML(URL url) throws ClientProtocolException, IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet get = new HttpGet(url.toString());
        HttpResponse response = httpClient.execute(get);
        String html = EntityUtils.toString(response.getEntity(), "UTF-8");
        return html;
    }

    public static String getURLMD5(URL url) {
        return DigestUtils.md5Hex(url.toString());
    }
}
