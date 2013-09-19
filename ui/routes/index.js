
/*
 * GET home page.
 */

exports.home = function(req, res){
  var query = req.param("q") || 'Aam Aadmi Party';
  res.render('index', { user_query: query });
};