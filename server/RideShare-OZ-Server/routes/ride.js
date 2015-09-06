var express = require('express');
var router = express.Router();
var Ride = require('../models/Ride.js');
var User = require('../models/User.js');

router.get('/create', function(req, res, next) {

  var ride = new Ride();
  ride.start_time = Date.now();
  ride.seat = 5;
  ride.start_point=[20,33];
  ride.end_point=[10.01,34.001];
  ride.passengers = ['55ebc3b9b9ccece95d5d1d85', '55ebbd95cbe7567855196aff'];
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
  Ride.find().populate('driver', 'username').populate('passengers', 'username').exec({}, function(err, rides){
    res.json(rides);
  });
});


router.get('/search',function(req,res){
  var coords=[];
coords[0]=req.query.lon;
coords[1]=req.query.lat;
var maxDistance=1;
var limit=10;
//groupID=req.query.groupId;

Ride.find({
      start_point: {
        $near:coords,
        $maxDistance: maxDistance
      }
    }).limit(limit).exec(function(err, locations) {
      if (err) {
        return res.json(500, err);
      }

      res.json(locations);
    });;

});

module.exports = router;
