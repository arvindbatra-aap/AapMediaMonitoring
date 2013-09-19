
/*
 * GET home page.
 */

exports.home = function(req, res){
	var csv = require('csv');
	csv().from.path(__dirname + '/../../../AapMediaMonitoring/candidatelist/CandidateList.csv', {})
		.to.array( function(data){
			res.render('index', { title: 'Express' , candidates: data});
		});
};