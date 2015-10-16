var express = require('express');
var router = express.Router();
var passport = require('passport');
var Group = require('../models/Group.js');

router.get('/', function(req, res, next) {

  passport.authenticate('local', { successRedirect: '/admin',
                                   failureRedirect: '/',
                                   failureFlash: true });
  // Group.find().populate('members',
      // 'username phone driver_license').exec({}, function(err, groups){
    // // res.json(groups);
    // res.render('admin', {
      // title: 'Administrator',
      // groups: groups
    // });
  // });

});

router.get('/test', function(req, res, next) {
  res.render('test', {
      title: 'Administrator',
  });
});

module.exports = router;
