var _AAP_UI = function (context) {
	console.log("Initializing UI engine...");
	this._context = context;
	this._ARTICLE_COUNT_CHART_DIV = '#article-count-trend';
	this._LOADING_DIV = '#trend-loading';
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

_AAP_UI.prototype.hideLoading = function() {
	$(this._LOADING_DIV).hide();
};
_AAP_UI.prototype.showLoading = function() {
	$(this._ARTICLE_COUNT_CHART_DIV).empty();
	$(this._LOADING_DIV).show();
};