package org.aap.monitoring;

import akka.dispatch.Await;
import akka.dispatch.Future;
import akka.dispatch.Futures;
import akka.japi.Function2;
import akka.util.FiniteDuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseData;
import org.apache.nutch.protocol.Content;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;


public class WordCloudGenerator {
    private static Set<String> stopwordSet;
    static {
        String stopWordString = "a,able,about,above,abst,accordance,according,accordingly,across,act,actually,added,adj,affected,affecting,affects,after,afterwards,again,against,ah,all,almost,alone,along,already,also,although,always,am,amp,among,amongst,an,and,announce,another,any,anybody,anyhow,anymore,anyone,anything,anyway,anyways,anywhere,apparently,approximately,are,aren,arent,arise,around,as,aside,ask,asking,at,auth,available,away,awfully,b,back,be,became,because,become,becomes,becoming,been,before,beforehand,begin,beginning,beginnings,begins,behind,being,believe,below,beside,besides,between,beyond,bit,biol,both,brief,briefly,but,by,c,ca,came,can,cannot,can't,cause,causes,certain,certainly,co,com,come,comes,contain,containing,contains,could,couldnt,d,date,did,didn't,different,do,does,doesn't,doing,done,don't,down,downwards,due,during,e,each,ed,edu,effect,eg,eight,eighty,either,else,elsewhere,end,ending,enough,especially,et,et-al,etc,even,ever,every,everybody,everyone,everything,everywhere,ex,except,f,far,few,ff,fifth,first,five,fix,followed,following,follows,for,former,formerly,forth,found,four,from,further,furthermore,g,gave,get,gets,getting,give,given,gives,giving,go,goes,gone,got,gotten,h,had,happens,hardly,has,hasn't,have,haven't,having,he,hed,hence,her,here,hereafter,hereby,herein,heres,hereupon,hers,herself,hes,hi,hid,him,himself,his,hither,home,how,howbeit,however,http,hundred,i,id,ie,if,i'll,i'm,im,imma,immediate,immediately,importance,important,in,inc,indeed,index,information,instead,into,invention,inward,is,isn't,it,itd,it'll,it's,its,itself,i've,j,just,k,keep,keeps,kept,kg,km,know,known,knows,l,largely,last,lately,later,latter,latterly,least,less,lest,let,lets,like,liked,likely,line,little,ll,'ll,lol,look,looking,looks,lt,ltd,ly,m,made,mainly,make,makes,many,may,maybe,me,mean,means,meantime,meanwhile,merely,mg,might,million,miss,ml,more,moreover,most,mostly,mr,mrs,much,mug,must,my,myself,n,na,name,namely,nay,nd,near,nearly,necessarily,necessary,need,needs,neither,never,nevertheless,new,next,nine,ninety,no,nobody,non,none,nonetheless,noone,nor,normally,nos,not,noted,nothing,now,nowhere,o,obtain,obtained,obviously,of,off,often,oh,ok,okay,old,omitted,on,once,one,ones,only,onto,or,ord,other,others,otherwise,ought,our,ours,ourselves,out,outside,over,overall,owing,own,p,page,pages,part,particular,particularly,past,per,perhaps,placed,please,plus,poorly,possible,possibly,potentially,pp,predominantly,present,previously,primarily,probably,promptly,proud,provides,put,q,que,quickly,quite,qv,r,ran,rather,rd,re,readily,really,recent,recently,ref,refs,regarding,regardless,regards,related,relatively,research,retweet,respectively,resulted,resulting,results,right,rt,run,s,said,same,saw,say,saying,says,sec,section,see,seeing,seem,seemed,seeming,seems,seen,self,selves,sent,seven,several,shall,she,shed,she'll,shes,should,shouldn't,show,showed,shown,showns,shows,significant,significantly,similar,similarly,since,six,slightly,so,some,somebody,somehow,someone,somethan,something,sometime,sometimes,somewhat,somewhere,soon,sorry,specifically,specified,specify,specifying,still,stop,strongly,sub,substantially,successfully,such,sufficiently,suggest,sup,sure,t,take,taken,taking,tell,tends,th,than,thank,thanks,thanx,that,that'll,thats,that've,the,their,theirs,them,themselves,then,thence,there,thereafter,thereby,thered,therefore,therein,there'll,thereof,therere,theres,thereto,thereupon,there've,these,they,theyd,they'll,theyre,they've,think,this,those,thou,though,thoughh,thousand,throug,through,throughout,thru,thus,til,tip,to,together,too,took,toward,towards,tried,tries,truly,try,trying,ts,twice,two,u,un,under,unfortunately,unless,unlike,unlikely,until,unto,up,upon,ups,ur,us,use,used,useful,usefully,usefulness,uses,using,usually,v,value,various,'ve,very,via,viz,vol,vols,vs,w,want,wants,was,wasn't,way,we,wed,welcome,we'll,went,were,weren't,we've,what,whatever,what'll,whats,when,whence,whenever,where,whereafter,whereas,whereby,wherein,wheres,whereupon,wherever,whether,which,while,whim,whither,who,whod,whoever,whole,who'll,whom,whomever,whos,whose,why,widely,will,willing,wish,with,within,without,won't,words,world,would,wouldn't,www,x,y,yes,yet,you,youd,you'll,your,youre,you're,yours,yourself,yourselves,you've,z,zero";
        String elements[] = stopWordString.split(",");
        stopwordSet = new HashSet<String>(Arrays.asList(elements));
    }

