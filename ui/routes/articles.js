var request = require('request');

var API_HOST = "http://66.175.223.5:9090/restServices/media";
var START_DATE = "2013-09-19";

function constructFiltersFromReq(req) {
    var filter = "";

    if(req.param("start")) {  
        filter += "&startDate=" + (new Date(parseInt(req.param("start")))).toISOString().substr(0,10);
    }
    else {
         filter += "&startDate=" + START_DATE;
    }

    if(req.param("end")) {
        filter += "&endDate=" + (new Date(parseInt(req.param("end")))).toISOString().substr(0,10);
    }
    else {
        filter += "&endDate=" + (new Date()).toISOString().substr(0,10)
    }

    if(req.param("src") && req.param("src") != 'Total') {
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

exports.getWordCloud = function(req, res) {
    var uri = API_HOST + '/getWordCloud?query=' + req.param("query") + constructFiltersFromReq(req);;
    console.log("Querying word cloud with URI:" + uri);

    request(uri, function(error, response, body) {
        if (!error && response.statusCode == 200) {
            console.log(body);
            res.send(JSON.parse(body));
        }
    });
};