var express = require('express');
var router = express.Router();
var User = require('../models/User.js');

/* Login. */
router.post('/login', function(req, res, next) {
  User.findOne({'username': req.userinfo.email}, function(err, user){
    if (!user) {
      User.createUser(req.userinfo.email, function(new_user){
        new_user.getRides(function(rides){
          res.json(rides);
        });
      });
    } else {
      user.getRides(function(rides){
        res.json(rides);
      });
    }
  });
  /* res.status(500).send('Something broke!'); */
});

router.post('/info', function(req, res, next) {
  User.findOne({'username': req.userinfo.email}, function(err, user){
    res.json(docs);
  });
});

router.get('/getall', function(req, res, next) {
  User.getAllUsers(function(rides){
    res.json(rides);
  });
});

module.exports = router;
