package org.aap.monitoring;

import java.util.*;

public class WordCloud {

    static Set<String> stopwordSet;
    static String reg = "[? .,!&()-:'\"]";
    SolrManager solrManager;

    public WordCloud(SolrManager solrManager) {
        this.solrManager = solrManager;
    }

    static {
        String stopWordString = "a,able,about,above,abst,accordance,according,accordingly,across,act,actually,added,adj,affected,affecting,affects,after,afterwards,again,against,ah,all,almost,alone,along,already,also,although,always,am,amp,among,amongst,an,and,announce,another,any,anybody,anyhow,anymore,anyone,anything,anyway,anyways,anywhere,apparently,approximately,are,aren,arent,arise,around,as,aside,ask,asking,at,auth,available,away,awfully,b,back,be,became,because,become,becomes,becoming,been,before,beforehand,begin,beginning,beginnings,begins,behind,being,believe,below,beside,besides,between,beyond,bit,biol,both,brief,briefly,but,by,c,ca,came,can,cannot,can't,cause,causes,certain,certainly,co,com,come,comes,contain,containing,contains,could,couldnt,d,date,did,didn't,different,do,does,doesn't,doing,done,don't,down,downwards,due,during,e,each,ed,edu,effect,eg,eight,eighty,either,else,elsewhere,end,ending,enough,especially,et,et-al,etc,even,ever,every,everybody,everyone,everything,everywhere,ex,except,f,far,few,ff,fifth,first,five,fix,followed,following,follows,for,former,formerly,forth,found,four,from,further,furthermore,g,gave,get,gets,getting,give,given,gives,giving,go,goes,gone,got,gotten,h,had,happens,hardly,has,hasn't,have,haven't,having,he,hed,hence,her,here,hereafter,hereby,herein,heres,hereupon,hers,herself,hes,hi,hid,him,himself,his,hither,home,how,howbeit,however,http,hundred,i,id,ie,if,i'll,i'm,im,imma,immediate,immediately,importance,important,in,inc,indeed,index,information,instead,into,invention,inward,is,isn't,it,itd,it'll,it's,its,itself,i've,j,just,k,keep,keeps,kept,kg,km,know,known,knows,l,largely,last,lately,later,latter,latterly,least,less,lest,let,lets,like,liked,likely,line,little,ll,'ll,lol,look,looking,looks,lt,ltd,ly,m,made,mainly,make,makes,many,may,maybe,me,mean,means,meantime,meanwhile,merely,mg,might,million,miss,ml,more,moreover,most,mostly,mr,mrs,much,mug,must,my,myself,n,na,name,namely,nay,nd,near,nearly,necessarily,necessary,need,needs,neither,never,nevertheless,new,next,nine,ninety,no,nobody,non,none,nonetheless,noone,nor,normally,nos,not,noted,nothing,now,nowhere,o,obtain,obtained,obviously,of,off,often,oh,ok,okay,old,omitted,on,once,one,ones,only,onto,or,ord,other,others,otherwise,ought,our,ours,ourselves,out,outside,over,overall,owing,own,p,page,pages,part,particular,particularly,past,per,perhaps,placed,please,plus,poorly,possible,possibly,potentially,pp,predominantly,present,previously,primarily,probably,promptly,proud,provides,put,q,que,quickly,quite,qv,r,ran,rather,rd,re,readily,really,recent,recently,ref,refs,regarding,regardless,regards,related,relatively,research,retweet,respectively,resulted,resulting,results,right,rt,run,s,said,same,saw,say,saying,says,sec,section,see,seeing,seem,seemed,seeming,seems,seen,self,selves,sent,seven,several,shall,she,shed,she'll,shes,should,shouldn't,show,showed,shown,showns,shows,significant,significantly,similar,similarly,since,six,slightly,so,some,somebody,somehow,someone,somethan,something,sometime,sometimes,somewhat,somewhere,soon,sorry,specifically,specified,specify,specifying,still,stop,strongly,sub,substantially,successfully,such,sufficiently,suggest,sup,sure,t,take,taken,taking,tell,tends,th,than,thank,thanks,thanx,that,that'll,thats,that've,the,their,theirs,them,themselves,then,thence,there,thereafter,thereby,thered,therefore,therein,there'll,thereof,therere,theres,thereto,thereupon,there've,these,they,theyd,they'll,theyre,they've,think,this,those,thou,though,thoughh,thousand,throug,through,throughout,thru,thus,til,tip,to,together,too,took,toward,towards,tried,tries,truly,try,trying,ts,twice,two,u,un,under,unfortunately,unless,unlike,unlikely,until,unto,up,upon,ups,ur,us,use,used,useful,usefully,usefulness,uses,using,usually,v,value,various,'ve,very,via,viz,vol,vols,vs,w,want,wants,was,wasn't,way,we,wed,welcome,we'll,went,were,weren't,we've,what,whatever,what'll,whats,when,whence,whenever,where,whereafter,whereas,whereby,wherein,wheres,whereupon,wherever,whether,which,while,whim,whither,who,whod,whoever,whole,who'll,whom,whomever,whos,whose,why,widely,will,willing,wish,with,within,without,won't,words,world,would,wouldn't,www,x,y,yes,yet,you,youd,you'll,your,youre,you're,yours,yourself,yourselves,you've,z,zero";
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
    public Map<String, Integer> getWordCloud(String query) {
        Map<String, Integer> keywordCounts = new HashMap<String, Integer>();
        try {
            List<Article> articlesForKeyword = solrManager.getArticlesForKeywords(query);
            for (Article article : articlesForKeyword) {
                processArticle(article, keywordCounts);
            }
            return keywordCounts;
        } catch (Exception e) {
            return keywordCounts;
        }
    }

    /**
     *
     * @param query - input query string.
     * @return map - <keyword, count> for all keywords that occur along with the query.
     * Counts one keyword only once in a document
     */
    public Map<String, Integer> getWordCloud(String query, Date startDate, Date endDate) {
        Map<String, Integer> keywordCounts = new HashMap<String, Integer>();
        try {
            List<Article> articlesForKeyword = solrManager.getArticlesForKeywords(query, startDate, endDate);
            for (Article article : articlesForKeyword) {
                processArticle(article, keywordCounts);
            }
            return keywordCounts;
        } catch (Exception e) {
            return keywordCounts;
        }
    }

    /**
     * Parse the content in the article, get keywords and increment count. (Only once
     * per article.)
     * @param article
     * @param keywordCounts
     */
    public void processArticle(Article article, Map<String, Integer> keywordCounts) {
        String content = article.getContent();
        String[] split = content.split(reg);
        Set<String> keywordsSeen = new HashSet<String>();
        for (String s : split) {
            if (keywordsSeen.contains(s)) {
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
    }
}
