package org.aap.media;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import org.apache.http.util.EntityUtils;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class GoogleNewsFeedCrawler {
    private static class CommandLineParser {
        private final Options options = new Options();
        private final GnuParser parser = new GnuParser();
        private CommandLine commandline;

        CommandLineParser() {
            Option dryRun = new Option("n", "dry-run", false, "dry run");
            Option feeds = new Option("f", "feeds-file", true, "feeds file");
            feeds.setRequired(true);
            Option output = new Option("o", "output-directory", true,
                            "output directory where crawled files are stored");
            output.setRequired(true);
            options.addOption(dryRun);
            options.addOption(feeds);
            options.addOption(output);
        }

        void parse(String[] args) throws ParseException {
            commandline = parser.parse(options, args);
        }

        boolean isDryRun() {
            return commandline.hasOption("n");
        }

        String getFeedsFile() {
            return commandline.getOptionValue("f");
        }

        String getOutputDirectory() {
            return commandline.getOptionValue("o");
        }
    }

    public static void main(String[] args) throws ParseException, IllegalArgumentException,
                    MalformedURLException, UnsupportedEncodingException, FileNotFoundException,
                    ClientProtocolException, IOException, FeedException {
        // Command line parsing.
        CommandLineParser command = new CommandLineParser();
        command.parse(args);

        // Create output folder for today.
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        File outputDir = new File(command.getOutputDirectory() + File.separatorChar + today);

        for (String feedURL : readFeedsFile(command.getFeedsFile())) {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(new URL(feedURL)));
            for (Object entry : feed.getEntries()) {
                if (entry instanceof SyndEntry && !command.isDryRun()) {
                    try {
                        SyndEntry syndEntry = (SyndEntry) entry;
                        URL link = new URL(syndEntry.getLink());
                        URL siteURL = new URL(splitQuery(link).get("url"));
                        System.out.println(siteURL);

                        File outputDirBySource = new File(outputDir.getAbsolutePath()
                                        + File.separatorChar + getDomain(siteURL));
                        outputDirBySource.mkdirs();

                        File htmlFile = new File(outputDirBySource.getAbsolutePath()
                                        + File.separatorChar + getURLMD5(siteURL) + ".html");
                        if (!htmlFile.exists()) {
                            PrintWriter htmlFileWriter = new PrintWriter(htmlFile);
                            htmlFileWriter.write(fetchHTML(siteURL));
                            htmlFileWriter.close();

                            PrintWriter urlFileWriter = new PrintWriter(
                                            outputDirBySource.getAbsolutePath()
                                                            + File.separatorChar
                                                            + getURLMD5(siteURL) + ".url");
                            urlFileWriter.write(siteURL.toString() + "\n");
                            urlFileWriter.close();
                        }
                    } catch (Exception e) {
                        System.err.println("Ignoring exception " + e.getClass().getName() + ": "
                                        + e.getMessage());
                        e.printStackTrace(System.err);
                    }
                }
            }
        }
    }

    private static List<String> readFeedsFile(String feedsFile) throws IOException {
        List<String> feeds = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(feedsFile));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                feeds.add(line);
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

    private static String fetchHTML(URL url) throws ClientProtocolException, IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet get = new HttpGet(url.toString());
        HttpResponse response = httpClient.execute(get);
        String html = EntityUtils.toString(response.getEntity(), "UTF-8");
        return html;
    }

    private static String getURLMD5(URL url) {
        return DigestUtils.md5Hex(url.toString());
    }

    private static String getDomain(URL url) {
        return url.getHost();
    }
}
