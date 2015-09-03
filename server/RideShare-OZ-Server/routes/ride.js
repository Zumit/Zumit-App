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
<<<<<<< HEAD
  User.findById('55e822ad281137b018d95333', function(err, user){
=======
  User.findById(req.query.driverid, function(err, user){
>>>>>>> 783bd9bb0e4497b7c0d8336dd7b2ba3c6eeb50c0
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

module.exports = router;
