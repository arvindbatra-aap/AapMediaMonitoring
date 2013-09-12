var _AAP = function() {
	console.log("Initializing AAP Media Monitoring Service...");
	this._ui = new _AAP_UI(this);
};

_AAP.prototype.init = function(config) {
	this.showArticleCountTrend(config.query, config.start, config.end);
};

_AAP.prototype.showArticleCountTrend = function(query, start, end) {
	console.log("Loading Article Counts for query:" + query + " and start:" + start + " and end:" + end);

	this._ui.showLoading();
	
	var params = {
		query : query,
		start : start,
		end   : end
	};
	var that = this;

	$.get('/articles/count', params, function(data, status, xhr) {
		if(data) {
			var chart_data = {
				dates: [],
				series: []
			};

			for(var date in data.countByDate) {
				chart_data.dates.push(date);
			}

			for(var source in data.countBySrc) {
				var blob = {
					name: source,
					data: []
				};
				for(var i=0; i<chart_data.dates.length; i++) {

					if(data.countBySrc[source][chart_data.dates[i]]) {
						blob.data.push(data.countBySrc[source][chart_data.dates[i]]);
					}
					else {
						blob.data.push(0);	
					}
				}
				chart_data.series.push(blob);
			}

			for(var i=0; i<chart_data.dates.length; i++) {
				chart_data.dates[i] = (new Date(chart_data.dates[i])).toDateString();
			}
			that._ui.hideLoading();
			that._ui.renderArticleCountChart(chart_data);
		}
	});
};

