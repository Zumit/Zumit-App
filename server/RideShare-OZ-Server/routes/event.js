var express = require('express');
var router = express.Router();
var Event = require('../models/Event.js');

router.get('/getall', function(req, res, next) {

  Event.find({}, function(err, events){
    res.json(events);
  });

});

router.post('/create', function(req, res, next) {

Event.createEvent(req, function(event){
		 res.json(event);
	});

});

router.post('/update', function(req, res, next) {

  Event.findOne({'_id': req.body.event_id}, function(err, event){
    console.log("============");
    if (event) {
      event.eventName = (req.body.event_name)? req.body.event_name : event.eventName;
      event.location = (req.body.location)? req.body.location : event.location;
      event.eventInfo = (req.body.eventInfo)? req.body.eventInfo : event.eventInfo;
      event.startTime = (req.body.start_time)? req.body.start_time : event.startTime;
      event.endTime = (req.body.end_time)? req.body.end_time : event.endTime;
      console.log(req.body.e_lat);
      event.eventLocation = (req.body.e_lon !== null && req.body.e_lat !== null)? [req.body.e_lon, req.body.e_lat] : event.eventLocation;
      console.log(event.eventLocation);
      event.save(function(err){
        res.json(event);
      });
    } else {
      Event.createEvent(req,function(event){
        res.json(event);
      });
    }
  });

});

module.exports = router;
