var committee = ['Arvind Kejriwal',
				'Manish Sisodia',
				'Gopal Rai',
				'Prashant Bhushan',
				'Sanjay Singh',
				'Pankaj Gupta',
				'Kumar Vishwas',
				'Naveen Jaihind', 
				'Dinesh Waghela',
				'Yogendra Yadav',
				'Ajit Jha',
				'Christina Samy',
				'Anand Kumar',
				'Shazia Ilmi',
				'Habung Pyang', 
				'Yogesh Dhahiya',
				'Ashok Aggarwal',
				'Illias Azmi',
				'Subash Ware',
				'Krishnakant Sevada',
				'Mayank Gandhi',
				'Rakesh Sinha',
				'Prem Singh Pahari'];

/*
 * GET home page.
 */

exports.home = function(req, res){
	var csv = require('csv');
	csv().from.path(__dirname + '/../../../AapMediaMonitoring/candidatelist/CandidateList.csv', {})
		.to.array( function(data){
			var query = req.param("q") || 'Aam Aadmi Party';
			res.render('index', { user_query: query, candidates: data, current_page: 'trend', core_committee: committee});
		});
};

/*
 * GET compare page.
 */

exports.compare = function(req, res){
	var csv = require('csv');
	csv().from.path(__dirname + '/../../../AapMediaMonitoring/candidatelist/CandidateList.csv', {})
		.to.array( function(data){
			var queries = req.param("q") || ["Arvind Kejriwal","Sheila Dikshit"];
			console.log(queries);
			res.render('compare', { user_query: queries, candidates: data, current_page: 'compare', core_committee: committee});
		});
};