package org.aap.monitoring;

import java.sql.SQLException;
import java.util.Map;


/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws SQLException {
        //SQLManager sqlManager = new SQLManager(new SolrManager());
        //sqlManager.triggerIndexer("\"2013-08-03\"");
        SolrManager solrManager = new SolrManager();
        solrManager.insertFraudyDocument("url1", "Aam admi party is contesting in elections. Arvind is here? This! Apple,Banana.");
        solrManager.insertFraudyDocument("url2", "BJP party is winning in this election. Namo is here? comma's");
        WordCloud wordCloud = new WordCloud(solrManager);
        Map<String,Integer> keywordsCount = wordCloud.getWordCloud("party");

        for (String s : keywordsCount.keySet()) {
            System.out.println(s + " : " + keywordsCount.get(s));
        }
    }
}
