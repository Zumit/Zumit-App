var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var EventSchema = new Schema({
  eventName: String,
  eventlocation: String,
  eventInfo: String,
  startTime:Date,
  group: {type: Schema.Types.ObjectId, ref: 'Group'},
  organizer: {type: Schema.Types.ObjectId, ref: 'User' },
  Members: [{type: Schema.Types.ObjectId, ref: 'User' }],
});

module.exports = mongoose.model('Event', EventSchema);