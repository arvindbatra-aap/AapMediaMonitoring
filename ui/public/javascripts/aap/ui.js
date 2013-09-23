var _AAP_UI = function (context) {
	console.log("Initializing UI engine...");
	this._context = context;
	this._ARTICLE_COUNT_CHART_DIV = '#article-count-trend';
	this._TREND_LOADING_DIV = '#trend-loading';
    this._ARTICLES_LOADING_DIV = '#articles-loading';
    this._ARTICLES_CONTAINER_DIV = '#articles-container';
    this._WORDCLOUD_CONTAINER_DIV = '#wordcloud';
    this._WORDCLOUD_LOADING_DIV = '#wordcloud-loading';
    this._ARTICLE_COUNT_CHART_CONTROL_DIV = '#article-count-trend-control';
    this._ARTICLE_BOX_TEMPLATE = Handlebars.compile($("#article-box-template").html());
    this._ARTICLES_MODAL_DIV = '#articles-modal';
    this._ARTICLES_MODAL_LOADING_DIV = '#articles-modal-loading';

    this._chart = null;
};

_AAP_UI.prototype.renderArticleCountChart = function(chart_data) {
	console.log("Rendering Article Count Chart in div:" + this._ARTICLE_COUNT_CHART_DIV + " with data:");
	console.log(chart_data);
    var that = this;
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
            type: 'datetime'
        },
        yAxis: {
        	allowDecimals: false,
            title: {
                text: 'Article Count'
            },
            min: 0
        },
        series: chart_data.series,
        ignoreHiddenSeries: false,
        plotOptions: {
            series: {
                cursor: 'pointer',
                point: {
                    events: {
                        click: function() {
                            console.log("Clicked on series: " + this.series.name + " on date:" + (new Date(this.x)).toDateString());
                            that._context.showArticlesForSrcDate(this.series.name, this.x);
                        }
                    }
                }
            }
        }
	});
    this._chart = $(this._ARTICLE_COUNT_CHART_DIV).highcharts();
    console.log(this._chart);
};

_AAP_UI.prototype.showAllTrendSeries = function() {
    for (var i=0; i<this._chart.series.length; i++)
    {
        if(!this._chart.series[i].visible) {
            this._chart.series[i].setVisible(true, false);
        }
    }
};

_AAP_UI.prototype.hideAllTrendSeries = function() {
    console.log(this._chart);
    for (var i=0; i<this._chart.series.length; i++)
    {
        if(this._chart.series[i].visible && this._chart.series[i].name != 'Total') {
            this._chart.series[i].setVisible(false, false);
        }
    }
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
    $(this._WORDCLOUD_CONTAINER_DIV).show();
    $(this._WORDCLOUD_CONTAINER_DIV).jQCloud(jqCloudInput);
};

_AAP_UI.prototype.renderArticlesModal = function(articles) {
    console.log("Rendering Articles in modal div:" + this._ARTICLES_MODAL_DIV + " with data:");
    console.log(articles);
    var modalbody = $(this._ARTICLES_MODAL_DIV).find('.modal-body');
    for(var i = 0; i < articles.length; i++) {
        articles[i].date = (new Date(articles[i].date)).toDateString();
        modalbody.append(this._ARTICLE_BOX_TEMPLATE(articles[i]));
    }

    modalbody.append("<div class='clearfix'></div>");
};

_AAP_UI.prototype.showArticlesModalWithLoading = function(src, date) {
    var title;
    if(src == 'Total') {
        title = "All Articles Published On " + (new Date(parseInt(date))).toDateString() 
    }
    else {
        title = "Articles From " + src + " Published On " + (new Date(parseInt(date))).toDateString()
    }
    $(this._ARTICLES_MODAL_DIV).find('.modal-title').text(title);
    $(this._ARTICLES_MODAL_LOADING_DIV).show();
    $(this._ARTICLES_MODAL_DIV).modal();
};

_AAP_UI.prototype.hideArticlesModalLoading = function() {
    $(this._ARTICLES_MODAL_LOADING_DIV).hide();
};

_AAP_UI.prototype.showWordCloudLoading = function() {
    $(this._WORDCLOUD_CONTAINER_DIV).empty();
    $(this._WORDCLOUD_LOADING_DIV).show();
};

_AAP_UI.prototype.hideWordCloudLoading = function() {
    $(this._WORDCLOUD_LOADING_DIV).hide();
};

_AAP_UI.prototype.hideTrendLoading = function() {
	$(this._TREND_LOADING_DIV).hide();
    $(this._ARTICLE_COUNT_CHART_CONTROL_DIV).show();
};

_AAP_UI.prototype.showTrendLoading = function() {
	$(this._ARTICLE_COUNT_CHART_DIV).empty();
    $(this._ARTICLE_COUNT_CHART_CONTROL_DIV).hide();
	$(this._TREND_LOADING_DIV).show();
};

_AAP_UI.prototype.hideArticlesLoading = function() {
    $(this._ARTICLES_LOADING_DIV).hide();
};
_AAP_UI.prototype.showArticlesLoading = function() {
    $(this._ARTICLES_CONTAINER_DIV).empty();
    $(this._ARTICLES_LOADING_DIV).show();
};