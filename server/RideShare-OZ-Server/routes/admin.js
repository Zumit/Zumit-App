var express = require('express');
var router = express.Router();
var passport = require('passport');
var Group = require('../models/Group.js');

router.get('/', function(req, res, next) {

  if (req.user) {
    res.render('admin', {
      title: 'Administrator',
      user: req.user.username
    });
  } else {
    res.redirect('/login');
  }

});

router.get('/test', function(req, res, next) {
  res.render('test', {
      title: 'Administrator',
  });
});

module.exports = router;
