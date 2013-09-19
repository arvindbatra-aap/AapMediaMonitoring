create table IF NOT EXISTS ARTICLE_TBL(
	url VARCHAR(200) PRIMARY KEY NOT NULL,
	title VARCHAR(512) NOT NULL,
	publishedDate date  NOT NULL,
	imageUrl TEXT NOT NULL,
	src VARCHAR(50) NOT NULL, 
	content LONGTEXT NOT NULL, 
	author VARCHAR(512) NOT NULL, 
	category VARCHAR(512) NOT NULL,
	country VARCHAR(512) NOT NULL,
	city VARCHAR(512) NOT NULL,
	commentCount INT,
	comments LONGTEXT NOT NULL);
	
create index pDateIndex on ARTICLE_TBL(publishedDate);
	
insert into  ARTICLE_TBL values (
'/myUrl', "md6", 'My title','13-08-03','/myImage','MYNew SRC','My content','my author', 'my category', 'my country', 'my city', 1 ,'my comments', null);
 
 
GRANT ALL PRIVILEGES ON AAP.* TO 'root'@'%' IDENTIFIED BY 'aapmysql00t';

/etc/mysql/my.cnf
Comment the bind address for making it accessible from all ip-address


wget http://mirror.cogentco.com/pub/apache/lucene/solr/4.4.0/solr-4.4.0-src.tgz