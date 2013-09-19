var request = require('request');

var API_HOST = "http://66.175.223.5:9090/restServices/media";

exports.getWordCloud = function(req, res) {
    var uri = API_HOST + '/getWordCloud?query='+req.param("query");
    console.log("Querying word cloud with URI:" + uri);

    request(uri, function(error, response, body) {
        if (!error && response.statusCode == 200) {
            console.log(body);
            res.send(JSON.parse(body));
        }
    });
};