var express = require('express');
var router = express.Router();

router.get('/', function(req, res, next) {

  var db = req.db;
  var collection = db.get('names');

  collection.find({},{},function(e,docs){

    console.log(docs);

    res.render('test', {
        "userlist" : docs
    });

  });

});

module.exports = router;
