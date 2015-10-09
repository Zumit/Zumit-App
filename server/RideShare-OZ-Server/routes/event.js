var express = require('express');
var router = express.Router();
var Event = require('../models/Event.js');

router.get('/getall', function(req, res, next) {

  Event.find({}, function(err, events){
    res.json(events);
  });

});

router.get('/create', function(req, res, next) {

Event.createEvent(req, function(event){
		 res.json(event);
	});

});

module.exports = router;