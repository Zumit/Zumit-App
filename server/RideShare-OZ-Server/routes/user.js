var express = require('express');
var router = express.Router();
var User = require('../models/User.js');

/* Login. */
router.post('/login', function(req, res, next) {
  User.findOne({'username': req.userinfo.email}, function(err, user){
    if (!user) {
      var new_user = new User({'username': req.userinfo.email});
      new_user.save(function(err){
        if (err) {
          res.end(err);
        } else {
          new_user.getRides(function(rides){
            res.json(rides);
          });
        }
      });
    } else {
      user.getRides(function(rides){
        res.json(rides);
      });
    }
  });
  /* res.status(500).send('Something broke!'); */
});

/* router.get('/create', function(req, res, next) { */
  /* var user = new User(); */
  /* user.username = req.query.username; */
  /* user.address= 'Chadstone'; */
  /* user.phone='112233'; */
  /* user.save(function(err, doc){ */
    /* res.json(doc); */
  /* }); */
/* }); */

router.post('/info', function(req, res, next) {
  User.findOne({'username': req.userinfo.email}, function(err, user){
    res.json(docs);
  });
});

router.get('/getall', function(req, res, next) {
  User.getAllUser(function(rides){
    res.json(rides);
  });
});

module.exports = router;
