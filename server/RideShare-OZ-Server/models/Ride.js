var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var RideSchema = new Schema({
  start_time: Date,
  driver_license: String,
  seats: Number,
  start_point: {type:String,coordinates:[Number]}, // Lat, Lng 
  end_point: [Number],
  driver: {type: Schema.Types.ObjectId, ref: 'User' },
  group: {type: mongoose.Schema.Types.ObjectId, ref: 'Group'},
  events:{type: mongoose.Schema.Types.ObjectId, ref: 'Event'},
  passengers: [{ type: Schema.Types.ObjectId, ref: 'User' }],
  requests: [{user:{type: Schema.Types.ObjectId, ref: 'Request' },requestDate:{ type: Date, default: Date.now }}],
  updated_at: { type: Date, default: Date.now },
});

module.exports = mongoose.model('Ride', RideSchema);
