var request = require('request');

var API_HOST = "http://66.175.223.5:9090/restServices/media";
var START_DATE = "2013/09/01";

function constructFiltersFromReq(req) {
    var filter = "";

    if(req.param("start")) {  
        filter += "&startDate=" + (new Date(parseInt(req.param("start")))).getTime();
    }
    else {
         filter += "&startDate=" + (new Date(START_DATE)).getTime();
    }

    if(req.param("end")) {
        filter += "&endDate=" + (new Date(parseInt(req.param("end")))).getTime();
    }

    if(req.param("src")) {
        filter += "&src=" + req.param("src");
    }  

    return filter;
}


exports.getArticlesCount = function(req, res) {
    var uri = API_HOST + '/getArticlesCount?query=' + req.param("query") + constructFiltersFromReq(req);
    console.log("Querying article counts API with URI:" + uri);

    request(uri, function(error, response, body) {
        if (!error && response.statusCode == 200) {
            console.log(body);

            res.send(JSON.parse(body));
        }
    });
};

exports.getArticlesContent = function(req, res) {
    var uri = API_HOST + '/getArticles?start=0&count=9&query=' + req.param("query") + constructFiltersFromReq(req);
    console.log("Querying article content API with URI:" + uri);

    request(uri, function(error, response, body) {
        if (!error && response.statusCode == 200) {
            console.log(body);

            res.send(JSON.parse(body));
        }
    });

};