var express = require('express');
var router = express.Router();
var User = require('../models/User.js');
var Group = require('../models/Group.js');


/* GET users listing. */
router.get('/', function(req, res, next) {

  res.send('respond with a resource');

});

router.get('/create', function(req, res, next) {

  var groups = new Group();
  groups.groupName = 'Melbourne uni';
  groups.grouplocation = 'Parkville';
  User.findById('55e822ad281137b018d95333', function(err, user){
  groups.adminID=user;
  groups.save(function(err, doc){
      if (err) {
        console.log(err);
      }
      Group.find({}, function(err, rides){
        res.json(rides);
      });
    });
  });
});

module.exports = router;
