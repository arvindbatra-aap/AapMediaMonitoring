var _AAP_UI = function (context) {
	console.log("Initializing UI engine...");
	this._context = context;
	this._ARTICLE_COUNT_CHART_DIV = '#article-count-trend';
	this._TREND_LOADING_DIV = '#trend-loading';
    this._ARTICLES_LOADING_DIV = '#articles-loading';
    this._ARTICLES_CONTAINER_DIV = '#articles-container';
    this._WORDCLOUD_CONTAINER_DIV = '#wordcloud';
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

    for(var i = 0; i < articles.length; i++) {
        articles[i].date = (new Date(articles[i].date)).toDateString();
        $(this._ARTICLES_CONTAINER_DIV).append(this._ARTICLE_BOX_TEMPLATE(articles[i]));
    }

    $(this._ARTICLES_CONTAINER_DIV).append("<div class='clearfix'></div>");
};

_AAP_UI.prototype.renderWordCloud = function(histogram) {
    console.log("Rendering Wordcloud in div:" + this._WORDCLOUD_CONTAINER_DIV + " with data:");
    console.log(histogram);
    var jqCloudInput = [];
    var max = -1;
    var min = 10000000;
    for (var tag in histogram) {
        max = Math.max(max, histogram[tag]);
        min = Math.min(min, histogram[tag]);
    }
    console.log(max);
    if (max > 20) {
        for (var tag in histogram) {
            jqCloudInput.push({text: tag, weight: (20.0 * (histogram[tag] - min))/((max - min) * 1.0), link:"/?q=" + tag});
        }
    } else {
        for (var tag in histogram) {
            jqCloudInput.push({text: tag, weight: histogram[tag], link:"/?q=" + tag});
        }
    }
    console.log(jqCloudInput);
    $(this._WORDCLOUD_CONTAINER_DIV).jQCloud(jqCloudInput);
};

_AAP_UI.prototype.emptyWordCloud = function(histogram) {
    $(this._WORDCLOUD_CONTAINER_DIV).empty();
};

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