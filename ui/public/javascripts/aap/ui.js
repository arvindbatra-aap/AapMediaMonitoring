var _AAP_UI = function (context) {
	console.log("Initializing UI engine...");

    this._chart = null;
	this._context = context;

    this._compare_queries = [];
    this._compare_query_fields = 2;

    this._ARTICLE_BOX_TEMPLATE = Handlebars.compile($("#article-box-template").html());
    this._COMPARE_QUERY_FIELD_TEMPLATE = Handlebars.compile($("#compare-query-field-template").html());
    

    this._COMPARE_PANEL_DIV = '#compare-panel';
    this._COMPARE_QUERY_FIELD = '.compare-query-field';

    this._ARTICLE_COUNT_CHART_DIV = '#article-count-trend';
	this._ARTICLE_COUNT_LOADING_DIV = '#trend-loading';
    this._ARTICLE_COUNT_CHART_CONTROL_DIV = '#article-count-trend-control';

    this._TREND_BREAKDOWN_DIV = "#trend-breakdown";
    this._TREND_BREAKDOWN_LOADING_DIV = "#trend-breakdown-loading";

    this._ARTICLES_LOADING_DIV = '#articles-loading';
    this._ARTICLES_CONTAINER_DIV = '#articles-container';

    this._WORDCLOUD_CONTAINER_DIV = '#wordcloud';
    this._WORDCLOUD_LOADING_DIV = '#wordcloud-loading';

    this._ARTICLES_MODAL_DIV = '#articles-modal';
    this._ARTICLES_MODAL_LOADING_DIV = '#articles-modal-loading';
};

_AAP_UI.prototype.renderNewCompareQueryField = function() {
    var html = this._COMPARE_QUERY_FIELD_TEMPLATE();
    $(this._COMPARE_PANEL_DIV).find('span.vstext').last().after(html);
    this._compare_query_fields++;
};

_AAP_UI.prototype.adjustCompareQueryFields = function() {
    var that = this;
    this._compare_queries = [];

    // Remove empty fields and load queries of non-empty fields
    $(this._COMPARE_PANEL_DIV).find(this._COMPARE_QUERY_FIELD).each(function(){
        var val = $(this).find('input').val();
        if(val == "" && that._compare_query_fields > 1) {
            $(this).next('.vstext').remove();
            $(this).remove();
            that._compare_query_fields--;
        }
        else if(val != "") {
            that._compare_queries.push(val);
        }
    });
};

_AAP_UI.prototype.getCompareQueries = function() {
    return this._compare_queries;
}

_AAP_UI.prototype.renderGetLinkPopover = function(link) {
    
    if($('#get-link-btn').data('state') == 'open') {
        $('#get-link-btn').popover('destroy');
        $('#get-link-btn').data('state', 'closed');
    }
    else {
        $('#get-link-btn').popover({
            html: true,
            title: 'Copy the link below',
            trigger: 'manual',
            placement: 'auto top',
            content: function() {
                return "<input type='text' value='"+link+"' style='width:250px' readonly id='get-link-field'/>";
            }
        });
        $('#get-link-btn').popover('show');
        $('#get-link-field').select();
        $('#get-link-btn').data('state', 'open');

        setTimeout(function() {
            $('#get-link-btn').popover('destroy');
            $('#get-link-btn').data('state', 'closed');
        }, 5000);
    }
};

_AAP_UI.prototype.renderTrendBreakdownChart = function(chart_data) {
    console.log("Rendering Trend Breakdown Chart in div:" + this._TREND_BREAKDOWN_DIV + " with data:");
    console.log(chart_data);
    
    this._context.setTrendBreakdownDate(chart_data.date);

    var that = this;
    $(this._TREND_BREAKDOWN_DIV).empty().show();
    $(this._TREND_BREAKDOWN_DIV).highcharts({
        chart: {
            type: 'bar'
        },
        legend: {
            enabled: false
        },
        title: {
            text: 'Breakdown by Source'
        },
        subtitle: {
            text: chart_data.date
        },
        yAxis: {
            allowDecimals: false,
            title: {
                text: 'Article Count'
            },
           
            min: 0
        },
        xAxis: {
            type: 'category',
            labels: {
                overflow: 'justify'
            }
        },
        series: [{
            data: chart_data.series,
            dataLabels: {
                enabled: true
            }
        }],
        plotOptions: {
            bar: {
                dataLabels: {
                    enabled: true
                }
            }
        },
        tooltip: {
            headerFormat: '<b>{point.key}</b>',
            pointFormat: ' : {point.y}',
      
        },
        exporting: {
            enabled: true
        },
        plotOptions: {
            series: {
                cursor: 'pointer',
                point: {
                    events: {
                        click: function() {
                            var date = that._context.getTrendBreakdownDate();
                            console.log("Clicked within Trend Breakdown Chart on Source: " + this.name + " for date:" + date);
                            date = (new Date(date)).getTime();
                            that._context.showArticles(date, date, this.name);
                            that._context.showWordCloud(date, date, this.name);
                        }
                    }
                }
            }
        }
    });
    setTimeout(removeHC, 500);
}

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
        legend: {
            reversed: true,
            maxHeight: 100,
            itemWidth: 200,
            itemDistance: 10,
            labelFormatter: function() {

                return this.name.length > 25 ? this.name.substr(0,25) + '...' : this.name;
            }
        },
        exporting: {
            enabled: true
        },
        series: chart_data.series,
        ignoreHiddenSeries: false,
        plotOptions: {
            series: {
                cursor: 'pointer',
                point: {
                    events: {
                        click: function() {
                            console.log("Clicked within Trend Chart on series: " + this.series.name + " on date:" + (new Date(this.x)).toDateString());
                            that._context.updateContentForSrcDate(this.series.name, this.x);
                        }
                    }
                }
            }
        }
	});
    this._chart = $(this._ARTICLE_COUNT_CHART_DIV).highcharts();
    setTimeout(removeHC, 500);
};

