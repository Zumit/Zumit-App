var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var EventSchema = new Schema({
  eventName: String,
  eventlocation: String,
  eventInfo: String,
  startTime:Date,
  adminID: {type: Schema.Types.ObjectId, ref: 'User' },
  joinedMembers: [{type: Schema.Types.ObjectId, ref: 'User' }],
  rquest: [{user:{type: Schema.Types.ObjectId, ref: 'User' },requestDate:{ type: Date, default: Date.now }}],
});

module.exports = mongoose.model('Event', EventSchema);