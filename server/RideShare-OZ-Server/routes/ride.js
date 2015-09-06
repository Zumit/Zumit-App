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