    private static final Pattern pattern = Pattern.compile("\\s*((http(s)?://[^\\s]*)|(RT\\s*@\\w+:)|(\\b\\w{1,3}\\b)|(@\\w+)|(\\s+rt\\s+)|[\\ck/\\\\\"'{}()\\[\\],;.!?&:]|(#\\w+))\\s*");
    private static final Pattern unwantedPattern = Pattern.compile("\\s*((http(s)?://[^\\s]*)|(RT\\s*@\\w+:)|(@\\w+)|(\\s+rt\\s+)|(&gt)|(&lt)|[\\ck/\\\\\"{}()\\[\\],;.!?&:\\-#\n\r])\\s*");
    private static List<Map<String, String>> getTweetSamplesForDateRange(String serverUrl,
            String query, String startDay, String endDay, Integer count) {
        String cacheKey = serverUrl + "_" + query+ "_" + startDay + "_" + endDay + "_" + count;
        Object cachedResult = Cache.get(cacheKey);
        if (cachedResult != null) {
            List<Map<String, String>> cachedEntry =   (List<Map<String, String>>) cachedResult;
            List<Map<String, String>> output = new ArrayList<Map<String, String>>();
            for (Map<String, String> map : cachedEntry) {
                Map<String, String> doc = new HashMap<String, String>();
                for (String key : map.keySet()) {
                    doc.put(key, map.get(key));
                }
                output.add(doc);
            }
            return output;
        }
        SolrQuery solrQuery = new SolrQuery();
        String w1 = "0.1";
        String termFrequency = "tf(tweet_text,'" + query + "')";
        String weightedTF = "product(" + termFrequency + "," + w1 + ")";

        String w2 = "0.1";
        String logFollowers = "log(add(num_followers,1))";
        String weightedLogFollowers = "product(" + logFollowers + "," + w2 + ")";

        String w3 = "0.1";
        String retweetCount = "num_retweets";
        String weightedRetweetCount = "product(" + retweetCount + "," + w3 + ")";

        String sortFunction = "add(" + weightedRetweetCount + ", " +
                "add(" + weightedLogFollowers + "," + weightedTF + "))";
        solrQuery.set("sort", sortFunction + " desc");

        solrQuery.setQuery("tweet_text:" + query);
        solrQuery.addFilterQuery("date:[" + startDay + " TO " + endDay + "]");
        solrQuery.setRows(count);
        List<Map<String, String>> results = new ArrayList<Map<String, String>>();
        try {
            HttpSolrServer solrServer = new HttpSolrServer(serverUrl);
            QueryResponse response = solrServer.query(solrQuery);
            SolrDocumentList responseObject = (SolrDocumentList)response.getResponse().get("response");
            for (SolrDocument doc : responseObject) {
                Map<String, String> document = new HashMap<String, String>();
                String originalText = doc.get("original_text").toString();
                originalText = originalText.substring(1, originalText.length() - 1);
                document.put("original_text", originalText);
                document.put("date", doc.get("date").toString());
                String screen_name = doc.get("screen_name").toString();
                document.put("screen_name", screen_name);
                document.put("tweet_id", doc.get("tweet_id").toString());
                results.add(document);
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        }

        // Cache for an hour
        Cache.set(cacheKey, results, 3600);
        return results;
    }

    public static List<Map<String, String>> getTweetSamplesForDateRange(final String query,
            final String startDay, final String endDay, final Integer count) {
        TextProfileSignatureChanged tps = new TextProfileSignatureChanged();
        List<Slot> sampleShards = Slot.getSampleTweetShardsInTimeRange(
                Integer.parseInt(startDay), Integer.parseInt(endDay));
        if (sampleShards.size() == 0) {
            return new ArrayList<Map<String,String>>();
        }
        LinkedList<Future<List<Map<String, String>>>> requests =
                new LinkedList<Future<List<Map<String, String>>>>();
        for (Slot shard : sampleShards) {
            Future<List<Map<String, String>>> futureRequest = getSamplesFromShard(
                    shard, query, startDay, endDay, count);
            requests.add(futureRequest);
        }

        Function2<List<Map<String, String>>, List<Map<String, String>>, List<Map<String, String>>> reducer =
                new Function2<List<Map<String,String>>, List<Map<String,String>>, List<Map<String,String>>>() {
            @Override
            public List<Map<String, String>> apply(
                    List<Map<String, String>> output,
                    List<Map<String, String>> current) {
                output.addAll(current);
                return output;
            }
        };
        Future<List<Map<String, String>>> resultFuture = Futures.reduce(requests, reducer, Akka.system().dispatcher());
        List<Map<String, String>> uniqueResults = new ArrayList<Map<String, String>>();

        try {
            List<Map<String, String>> allResults = Await.result(resultFuture, new FiniteDuration(90, TimeUnit.SECONDS));
            if (allResults.size() == 0) {
                StatsDUtil.increment("frontend_0SampleSearchResults");
            }
            Set<String> uniqueTexts = new HashSet<String>();
            Set<String> uniqueSignatures = new HashSet<String>();
            for (Map<String, String> doc : allResults) {
                String originalText = doc.get("original_text").toString();
                final String sanitizedText = pattern.matcher(originalText).replaceAll(" ").replaceAll("[^\\p{L}\\p{N}]", " ").replaceAll("\\s+", " ").toLowerCase().trim();
                if (sanitizedText != "" && !uniqueTexts.contains(sanitizedText)) {
                    uniqueTexts.add(sanitizedText);
                    byte[] sig = tps.calculate(new Content(), new Parse() {
                        @Override
                        public ParseData getData() {
                            return null;
                        }

                        @Override
                        public String getText() {
                            return sanitizedText;
                        }

                        @Override
                        public boolean isCanonical() {
                            return false;
                        }
                    });
                    String signatureString = null;
                    if (sig != null) {
                        signatureString = new String(sig);
                    }

                    if (signatureString != null && !uniqueSignatures.contains(signatureString)) {
                        uniqueSignatures.add(signatureString);
                        uniqueResults.add(doc);
                    }
                }
            }
            return uniqueResults;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Future<List<Map<String, String>>> getSamplesFromShard(
            final Slot shard, final String query, final String startDay,
            final String endDay, final Integer count) {
        return Futures.future(new Callable<List<Map<String, String>>>() {
            @Override
            public List<Map<String, String>> call() throws Exception {
                String serverUrl = "http://" + shard.hostname + ":" + shard.port + "/solr";
                List<Map<String, String>> resultsFromShard = null;
                long startTime = System.currentTimeMillis();
                resultsFromShard = getTweetSamplesForDateRange(serverUrl, query, startDay, endDay, count);
                long endTime = System.currentTimeMillis();
                return resultsFromShard;
            }
        }, Akka.system().dispatcher());
    }

    //Remove duplicate and sort
    private static Map sortByComparator(Map unsortMap, Map<String,List<Map<String, String>>> finalResult) {
        List list = new LinkedList(unsortMap.entrySet());
        //sort list based on comparator
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        //put sorted list into map again
        Map sortedMap = new LinkedHashMap();
        Set usedWord = new HashSet<String>();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry)it.next();
            String[] keywords = ((String)entry.getKey()).split(" ");
            boolean removeWord = true;
            for (String word:keywords) {
                if (!usedWord.contains(word)) {
                    usedWord.add(word);
                    sortedMap.put(entry.getKey(), entry.getValue());
                    removeWord = false;
                    break;
                }
            }
            if (removeWord) {
                finalResult.remove(entry.getKey());
            }
        }
        return sortedMap;
    }

    private static void setBigrams(Map<String, List<String>> bigramDictionay,
            Map<String, Integer> frequencyMap,
            Map<String, List<Map<String, String>>> finalResult,
            Set queryWords) {
        Set<String> wordsTobeRemoved = new HashSet<String>();
        for (String word : bigramDictionay.keySet()) {
            boolean bigramPresent = false;
            boolean parentWordToBeRemoved = true;
            for (String bigramWord : bigramDictionay.get(word)) {
                if (frequencyMap.containsKey(word) && frequencyMap.containsKey(bigramWord)) {
                    float bigramScore = (float)frequencyMap.get(word)/(float)frequencyMap.get(bigramWord);
                    if (bigramScore  > 0.80) {
                        if (!wordsTobeRemoved.contains(bigramWord)) {
                            wordsTobeRemoved.add(bigramWord);
                            parentWordToBeRemoved = false;
                        }
                    }
                }
            }
            if (parentWordToBeRemoved) {
                wordsTobeRemoved.add(word);
            }
        }
        for (String wordTobeRemoved : wordsTobeRemoved) {
            frequencyMap.remove(wordTobeRemoved);
            finalResult.remove(wordTobeRemoved);
        }
    }

    private static boolean isValidWCWord(String cloudWord, Set queryWords ) {
        String[] words = cloudWord.split(" ");
        for (String word : words) {
            if (!queryWords.contains(word) && !stopwordSet.contains(word)&& word.length()>1 && !word.matches("-?\\d+(\\.\\d+)?") && !word.matches("'"))
                return true;
        }
        return false;
    }

    private static boolean isSurrogate(String query) {
        for (int i = 0; i < query.length(); i++) {
            char c = query.charAt(i);
            if (Character.isHighSurrogate(c) || Character.isLowSurrogate(c))
                return true;
        }
        return false;
    }

    private static Map<String,Integer> formNgrams(String[] words,
            Map<String, List<String>> bigramDictionay,
            Set<String> queryWords, int n ) {
        Queue<String> prevWordQue = new LinkedList<String>();
        Map<String, Integer> resultanatWords = new HashMap<String, Integer>();
        for (String word : words) {
            if (word.isEmpty()||queryWords.contains(word)||isSurrogate(word)) {
                continue;
            }
            if (stopwordSet.contains(word)) {
                prevWordQue = new LinkedList<String>();
                continue;
            }
            if (!resultanatWords.containsKey(word)) {
                resultanatWords.put(word,1);
            } else {
                resultanatWords.put(word,resultanatWords.get(word) + 1);
            }
            String currWord = word;
            String currWordFirstpart = "";
            String currWordSecondpart = "";
            int wordSize = 1;
            prevWordQue.add(word);
            for (int i = 0; i < prevWordQue.size() - 1; i++) {
                String prevWord = prevWordQue.poll();
                if (wordSize == 1) {
                    currWordFirstpart = prevWord;
                } else {
                    currWordFirstpart = prevWord + " " + currWordFirstpart;
                }
                currWordSecondpart = currWord;
                currWord = prevWord + " " + currWord;
                List<String> childWords = Arrays.asList(currWordFirstpart, currWordSecondpart);
                bigramDictionay.put(currWord, childWords);
                wordSize++;
                if (i != n) {
                    prevWordQue.add(prevWord);
                }
                if (!resultanatWords.containsKey(currWord)) {
                    resultanatWords.put(currWord,1);
                } else {
                    resultanatWords.put(currWord,resultanatWords.get(currWord) + 1);
                }
            }
        }
        return resultanatWords;
    }

    public static Map<String, List<Map<String, String>>> getWordCloudTweet(
            final String query, final Set queryWords, final String startDay, final String endDay,
            final Integer count) {
        List<Map<String, String>> rawSample = getTweetSamplesForDateRange(query, startDay, endDay, count);
        return getWordCloudTweet(rawSample, queryWords, 30);
    }

    public static Map<String, List<Map<String, String>>> getWordCloudTweet(
            final List<Map<String, String>> rawSample, final Set queryWords, final int numWords) {
        Map<String, List<Map<String, String>>> finalResult = new HashMap<String, List<Map<String, String>>>();;
        Map<String, List<String>> bigramDictionay = new HashMap<String, List<String>>();
        Map<String, Integer> frequencyMap =new HashMap<String, Integer>();
        for (Map<String, String> tweetmap : rawSample) {
            String rawText = tweetmap.get("original_text");
            rawText = StringEscapeUtils.unescapeHtml(rawText);
            String textforWords = unwantedPattern.matcher(rawText).replaceAll(" ").trim();
            rawText = rawText.replaceAll("[\\r\\n\\s+]", " ");
            tweetmap.put("original_text",rawText);
            textforWords=textforWords.toLowerCase();
            String[] wordsArray = textforWords.split(" ");
            Map<String, Integer> ngramWords = formNgrams(wordsArray, bigramDictionay, queryWords, 3);
            for (String word : ngramWords.keySet()) {
                if (!isValidWCWord (word, queryWords)) {
                    continue;
                }
                if (frequencyMap.containsKey(word)) {
                    frequencyMap.put(word, frequencyMap.get(word) +1);
                    finalResult.get(word).add(tweetmap);
                } else {
                    frequencyMap.put(word, 1);
                    List<Map<String, String>> listTweetmap = new ArrayList<Map<String, String>>();
                    listTweetmap.add(tweetmap);
                    finalResult.put(word, listTweetmap);
                }
            }
        }
        List<String> selectedWords = null;
        setBigrams(bigramDictionay, frequencyMap, finalResult, queryWords);
        Map<String, Integer> sortedFrequencyMap = sortByComparator(frequencyMap, finalResult);
        int currCount = 0;
        for (String word : sortedFrequencyMap.keySet()) {
            if (currCount > numWords) {
                finalResult.remove(word);
            }
            currCount++;
        }
        return finalResult;
    }
}
