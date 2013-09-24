var _AAP = function() {
	console.log("Initializing AAP Media Monitoring Service...");
	this._ui = new _AAP_UI(this);
	this._VISIBLE_SOURCES = ["timesofindia.indiatimes.com", "www.hindustantimes.com", "www.indianexpress.com", "www.thehindu.com", "zeenews.india.com"];
	this._query = "";
};

_AAP.prototype.init = function(config) {
	this._query = config.query;
	this.showArticleCountTrend(config.start, config.end);
	this.showWordCloud(config.start, config.end);
};

_AAP.prototype.setQuery = function(query) {
	this._query = query;
}

_AAP.prototype.showAllTrendSeries = function() {
	this._ui.showAllTrendSeries();
};

_AAP.prototype.hideAllTrendSeries = function() {
	this._ui.hideAllTrendSeries();
};

_AAP.prototype.showArticlesForSrcDate = function(src, date) {
	console.log("Loading articles for query:" + this._query + " and source:" +  src + " and date:" + date);
	var params = {
		query : this._query,
		start : date,
		end   : date,
		src   : src != "Total" ? src : null
	};
	console.log(params);
	
	this._ui.showArticlesModalWithLoading(src, date);

	var that = this;

	// Update article list
	$.get('/articles/content', params, function(data, status, xhr) {
		if(data) {
			that._ui.hideArticlesModalLoading();
			that._ui.renderArticlesModal(data);
		}
	});
};

_AAP.prototype.showArticleCountTrend = function(start, end) {
	console.log("Loading Articles for query:" + this._query + " and start:" + start + " and end:" + end);

	this._ui.showTrendLoading();
	this._ui.showTrendBreakdownLoading();
	this._ui.showArticlesLoading();
	
	var params = {
		query : this._query,
		start : start,
		end   : end
	};
	var that = this;

	// Update trend graph
	$.get('/articles/count', params, function(data, status, xhr) {
		if(data) {
			var chart_data = {
				dates: [],
				series: []
			};

			for(var date in data.countByDate) {
				chart_data.dates.push(date);
			}

			var epochLatest = chart_data.dates[chart_data.dates.length-1];
			var breakdown_chart_data = {
				date: (new Date(parseInt(epochLatest))).toISOString().substr(0,10),
				series: []
			};		
			
			var count = 0;
			var colors = Highcharts.getOptions().colors;

			var total_data = new Array(chart_data.dates.length);

			for(var source in data.countBySrc) {
				var blob = {
					name: source,
					data: [],
					visible: (that._VISIBLE_SOURCES.indexOf(source) != -1)
				};
				for(var i=0; i<chart_data.dates.length; i++) {

					if(data.countBySrc[source][chart_data.dates[i]]) {
						blob.data.push([parseInt(chart_data.dates[i]), data.countBySrc[source][chart_data.dates[i]]]);
						total_data[i] = total_data[i] + data.countBySrc[source][chart_data.dates[i]] || data.countBySrc[source][chart_data.dates[i]];
					}
					else {
						blob.data.push([parseInt(chart_data.dates[i]), 0]);	
						total_data[i] = total_data[i] + 0 || 0;
					}

					if(chart_data.dates[i] == epochLatest && data.countBySrc[source][chart_data.dates[i]] > 0) {
						breakdown_chart_data.series.push({
							name: source,
							y: data.countBySrc[source][chart_data.dates[i]],
							color: colors[count++]
						})
					}
 
				}
				chart_data.series.push(blob);
			}

			var total_data_chart = [];
			for(var i=0; i<chart_data.dates.length; i++) {
				total_data_chart.push([parseInt(chart_data.dates[i]), total_data[i]]);
			}

			// All timeline
			chart_data.series.push({name: 'Total', data: total_data_chart});

			that._ui.hideTrendLoading();
			that._ui.renderArticleCountChart(chart_data);


			// Update Trend Breakdown
			/*var colors = Highcharts.getOptions().colors
			breakdown_chart_data = {
				date: '2013-09-23',
				series: [{name:'X',y:10,color:colors[0]},{name:'D',y:40,color:colors[1]},{name:'S',y:50,color:colors[2]}]
			};*/

			that._ui.hideTrendBreakdownLoading();
			that._ui.renderTrendBreakdownChart(breakdown_chart_data);


		}
	});

	// Update article list
	$.get('/articles/content', params, function(data, status, xhr) {
		if(data) {
			that._ui.renderArticles(data);
			that._ui.hideArticlesLoading();
		}
	});

};

_AAP.prototype.showWordCloud = function(start, end) {
	console.log("Loading Word Cloud for query:" + this._query + " and start:" + start + " and end:" + end);
	this._ui.showWordCloudLoading();
	// Add loading code here
	
	var params = {
		query : this._query,
		start : start,
		end   : end
	};
	var self = this;

	// Update article list
	$.get('/wordcloud', params, function(data, status, xhr) {
		if(data) {
			delete data[this._query];
			self._ui.renderWordCloud(data);
			self._ui.hideWordCloudLoading();
		}
	});

};

