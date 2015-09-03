var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var UserSchema = new Schema({
  username: String,
  note: String,
  address: String,
  phone: String,
  DoB: Date,
  driver_license: String,
  groups: [{type: Schema.Types.ObjectId, ref: 'Group' }],
  rides: [{type: Schema.Types.ObjectId, ref: 'Ride' }],
  events: [{type: Schema.Types.ObjectId, ref: 'Event' }],
  updated_at: { type: Date, default: Date.now },
});

module.exports = mongoose.model('User', UserSchema);
