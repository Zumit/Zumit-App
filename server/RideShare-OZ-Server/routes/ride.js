var express = require('express');
var router = express.Router();
var Ride = require('../models/Ride.js');
var User = require('../models/User.js');

router.get('/create', function(req, res, next) {

  var ride = new Ride();
  ride.start_time = Date.now();
  ride.seat = 5;
  ride.start_point=[20,30];
  ride.end_point=[10,34];
  User.findById(req.query.driverid, function(err, user){
    ride.driver=user;
    ride.save(function(err, doc){
      if (err) {
        console.log(err);
      }
      res.json(doc);
    });
  });
});

router.get('/getall', function(req, res, next) {
  Ride.find().populate('driver', 'username').exec({}, function(err, rides){
    res.json(rides);
  });
});


router.get('/search',function(req,res){
lon=req.query.lon;
lat=req.query.lat;
groupID=req.query.groupId;



})

module.exports = router;
