var express = require('express');
var router = express.Router();
var User = require('../models/User.js');

/* Login. */
router.get('/login', function(req, res, next) {
  res.status(500).send('Something broke!');
});

router.get('/create', function(req, res, next) {
  var user = new User();
<<<<<<< HEAD
  user.username = 'test7';
  user.address= 'Chadstone';
  user.phone='112233';
=======
  user.username = req.query.username;
>>>>>>> 783bd9bb0e4497b7c0d8336dd7b2ba3c6eeb50c0
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
