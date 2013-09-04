package org.aap.media.crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import org.aap.media.utils.AppConstants;
import org.aap.media.utils.URLUtils;
import org.apache.log4j.Logger;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;

public class HTMLWriter {
  protected static final Logger logger = Logger.getLogger(HTMLWriter.class.getName());
  private static File crawlDir;
  public HTMLWriter (String dir) {
    crawlDir = new File(dir);
    logger.info("Setting crawl dir=" + crawlDir.getAbsolutePath());
  }

  public void write(String url, Page page) {
    String date = AppConstants.DATE_FORMAT.format(Calendar.getInstance().getTime());
    String dateCarwlDirString = crawlDir.getAbsolutePath() + "/" + date ;
    File dateCrawlDir = new File(dateCarwlDirString);
    if (!dateCrawlDir.exists()) {
      synchronized(this) {
        logger.info("Creating dir " + dateCarwlDirString);
        dateCrawlDir.mkdirs();                                                 
      }                                                                        
    }                                                                          
    String md5url = URLUtils.getURLMD5(url);                                   
    try {                                                                      
      String filename = dateCarwlDirString + "/" + md5url + ".html";           
      logger.info("Writing to file: " + filename + " " + url);                 
      File crawlFile = new File(filename);                                     
      if (!crawlFile.exists()) {                                               
          crawlFile.createNewFile();                                           
      }                                                                        
      FileWriter writer = new FileWriter(crawlFile);                           
      BufferedWriter bw = new BufferedWriter(writer);                          
      bw.write(((HtmlParseData) page.getParseData()).getHtml());               
      bw.close();                                                              
    } catch (IOException ioe) {                                                
      logger.error("Error in writing html file for url " + url + " " + ioe.getMessage());
      ioe.printStackTrace();                                                   
    } 
    
  }
}