package org.aap.monitoring;

import java.util.Date;

import org.apache.solr.common.SolrDocument;

public class Article {
    String src;
    String url;
    String title;
    Date date;
    String image_url;
    String content;
    String author;
    String category;
    String comments;
    String country;
    String city;
    int commentcount;

    private Article() {}

    public static Article getArticleFrom(SolrDocument doc) {
        Article article = new Article();
        article.setSrc(doc.get("src").toString());
        article.setUrl(doc.get("url").toString());
        article.setTitle(doc.get("title").toString());
        article.setDate((Date) doc.get("date"));
        article.setImage_url(doc.get("image_url").toString());
        if(doc.get("content") != null){
        	article.setContent(doc.get("content").toString());
        }
        article.setAuthor(doc.get("author").toString());
        article.setCategory(doc.get("category").toString());
        // article.setComments(doc.get("comments").toString());
        if(doc.get("country") != null){
        	article.setCountry(doc.get("country").toString());
        }
        article.setCity(doc.get("city").toString());
        article.setCommentcount((Integer)doc.get("commentcount"));
        return article;
    }

    public String getSrc() {
        return src;
    }
    public void setSrc(String src) {
        this.src = src;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public String getImage_url() {
        return image_url;
    }
    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public int getCommentcount() {
        return commentcount;
    }
    public void setCommentcount(int commentcount) {
        this.commentcount = commentcount;
    }

}
