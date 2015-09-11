var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var GroupsSchema = new Schema({
  groupname: String,
  introduction: String,
  group_location: [Number],
  admins: [{type: Schema.Types.ObjectId, ref: 'User' }],
  members: [{type: Schema.Types.ObjectId, ref: 'User' }],
  requests: [{user:{type: Schema.Types.ObjectId, ref: 'User' },requestDate:{ type: Date, default: Date.now }}],
});

module.exports = mongoose.model('Group', GroupsSchema);
