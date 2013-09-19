package org.aap.monitoring;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws SQLException {
        //SQLManager sqlManager = new SQLManager(new SolrManager());
        //sqlManager.triggerIndexer("\"2013-08-03\"");
        SolrManager solrManager = new SolrManager();
//        Date todayDate = new Date();
//        long seconds = todayDate.getTime();
//        solrManager.insertFraudyDocument("url1", "Aam admi party is contesting in elections. Arvind is here? This! Apple,Banana.", new Date(seconds - 86400000));
//        solrManager.insertFraudyDocument("url2", "BJP party is winning in this election. Namo is here? comma's",  new Date(seconds - (2 * 86400000)));
//        solrManager.insertFraudyDocument("url3", "Congress party is losing in this election. Rahul is here? comma's",  new Date(seconds - (3 * 86400000)));
        WordCloud wordCloud = new WordCloud(solrManager);
        String content = "This is the beginning. All Is Well. Thi is Aam Admi Party Winner and Arvind Kejriwal.";
        test(wordCloud, content);
        content = "Answer Me If This Is Correct Is the beginning. All Is Well. Thi is Aam Admi Party Winner and Arvind Kejriwal.";
        test(wordCloud, content);
        content = "This is the beginning. why?  All is Well. Thi is Aam Admi AWESOME Party Winner and Arvind Kejriwal.";
        test(wordCloud, content);
        content = "Thi is Aam A 232 dmi Party Winner and Arvind Kejriwal.";
        test(wordCloud, content);
        content = "This Is the Beginning. All Is Well. Thi is Winner and Arvind Kejriwal.";
        test(wordCloud, content);
        content = "This Is The beginning. All Is Well. Thi is Aam Admi Party Winner and Arvind .AB";
        test(wordCloud, content);
        content = "This Is The Beginning. All? Is! Well. Thi is Aam ; Admi ( Party Winner A B.";
        test(wordCloud, content);
        content = "This . Is?  .The Beginning. All? Is! Well. Thi is Aam ; Admi ( Party Winner A23B.";
        test(wordCloud, content);
        content = "";
        test(wordCloud, content);
//        Map<String,Integer> keywordsCount = wordCloud.getWordCloud("party", new Date(seconds - (2 * 86400000)), new Date());
//        for (String s : keywordsCount.keySet()) {
//            System.out.println(s + " : " + keywordsCount.get(s));
//        }
    }

    public static void test(WordCloud wordCloud, String content) {
        System.out.println(content);
        List<String> phrases = new ArrayList<String>();
        wordCloud.getPhrases2(content, phrases);
        for (String phrase : phrases) {
            System.out.println(phrase);
        }
    }
}
