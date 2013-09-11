package org.aap.monitoring;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;


/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws SQLException {
        //SQLManager sqlManager = new SQLManager(new SolrManager());
        //sqlManager.triggerIndexer("\"2013-08-03\"");
        SolrManager solrManager = new SolrManager();
        Date todayDate = new Date();
        long seconds = todayDate.getTime();
        solrManager.insertFraudyDocument("url1", "Aam admi party is contesting in elections. Arvind is here? This! Apple,Banana.", new Date(seconds - 86400000));
        solrManager.insertFraudyDocument("url2", "BJP party is winning in this election. Namo is here? comma's",  new Date(seconds - (2 * 86400000)));
        solrManager.insertFraudyDocument("url3", "Congress party is losing in this election. Rahul is here? comma's",  new Date(seconds - (3 * 86400000)));
        WordCloud wordCloud = new WordCloud(solrManager);
        Map<String,Integer> keywordsCount = wordCloud.getWordCloud("party", new Date(seconds - (2 * 86400000)), new Date());
        for (String s : keywordsCount.keySet()) {
            System.out.println(s + " : " + keywordsCount.get(s));
        }
    }
}
