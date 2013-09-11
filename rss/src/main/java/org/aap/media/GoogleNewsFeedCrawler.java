package org.aap.media;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class GoogleNewsFeedCrawler {
    private static final Logger LOG = Logger.getLogger(GoogleNewsFeedCrawler.class
                    .getCanonicalName());

    private static class CommandLineParser {
        private final Options options = new Options();
        private final GnuParser parser = new GnuParser();
        private CommandLine commandline;

        CommandLineParser() {
            Option feeds = new Option("f", "feeds-file", true, "feeds file");
            feeds.setRequired(true);
            Option output = new Option("o", "output-directory", true,
                            "output directory where crawled files are stored");
            output.setRequired(true);
            options.addOption(feeds);
            options.addOption(output);
        }

        void parse(String[] args) throws ParseException {
            commandline = parser.parse(options, args);
        }

        String getFeedsFile() {
            return commandline.getOptionValue("f");
        }

        String getOutputDirectory() {
            return commandline.getOptionValue("o");
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws ParseException, IllegalArgumentException,
                    MalformedURLException, UnsupportedEncodingException, FileNotFoundException,
                    ClientProtocolException, IOException, FeedException {
        // Command line parsing.
        CommandLineParser command = new CommandLineParser();
        command.parse(args);

        // Know what all is already crawled.
        LOG.info("Finding allready crawled webpages");
        Set<String> crawledURLMd5Set = getAllCrawledURLMd5Set(command.getOutputDirectory());

        // Create output folder for today.
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        File outputDir = new File(command.getOutputDirectory() + File.separatorChar + today);

        for (String feedURL : readFeedsFile(command.getFeedsFile())) {
            SyndFeedInput input = new SyndFeedInput();
            LOG.info("Reading feed: " + feedURL);
            SyndFeed feed = input.build(new XmlReader(new URL(feedURL)));
            for (Object entry : feed.getEntries()) {
                if (!(entry instanceof SyndEntry)) {
                    continue;
                }

                try {
                    SyndEntry syndEntry = (SyndEntry) entry;
                    URL link = new URL(syndEntry.getLink());
                    URL siteURL = new URL(splitQuery(link).get("url"));
                    if (crawledURLMd5Set.contains(getURLMD5(siteURL))) {
                        LOG.info("Already Fetched URL: " + siteURL);
                        continue;
                    }

                    LOG.info("Fetching URL: " + siteURL);
                    File outputDirBySource = new File(outputDir.getAbsolutePath()
                                    + File.separatorChar + getDomain(siteURL));
                    outputDirBySource.mkdirs();
                    String outputFilePrefix = outputDirBySource.getAbsolutePath()
                                    + File.separatorChar + getURLMD5(siteURL);

                    writeToFile(fetchHTML(siteURL), outputFilePrefix + ".html");
                    writeToFile(siteURL.toString() + "\n", outputFilePrefix + ".url");

                    JSONObject obj = new JSONObject();
                    obj.put("url", siteURL.toString());
                    obj.put("title", syndEntry.getTitle());
                    obj.put("publishedDate", syndEntry.getPublishedDate());
                    obj.put("description", Jsoup.parse(syndEntry.getDescription().getValue())
                                    .text());
                    StringWriter out = new StringWriter();
                    obj.writeJSONString(out);
                    writeToFile(out.toString(), outputFilePrefix + ".item");
                } catch (Exception e) {
                    LOG.log(Level.WARNING, "Exception in crawler", e);
                }
            }
        }
    }

    private static List<String> readFeedsFile(String feedsFile) throws IOException {
        LOG.info("Reading feeds file: " + feedsFile);
        List<String> feeds = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(feedsFile));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    LOG.info("Found feed URL: " + line);
                    feeds.add(line);
                }
            }
        } finally {
            br.close();
        }

        return feeds;
    }

    private static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
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

    private static DefaultHttpClient httpClient = null;

    private static DefaultHttpClient getHttpClient() {
        if (httpClient == null) {
            BasicHttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 6000);
            HttpConnectionParams.setSoTimeout(httpParameters, 6000);
            httpClient = new DefaultHttpClient(httpParameters);
        }
        return httpClient;
    }

    private static String fetchHTML(URL url) throws ClientProtocolException, IOException {
        HttpGet get = new HttpGet(url.toString());
        HttpResponse response = getHttpClient().execute(get);
        String html = EntityUtils.toString(response.getEntity(), "UTF-8");
        return html;
    }

    private static String getURLMD5(URL url) {
        return DigestUtils.md5Hex(url.toString());
    }

    private static String getDomain(URL url) {
        return url.getHost();
    }

    private static Set<String> getAllCrawledURLMd5Set(String outputDir) {
        return getAllCrawledURLMd5Set(new File(outputDir));
    }

    private static Set<String> getAllCrawledURLMd5Set(File outputDir) {
        Set<String> crawledURLsMd5 = new HashSet<String>();
        for (File f : outputDir.listFiles()) {
            if (f.isDirectory()) {
                crawledURLsMd5.addAll(getAllCrawledURLMd5Set(f));
            } else if (f.getName().endsWith(".html")) {
                crawledURLsMd5.add(f.getName().replace(".html", ""));
            }
        }
        return crawledURLsMd5;
    }

    private static void writeToFile(String content, String filepath) throws FileNotFoundException {
        PrintWriter fileWriter = new PrintWriter(filepath);
        fileWriter.write(content);
        fileWriter.close();
    }
}
