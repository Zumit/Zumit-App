var express = require('express');
var router = express.Router();
var User = require('../models/User.js');
var Group = require('../models/Group.js');


/* GET users listing. */
router.get('/getall', function(req, res, next) {

  Group.find().populate('members',
      'username phone driver_license').exec({}, function(err, groups){
    res.json(groups);
  });

});

router.get('/getallNoMember', function(req, res, next) {
  Group.find({}, {'members':0}).exec({}, function(err, groups){
    res.json(groups);
  });
});

router.post('/getallNoMember', function(req, res, next) {
  Group.find({}, {'members':0}).exec({}, function(err, groups){
    res.json(groups);
  });
});

router.post('/create', function(req, res) {

  Group.createGroup(req,function(groups){
  res.json(groups);

 });
});

router.post('/request',function(req,res){
 Group.addRequest(req,function(group){
    res.json(group);
 });
  
});

router.post('/accept', function(req, res) {

  Group.acceptRequest(req,function(groups){
  res.json(groups);

 });
});

router.post('/reject', function(req, res) {

  Group.rejectRequest(req,function(groups){
  res.json(groups);

 });
});

router.post('/leave', function(req, res) {

  Group.leaveGroup(req,function(groups){
  res.json(groups);

 });
});



module.exports = router;
