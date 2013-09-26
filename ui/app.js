
/**
 * Module dependencies.
 */

var express = require('express')
  , engine = require('ejs-locals')
  , index = require('./routes/index')
  , articles = require('./routes/articles')
  , http = require('http')
  , path = require('path');

var app = express();

// use ejs-locals for all ejs templates:
app.engine('ejs', engine);

// all environments
app.set('port', process.env.PORT || 3000);
app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.bodyParser());
app.use(express.methodOverride());
app.use(app.router);
app.use(express.static(path.join(__dirname, 'public')));

// development only
if ('development' == app.get('env')) {
  app.use(express.errorHandler());
}

app.get('/', index.home);
app.get('/compare', index.compare);
app.get('/articles/count', articles.getArticlesCount);
app.get('/articles/content', articles.getArticlesContent);
app.get('/articles/multicount', articles.getMultiQueryCounts);
app.get('/wordcloud', articles.getWordCloud);

http.createServer(app).listen(app.get('port'), function(){
  console.log('Express server listening on port ' + app.get('port'));
});
