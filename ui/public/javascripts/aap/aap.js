var AAP_INI_CONFIG = {
	'query' : user_query 
};

var $AAP;

$(document).ready(function(){
	$AAP = new _AAP();
	$AAP.init(AAP_INI_CONFIG);

	$('#trend-update-btn').click(function(event){

		var query = $('#trend-query-string').val();
		if(query && query.length > 0){
			$AAP.showArticleCountTrend(query);
			$AAP.showWordCloud(query);
		}
	});

	$('#about-btn').click(function(){
		$('#about-modal').modal();
	});
});




