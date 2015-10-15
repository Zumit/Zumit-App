var express = require('express');
var router = express.Router();
var User = require('../models/User.js');
var Group = require('../models/Group.js');


/* GET users listing. */
router.get('/getall', function(req, res, next) {

  Group.find().populate('members requests.user',
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

router.post('/update', function(req, res, next){
  Group.findOne({'_id': req.body.group_id}, function(err, group){
    if (group) {
      group.groupname = (req.body.name)? req.body.name : group.groupname;
      group.location = (req.body.location)? req.body.location : group.location;
      group.introduction = (req.body.introduction)? req.body.introduction : group.introduction;
      group.group_location = (req.body.g_lon !== null && req.body.g_lat !== null)? [req.body.g_lon, req.body.g_lat] : group.group_location;
      console.log(group.group_location);
      group.save(function(err){
        res.json(group);
      });
    } else {
      Group.createGroup(req,function(group){
        res.json(group);
      });
    }
  });
});

module.exports = router;
