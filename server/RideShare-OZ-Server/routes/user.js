var express = require('express');
var router = express.Router();
var User = require('../models/User.js');

/* Login. */
router.get('/login', function(req, res, next) {
  res.status(500).send('Something broke!');
});

router.get('/create', function(req, res, next) {
  var user = new User();
  user.username = req.query.username;
  user.save(function(err, doc){
    res.json(doc);
  });
});

router.get('/getall', function(req, res, next) {
  User.find({}, function(err, users){
    res.json(users);
  });
});

module.exports = router;
