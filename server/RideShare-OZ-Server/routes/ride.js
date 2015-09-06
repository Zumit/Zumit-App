var express = require('express');
var router = express.Router();
var Ride = require('../models/Ride.js');
var User = require('../models/User.js');

router.get('/create', function(req, res, next) {

  Ride.createRide(req, function(doc){

    res.json(doc);
  });

});

router.get('/getall', function(req, res, next) {
   Ride.getallRide(function(rides){
    res.json(rides);
   });
});

router.get('/search',function(req,res){
  
   Ride.searchRide(req,function(locations){
    res.json(locations);
   });
});


module.exports = router;
