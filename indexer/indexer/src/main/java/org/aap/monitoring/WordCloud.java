package org.aap.monitoring;

import java.util.*;

import org.apache.log4j.Logger;

public class WordCloud {

    static Set<String> stopwordSet;
    static String reg = "[? .,!&()-:'\"]";
    SolrManager solrManager;
    private static Logger LOG = Logger.getLogger(WordCloud.class);

    public WordCloud(SolrManager solrManager) {
        this.solrManager = solrManager;
    }

    static {
    	
        String stopWordString = "rs,january,february,march,april,may,june,july,august,september,october,november,december,wednesday,thursday,friday,saturday,sunday,monday,tuesday,ms,mrs,mr,a,able,about,above,abst,accordance,according,accordingly,across,act,actually,added,adj,affected,affecting,affects,after,afterwards,again,against,ah,all,almost,alone,along,already,also,although,always,am,amp,among,amongst,an,and,announce,another,any,anybody,anyhow,anymore,anyone,anything,anyway,anyways,anywhere,apparently,approximately,are,aren,arent,arise,around,as,aside,ask,asking,at,auth,available,away,awfully,b,back,be,became,because,become,becomes,becoming,been,before,beforehand,begin,beginning,beginnings,begins,behind,being,believe,below,beside,besides,between,beyond,bit,biol,both,brief,briefly,but,by,c,ca,came,can,cannot,can't,cause,causes,certain,certainly,co,com,come,comes,contain,containing,contains,could,couldnt,d,date,did,didn't,different,do,does,doesn't,doing,done,don't,down,downwards,due,during,e,each,ed,edu,effect,eg,eight,eighty,either,else,elsewhere,end,ending,enough,especially,et,et-al,etc,even,ever,every,everybody,everyone,everything,everywhere,ex,except,f,far,few,ff,fifth,first,five,fix,followed,following,follows,for,former,formerly,forth,found,four,from,further,furthermore,g,gave,get,gets,getting,give,given,gives,giving,go,goes,gone,got,gotten,h,had,happens,hardly,has,hasn't,have,haven't,having,he,hed,hence,her,here,hereafter,hereby,herein,heres,hereupon,hers,herself,hes,hi,hid,him,himself,his,hither,home,how,howbeit,however,http,hundred,i,id,ie,if,i'll,i'm,im,imma,immediate,immediately,importance,important,in,inc,indeed,index,information,instead,into,invention,inward,is,isn't,it,itd,it'll,it's,its,itself,i've,j,just,k,keep,keeps,kept,kg,km,know,known,knows,l,largely,last,lately,later,latter,latterly,least,less,lest,let,lets,like,liked,likely,line,little,ll,'ll,lol,look,looking,looks,lt,ltd,ly,m,made,mainly,make,makes,many,may,maybe,me,mean,means,meantime,meanwhile,merely,mg,might,million,miss,ml,more,moreover,most,mostly,mr,mrs,much,mug,must,my,myself,n,na,name,namely,nay,nd,near,nearly,necessarily,necessary,need,needs,neither,never,nevertheless,new,next,nine,ninety,no,nobody,non,none,nonetheless,noone,nor,normally,nos,not,noted,nothing,now,nowhere,o,obtain,obtained,obviously,of,off,often,oh,ok,okay,old,omitted,on,once,one,ones,only,onto,or,ord,other,others,otherwise,ought,our,ours,ourselves,out,outside,over,overall,owing,own,p,page,pages,part,particular,particularly,past,per,perhaps,placed,please,plus,poorly,possible,possibly,potentially,pp,predominantly,present,previously,primarily,probably,promptly,proud,provides,put,q,que,quickly,quite,qv,r,ran,rather,rd,re,readily,really,recent,recently,ref,refs,regarding,regardless,regards,related,relatively,research,retweet,respectively,resulted,resulting,results,right,rt,run,s,said,same,saw,say,saying,says,sec,section,see,seeing,seem,seemed,seeming,seems,seen,self,selves,sent,seven,several,shall,she,shed,she'll,shes,should,shouldn't,show,showed,shown,showns,shows,significant,significantly,similar,similarly,since,six,slightly,so,some,somebody,somehow,someone,somethan,something,sometime,sometimes,somewhat,somewhere,soon,sorry,specifically,specified,specify,specifying,still,stop,strongly,sub,substantially,successfully,such,sufficiently,suggest,sup,sure,t,take,taken,taking,tell,tends,th,than,thank,thanks,thanx,that,that'll,thats,that've,the,their,theirs,them,themselves,then,thence,there,thereafter,thereby,thered,therefore,therein,there'll,thereof,therere,theres,thereto,thereupon,there've,these,they,theyd,they'll,theyre,they've,think,this,those,thou,though,thoughh,thousand,throug,through,throughout,thru,thus,til,tip,to,together,too,took,toward,towards,tried,tries,truly,try,trying,ts,twice,two,u,un,under,unfortunately,unless,unlike,unlikely,until,unto,up,upon,ups,ur,us,use,used,useful,usefully,usefulness,uses,using,usually,v,value,various,'ve,very,via,viz,vol,vols,vs,w,want,wants,was,wasn't,way,we,wed,welcome,we'll,went,were,weren't,we've,what,whatever,what'll,whats,when,whence,whenever,where,whereafter,whereas,whereby,wherein,wheres,whereupon,wherever,whether,which,while,whim,whither,who,whod,whoever,whole,who'll,whom,whomever,whos,whose,why,widely,will,willing,wish,with,within,without,won't,words,world,would,wouldn't,www,x,y,yes,yet,you,youd,you'll,your,youre,you're,yours,yourself,yourselves,you've,z,zero";
        String elements[] = stopWordString.split(",");
        stopwordSet = new HashSet<String>(Arrays.asList(elements));
        assert(stopwordSet.size()>0);
    }


