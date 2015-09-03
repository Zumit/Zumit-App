var express = require('express');
var router = express.Router();

/* GET users listing. */
router.get('/', function(req, res, next) {

  var db = req.db;
  var collection = db.get('msgs');

  res.send('respond with a resource');

});

module.exports = router;