_AAP_UI.prototype.showAllTrendSeries = function() {
    for (var i=0; i<this._chart.series.length; i++)
    {
        if(!this._chart.series[i].visible) {
            this._chart.series[i].setVisible(true, false);
        }
    }
    this._chart.redraw();
};

_AAP_UI.prototype.hideAllTrendSeries = function() {
    console.log(this._chart);
    for (var i=0; i<this._chart.series.length; i++)
    {
        if(this._chart.series[i].visible && this._chart.series[i].name != 'Total') {
            this._chart.series[i].setVisible(false, false);
        }
    }
    this._chart.redraw();
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
   
    if (max > 20) {
        for (var tag in histogram) {
            jqCloudInput.push({text: tag, weight: (20.0 * (histogram[tag] - min))/((max - min) * 1.0), link:"/?q=" + tag});
        }
    } else {
        for (var tag in histogram) {
            jqCloudInput.push({text: tag, weight: histogram[tag], link:"/?q=" + tag});
        }
    }
   
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
    $(this._ARTICLES_MODAL_DIV).find('.article-box, .clearfix').remove();
    $(this._ARTICLES_MODAL_LOADING_DIV).show();
    $(this._ARTICLES_MODAL_DIV).modal();
};

_AAP_UI.prototype.hideArticlesModalLoading = function() {
    $(this._ARTICLES_MODAL_LOADING_DIV).hide();
};

/* Word Cloud */
_AAP_UI.prototype.showWordCloudLoading = function() {
    $(this._WORDCLOUD_CONTAINER_DIV).empty().hide();
    $(this._WORDCLOUD_LOADING_DIV).show();
};

_AAP_UI.prototype.hideWordCloudLoading = function() {
    $(this._WORDCLOUD_LOADING_DIV).hide();
};

_AAP_UI.prototype.showNoResponseErrorWordCloud = function() {
    $(this._WORDCLOUD_CONTAINER_DIV).text("Sorry! Not enough data to generate a word cloud.").show();
};

/* Overall Trend Chart */
_AAP_UI.prototype.hideTrendLoading = function() {
	$(this._ARTICLE_COUNT_LOADING_DIV).hide();
    $(this._ARTICLE_COUNT_CHART_CONTROL_DIV).show();
};

_AAP_UI.prototype.showTrendLoading = function() {
	$(this._ARTICLE_COUNT_CHART_DIV).empty();
    $(this._ARTICLE_COUNT_CHART_CONTROL_DIV).hide();
	$(this._ARTICLE_COUNT_LOADING_DIV).show();
};

_AAP_UI.prototype.showNoResponseErrorTrendChart = function() {
    $(this._ARTICLE_COUNT_CHART_CONTROL_DIV).hide();
    $(this._ARTICLE_COUNT_CHART_DIV).text("Sorry! Not enough data to generate a trend chart!").show();
};

/* Trend Breakdown Chart */
_AAP_UI.prototype.hideTrendBreakdownLoading = function() {
    $(this._TREND_BREAKDOWN_LOADING_DIV).hide();};

_AAP_UI.prototype.showTrendBreakdownLoading = function() {
    $(this._TREND_BREAKDOWN_DIV).empty().hide();
    $(this._TREND_BREAKDOWN_LOADING_DIV).show();
};

_AAP_UI.prototype.showNoResponseErrorTrendBreakdown = function() {
    $(this._TREND_BREAKDOWN_DIV).text("Sorry! Not enough data to generate a breakdown chart!").show();
};

/* Articles */
_AAP_UI.prototype.hideArticlesLoading = function() {
    $(this._ARTICLES_LOADING_DIV).hide();
};

_AAP_UI.prototype.showArticlesLoading = function() {
    $(this._ARTICLES_CONTAINER_DIV).find('.article-box, .clearfix').remove();
    $(this._ARTICLES_LOADING_DIV).show();
};

_AAP_UI.prototype.showNoResponseErrorArticles = function() {
    $(this._ARTICLES_LOADING_DIV).text("Sorry! There are no articles for the given query!").show();
};