    /**
     *
     * @param query - input query string.
     * @return map - <keyword, count> for all keywords that occur along with the query.
     * Counts one keyword only once in a document
     */
    public Map<String, Integer> getWordCloud(String query, String startDate, String endDate, String src, int count) {
        Map<String, Integer> keywordCounts = new HashMap<String, Integer>();
        try {
            List<Article> articlesForKeyword = solrManager.getArticlesForKeywords(Arrays.asList(query), startDate, endDate, src, 0, count);
            for (Article article : articlesForKeyword) {
            	 Map<String, Integer> newKeywordCounts = processArticle(article);
                 mergeKeywordCounts(newKeywordCounts, keywordCounts);                 
            }
            return keywordCounts;
        } catch (Exception e) {
            LOG.error("Failed to get word cloud",e);
        }
        return keywordCounts;
    }
    
    private void mergeKeywordCounts(Map<String, Integer> newKeywordCounts, Map<String, Integer> keywordCounts) {
    	for (String key : newKeywordCounts.keySet()) {
    		if (keywordCounts.containsKey(key)) {
    			keywordCounts.put(key, keywordCounts.get(key) + 1);
    		} else {
    			keywordCounts.put(key, 1);
    		}
    	}
    }


    /**
     * Parse the content in the article, get keywords and increment count. (Only once
     * per article.)
     * @param article
     */
    public Map<String, Integer> processArticle(Article article) {
    	Map<String, Integer> keywordCounts = new HashMap<String, Integer>();
        String content = article.getContent();
        String[] split = content.split(reg);
        Set<String> keywordsSeen = new HashSet<String>();
        for (String s : split) {
            insertString(keywordCounts, keywordsSeen, s);
        }
        List<String> phrases = new ArrayList<String>();
        getPhrases2(content, phrases);
        for (String phrase : phrases) {        	
            insertString(keywordCounts, keywordsSeen, phrase);
            // Remove individual keywords that are part of the phrases.
            removeKeywords(phrase, keywordCounts);
        }
        return keywordCounts;
    }
    
    private void removeKeywords(String phrase, Map<String, Integer> keywordCounts) {
    	String [] keywords = phrase.split("[ ]");
    	for (int i = 0; i < keywords.length; i++) {
    		keywordCounts.remove(keywords[i]);
    	}
    }
    
    private void insertString(Map<String, Integer> keywordCounts, Set<String> keywordsSeen, String s) {
    	// clean the string before s is inserted.
    	s = removeSpecialCharacters(s.trim());
    	
        if (ignoreKeyword(s) || keywordsSeen.contains(s)) {
            // keyword already seen. Ignore.
        } else {
            // new keyword increment the count and add to the set.
            if (keywordCounts.containsKey(s)) {
                keywordCounts.put(s, keywordCounts.get(s) + 1);
            } else {
                keywordCounts.put(s, 1);
            }
            keywordsSeen.add(s);
        }
    }
    
    private String removeSpecialCharacters(String s) {
    	return s.replaceAll("[^A-Z^a-z~ ]", " ");
    }

    /**
     *
     * @param keyword
     * @return true to ignore keyword
     */
    public boolean ignoreKeyword(String keyword) {
        if (keyword.isEmpty()) {
            return true;
        } else if (keyword.trim().isEmpty()) {
            return true;
        } else {
            if (stopwordSet.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public void getPhrases(String content, List<String> currentPhrase, List<String> phrases) {
        if (content.isEmpty()) {
            if (currentPhrase.size() > 1) {
                String newPhrase = getString(currentPhrase);
                phrases.add(newPhrase);
            }
            return;
        }

        String word = getWord(content);
        if (isCharWord(word)) {
            currentPhrase.add(word);
            if (content.length() > word.length())
                getPhrases(content.substring(word.length()+1), currentPhrase, phrases);
        } else {
            if (currentPhrase.size() > 1) {
                String newPhrase = getString(currentPhrase);
                phrases.add(newPhrase);
            }
            if (content.length() > word.length())
                getPhrases(content.substring(word.length()+1), new ArrayList<String>(), phrases);
        }
    }

    public void getPhrases2(String content, List<String> phrases) {
        if (content.isEmpty()) {
            return;
        }
        List<String> tempPhrase = new ArrayList();
        while (true) {
            String word = getWord(content);

            if (isCharWord(word)) {
                tempPhrase.add(word);
            } else if (word.equalsIgnoreCase(" ")) {
                // Ignore spaces.
            } else {
                if (tempPhrase.size() > 1) {
                    String newPhrase = getString(tempPhrase);
                    phrases.add(newPhrase);
                }
                tempPhrase.clear();
            }
            if (content.isEmpty()) {
                break;
            }
            content = content.substring(word.length());
        }
        if (tempPhrase.size() > 1) {
            String newPhrase = getString(tempPhrase);
            phrases.add(newPhrase);
        }
    }

    public String getString(List<String> words) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String word : words) {
            stringBuilder.append(word);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString().trim();
    }

    public String getWord(String content) {
        if (content.isEmpty())
            return content;
        for (int i = 0; i < content.length(); i++) {
            if (!Character.isLetter(content.charAt(i))) {
                // non letter char seen
            	if(i==0) break;
                return content.substring(0, i);
            }
        }
        return content.substring(0, 1);
    }

    // true for valid keyword
    public boolean isCharWord(String word) {
        if (word.isEmpty())
            return false;
        if (!Character.isLetter(word.charAt(0)) || !Character.isUpperCase(word.charAt(0)))
            return false;
        for(int i = 0; i < word.length(); i++) {
            if (!Character.isLetter(word.charAt(i)))
                return false;
        }
        return true;
    }
}
