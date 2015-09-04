var express = require('express');
var router = express.Router();
var User = require('../models/User.js');

/* Login. */
router.post('/login', function(req, res, next) {
  User.findOne({'username': req.userinfo.email}, function(err, user){
    if (!user) {
      console.log('User not found');
      var new_user = new User();
      new_user.username = req.userinfo.email;
      new_user.save();
      res.end('Hello, new user: ' + req.userinfo.email);
    } else {
      console.log('User found');
      res.end('Welcome back, ' + req.userinfo.email);
    }
  });
  /* res.status(500).send('Something broke!'); */
});

router.get('/create', function(req, res, next) {
  var user = new User();
  user.username = req.query.username;
  user.address= 'Chadstone';
  user.phone='112233';
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
