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

    res.send([{
        "src": "",
        "url": "http://www.thehindu.com/todays-paper/tp-national/tp-kerala/fssa-steps-up-sample-collection-ahead-of-onam/article5115172.ece",
        "title": "FSSA steps up sample collection ahead of Onam",
        "date": 1378751400000,
        "image_url": null,
        "content": null,
        "author": null,
        "category": null,
        "comments": null,
        "country": null,
        "city": null,
        "commentcount": 0
    },
    {
        "src": "mysrc",
        "url": "/myUrl",
        "title": "My title",
        "date": 1375468200000,
        "image_url": "[/myImage]",
        "content": null,
        "author": "[my author]",
        "category": "my category",
        "comments": null,
        "country": "my country",
        "city": "[my city]",
        "commentcount": 1
    }]);    
}