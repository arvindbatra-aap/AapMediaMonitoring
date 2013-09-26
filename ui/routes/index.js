
/*
 * GET home page.
 */

exports.home = function(req, res){
	var csv = require('csv');
	csv().from.path(__dirname + '/../../../AapMediaMonitoring/candidatelist/CandidateList.csv', {})
		.to.array( function(data){
			var query = req.param("q") || 'Aam Aadmi Party';
			res.render('index', { user_query: query, candidates: data, current_page: 'trend'});
		});
};

/*
 * GET compare page.
 */

exports.compare = function(req, res){
	var csv = require('csv');
	csv().from.path(__dirname + '/../../../AapMediaMonitoring/candidatelist/CandidateList.csv', {})
		.to.array( function(data){
			var query = req.param("q") || 'Aam Aadmi Party';
			res.render('compare', { user_query: query, candidates: data, current_page: 'compare'});
		});
};