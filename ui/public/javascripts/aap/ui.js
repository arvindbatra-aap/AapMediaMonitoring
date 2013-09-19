var _AAP_UI = function (context) {
	console.log("Initializing UI engine...");
	this._context = context;
	this._ARTICLE_COUNT_CHART_DIV = '#article-count-trend';
	this._TREND_LOADING_DIV = '#trend-loading';
    this._ARTICLES_LOADING_DIV = '#articles-loading';
    this._ARTICLES_CONTAINER_DIV = '#articles-container';
    this._ARTICLE_BOX_TEMPLATE = Handlebars.compile($("#article-box-template").html());
};

_AAP_UI.prototype.renderArticleCountChart = function(chart_data) {
	console.log("Rendering Article Count Chart in div:" + this._ARTICLE_COUNT_CHART_DIV + " with data:");
	console.log(chart_data);
	$(this._ARTICLE_COUNT_CHART_DIV).empty();
	$(this._ARTICLE_COUNT_CHART_DIV).highcharts({
		chart: {
            type: 'line',
            zoomType: 'x'
        },
        title: {
            text: 'Media Trend'
        },
        xAxis: {
            categories: chart_data.dates
        },
        yAxis: {
        	allowDecimals: false,
            title: {
                text: 'Article Count'
            }
        },
        series: chart_data.series
	});
};

_AAP_UI.prototype.renderArticles = function(articles) {
    console.log("Rendering Articles in div:" + this._ARTICLES_CONTAINER_DIV + " with data:");
    console.log(articles);

    for(var i=0; i<articles.length; i++) {
        articles[i].date = (new Date(articles[i].date)).toDateString();
        $(this._ARTICLES_CONTAINER_DIV).append(this._ARTICLE_BOX_TEMPLATE(articles[i]));
    }

    $(this._ARTICLES_CONTAINER_DIV).append("<div class='clearfix'></div>");
}

_AAP_UI.prototype.hideTrendLoading = function() {
	$(this._TREND_LOADING_DIV).hide();
};

_AAP_UI.prototype.showTrendLoading = function() {
	$(this._ARTICLE_COUNT_CHART_DIV).empty();
	$(this._TREND_LOADING_DIV).show();
};

_AAP_UI.prototype.hideArticlesLoading = function() {
    $(this._ARTICLES_LOADING_DIV).hide();
};
_AAP_UI.prototype.showArticlesLoading = function() {
    $(this._ARTICLES_CONTAINER_DIV).empty();
    $(this._ARTICLES_LOADING_DIV).show();
};