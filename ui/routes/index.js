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

var labs_team = [{
	n: 'Abhinav Anand (Hum)',
	e: 'ab.rvian@gmail.com'	
}, {
	n: 'Abhishek Shrivastava',
	f: 'abhishekdelta',
	t: 'abhishekdelta'	
}, {
	n: 'Ankit Gupta (Chunky)',
	f: 'ankitgupta'
}, {
	n: 'Arvind Batra',
}, {
	n: 'Gurmeet Singh',
	f: 'guru27gurmeet'
}, {
	n: 'Nikesh Garera'
}, {
	n: 'Parag Sarda',
	f: 'parag.sarda'	
}, {
	n: 'Pratik Patre',
	e: 'pbpatre@gmail.com',
	f: 'pbpatre'	
}, {
	n: 'Pranay Venkata',
	f: 'svpranay'
}, {
	n: 'Rohit Hiwale',
	f: 'rohit.hiwale'	
}];

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

/*
 * GET about page.
 */

exports.about = function(req, res){
	var csv = require('csv');
	csv().from.path(__dirname + '/../../../AapMediaMonitoring/candidatelist/CandidateList.csv', {})
		.to.array( function(data){;
			res.render('about', { user_query: "", candidates: data, current_page: 'about', core_committee: committee, labs_team: labs_team});
		});
};