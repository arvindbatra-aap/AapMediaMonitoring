var AAP_INI_CONFIG = {
	'query' : user_query,
	'domain': 'http://aap.mediatrack.in',
	'page'  : current_page
};

var $AAP;

$(document).ready(function(){
	$AAP = new _AAP();
	$AAP.init(AAP_INI_CONFIG);

	$('#trend-update-btn').click(function(){
		var query = $('#trend-query-string').val();
		if(query && query.length > 0){
			$AAP.abortAllActiveAjax();
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

	$('#compare-btn').click(function(){
		document.location.href="/compare";
	});

	$('#show-all-series-btn').click(function(){
		$AAP.showAllTrendSeries();
	});

	$('#hide-all-series-btn').click(function(){
		$AAP.hideAllTrendSeries();
	});


	/* Compare Page */
	$('#compare-add-btn').click(function(){
		$AAP.showNewCompareQueryField();
	});

	$('#compare-do-btn').click(function(){
		$AAP.doQueryComparison();
	});

	$('.compare-query-field > input').keyup(function(event){
		if(event.which == 13) {
			$('#compare-add-btn').click();
		}
	});

	/* Common */
	$('#get-link-btn').click(function(){
		$AAP.showGetLinkPopover();
	});

	$('#about-btn').click(function(){
		$('#about-modal').modal();
	});

});




