var express = require('express');
var router = express.Router();
var Ride = require('../models/Ride.js');

router.get('/create', function(req, res, next) {
  Ride.createRide(req, function(doc){
    res.json(doc);
  });
});

router.get('/getall', function(req, res, next) {
  Ride.getAllRides(function(rides){
    res.json(rides);
  });
});

router.get('/search',function(req,res){
  Ride.searchRide(req,function(locations){
    res.json(locations);
  });
});

router.get('/request',function(req,res){
  Ride.findById(req.query.ride_id, function(err, ride){
    ride.addRequest(req.query.user_name,req, function(updated_ride){
      res.json(updated_ride);
    });
  });
  
});

router.get('/cancel',function(req,res){
  Ride.cancelRide(req, function(flag){
    res.json(flag);
  });
});

module.exports = router;
