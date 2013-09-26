var AAP_INI_CONFIG = {
	'query' : user_query,
	'domain': 'http://aap.mediatrack.in'
};

var $AAP;

$(document).ready(function(){
	$AAP = new _AAP();
	$AAP.init(AAP_INI_CONFIG);

	$('#trend-update-btn').click(function(){
		var query = $('#trend-query-string').val();
		if(query && query.length > 0){
			$AAP.setQuery(query);
			$AAP.showArticleCountTrend();
			$AAP.showArticles();
			$AAP.showWordCloud();
		}
	});

	$('#trend-query-string').keyup(function(event){
		if(event.which == 13) {
			$('#trend-update-btn').click();
		}
	});

	$('#get-link-btn').click(function(){
		$AAP.showGetLinkPopover();
	});

	$('#about-btn').click(function(){
		$('#about-modal').modal();
	});

	$('#show-all-series-btn').click(function(){
		$AAP.showAllTrendSeries();
	});

	$('#hide-all-series-btn').click(function(){
		$AAP.hideAllTrendSeries();
	});
});




