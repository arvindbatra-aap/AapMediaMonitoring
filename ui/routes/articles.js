var request = require('request');

var API_HOST = "http://66.175.223.5:9090/restServices/media";

exports.getArticlesCount = function(req, res) {
    var uri = API_HOST + '/getArticlesCount?query='+req.param("query");
    console.log("Querying article counts API with URI:" + uri);

    request(uri, function(error, response, body) {
        if (!error && response.statusCode == 200) {
            console.log(body);

            res.send(JSON.parse(body));
        }
    });
};

exports.getArticlesContent = function(req, res) {
    var uri = API_HOST + '/getArticles?start=0&count=9&query='+req.param("query");
    console.log("Querying article content API with URI:" + uri);

    request(uri, function(error, response, body) {
        if (!error && response.statusCode == 200) {
            console.log(body);

            res.send(JSON.parse(body));
        }
    });

}