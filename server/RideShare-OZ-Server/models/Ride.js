var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var User = require('../models/User.js');
var Group = require('../models/Group.js');
var RideSchema = new Schema({
  start_time: Date,
  seats: Number,
  start_point: {type:[Number],index:'2d'}, // Lat, Lng
  end_point: [Number],
  driver: {type: Schema.Types.ObjectId, ref: 'User' },
  group: {type: Schema.Types.ObjectId, ref: 'Group'},
  events:{type: Schema.Types.ObjectId, ref: 'Event'},
  passengers: [{ type: Schema.Types.ObjectId, ref: 'User' }],
  requests: [{user:{type: Schema.Types.ObjectId, ref: 'User' },requestDate:{ type: Date, default: Date.now },state:String}],
  updated_at: { type: Date, default: Date.now },
});


RideSchema.statics.getAllRides = function(callback){
  this.find().populate('driver', 'username').exec({}, function(err, rides){
    callback(rides);
  });
};

RideSchema.statics.createRide = function(req,callback){
  var Ride = mongoose.model('Ride');
  var ride =new Ride();
  ride.start_time = Date.now();
  ride.seats = req.query.seat;
  var start_lon=req.query.s_lon;
  var start_lat=req.query.s_lat;

  ride.start_point=[Number(start_lon),Number(start_lat)];
  var end_lon=req.query.e_lon;
  var end_lat=req.query.e_lat;
  ride.end_point=[Number(end_lon),Number(end_lat)];
  User.findById(req.query.driverid, function(err, user){
    ride.driver=user;
    Group.findById(req.query.groupid,function(err,group){
      ride.group=group;
      ride.save(function(err, doc){
      if (err) {
        console.log(err);
      }
        callback(doc);
      });
    });
  });
};

RideSchema.statics.searchRide =function(req,callback){

  var coords=[];
  coords[0]=req.query.lon;
  coords[1]=req.query.lat;
  var maxDistance=1;
  var limit=10;
  var groupID=req.query.groupId;

  this.find({group:groupID, start_point: {
      $near:coords,
      $maxDistance: maxDistance
    }
  }).limit(limit).exec(function(err, locations) {

    callback(locations);
  });
};

RideSchema.methods.addRequest= function(user_id, callback){
  /* console.log(this); */
  this.requests.push({'user':user_id,'state':"unaccept"});
  this.save(function(err, doc){
    callback(doc);
  });
};

module.exports = mongoose.model('Ride', RideSchema);
