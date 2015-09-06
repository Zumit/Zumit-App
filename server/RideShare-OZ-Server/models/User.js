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

UserSchema.statics.getAllUser = function(callback){
  this.find({}, function(err, users){
    callback(users);
  });
};

UserSchema.methods.getRides = function(callback){
  var conditions = {$or:[{'driver':'55ebc26a519891745c72ccb3'}, {'passengers':'55ebc26a519891745c72ccb3'}]};
  /* var conditions = {}; */
  this.model('Ride').find(conditions, function(err, rides){
    callback(rides);
  });
};

module.exports = mongoose.model('User', UserSchema);
