var express = require('express');
var router = express.Router();
var Group = require('../models/Group.js');

router.get('/', function(req, res, next) {

  Group.find().populate('members',
      'username phone driver_license').exec({}, function(err, groups){
    // res.json(groups);
    res.render('admin', {
      title: 'Administrator',
      groups: groups
    });
  });

});

module.exports = router;
