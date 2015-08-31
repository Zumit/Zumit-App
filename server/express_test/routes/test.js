var express = require('express');
var router = express.Router();

router.get('/', function(req, res, next) {

  var db = req.db;
  var collection = db.get('names');

  collection.find({},{},function(e,docs){

  res.end(JSON.stringify(docs));

    // res.render('test', {
      // "info": JSON.stringify(docs)
        // // "userlist" : docs
    // });

  });

});

module.exports = router;
