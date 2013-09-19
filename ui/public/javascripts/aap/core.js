var _AAP = function() {
	console.log("Initializing AAP Media Monitoring Service...");
	this._ui = new _AAP_UI(this);
};

_AAP.prototype.init = function(config) {
	this.showArticleCountTrend(config.query, config.start, config.end);
};

_AAP.prototype.showArticleCountTrend = function(query, start, end) {
	console.log("Loading Article Counts for query:" + query + " and start:" + start + " and end:" + end);

	this._ui.showTrendLoading();
	this._ui.showArticlesLoading();
	
	var params = {
		query : query,
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


			var total_data = new Array(chart_data.dates.length);

			for(var source in data.countBySrc) {
				var blob = {
					name: source,
					data: []
				};
				for(var i=0; i<chart_data.dates.length; i++) {

					if(data.countBySrc[source][chart_data.dates[i]]) {
						blob.data.push(data.countBySrc[source][chart_data.dates[i]]);
						total_data[i] = total_data[i] + data.countBySrc[source][chart_data.dates[i]] || data.countBySrc[source][chart_data.dates[i]];
					}
					else {
						blob.data.push(0);	
						total_data[i] = total_data[i] + 0 || 0;
					}
				}
				chart_data.series.push(blob);
			}

			// All timeline
			chart_data.series.push({name: 'Total', data: total_data});

			for(var i=0; i<chart_data.dates.length; i++) {
				chart_data.dates[i] = (new Date(parseInt(chart_data.dates[i]))).toDateString();
			}
			that._ui.hideTrendLoading();
			that._ui.renderArticleCountChart(chart_data);
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

