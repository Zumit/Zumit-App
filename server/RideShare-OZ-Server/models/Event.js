var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var User = require('../models/User.js');

var EventSchema = new Schema({
  eventName: String,
  eventInfo: String,
  eventLocation: [Number],
  location: String,
  startTime:Date,
  endTime:Date,
  organizer: {type: Schema.Types.ObjectId, ref: 'User' },
});


EventSchema.statics.createEvent= function(req,callback) {
var Event = mongoose.model('Event');
var events = new Event();
events.eventName = req.query.event_name;
var lon=req.query.e_lon;
var lat=req.query.e_lat;
events.eventLocation=[lon,lat];
events.eventInfo=req.query.eventInfo;
events.startTime=req.query.start_time;
events.endTime=req.query.end_time;
events.location=req.query.location;
User.findById(req.userinfo._id, function(err, user){
  console.log(user);
  events.organizer=user;
  events.save(function(err, doc){
    if (err) {
      console.log(err);
    }
    callback(doc);
    });
  });
};

module.exports = mongoose.model('Event', EventSchema);
