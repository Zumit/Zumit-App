var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var RideSchema = new Schema({
  start_time: Date,
  driver_license: String,
  seats: Number,
  start_point: [Number], // Lat, Lng 
  end_point: [Number],
  driver: {type: Schema.Types.ObjectId, ref: 'User' },
  group: {type: mongoose.Schema.Types.ObjectId, ref: 'Group'},
  passengers: [{ type: Schema.Types.ObjectId, ref: 'User' }],
  requests: [{type: Schema.Types.ObjectId, ref: 'Request' }],
  updated_at: { type: Date, default: Date.now },
});

module.exports = mongoose.model('Ride', RideSchema);
