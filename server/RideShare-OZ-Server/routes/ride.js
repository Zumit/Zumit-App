var express = require('express');
var router = express.Router();
var Ride = require('../models/Ride.js');
var User = require('../models/User.js');

/* GET users listing. */
router.get('/create', function(req, res, next) {

  var ride = new Ride();
  ride.start_time = Date.now();
  ride.seat = 5;
  ride.start_point=[2,3];
  ride.end_point=[10,34];
  User.findById('55e822ad281137b018d95333', function(err, user){
    ride.driver=user;
    ride.save(function(err, doc){
      if (err) {
        console.log(err);
      }
      Ride.find({}, function(err, rides){
        res.json(rides);
      });
    });
  });
});

router.get('/show', function(req, res, next) {

  Ride.findOne().populate('driver').exec(function(err, ride){
    res.json(ride);
  });
});

module.exports = router;
