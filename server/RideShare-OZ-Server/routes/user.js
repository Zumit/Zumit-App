var express = require('express');
var router = express.Router();
var User = require('../models/User.js');

/* Login. */
router.get('/login', function(req, res, next) {
  // User
  var user = new User();
  user.username = 'test7';
  user.save(function(err, doc){
    User.find({}, function (err, users) {
      res.json(users);
    });
  });
  // User.find({}, function (err, users) {
    // res.json(users);
  // });

});

module.exports = router;
