var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var GroupsSchema = new Schema({
  groupName: String,
  grouplocation: String,
  groupInfo: String,
  adminID: {type: Schema.Types.ObjectId, ref: 'User' },
  groupMembers: [{type: Schema.Types.ObjectId, ref: 'User' }],
  rquest: [{user:{type: Schema.Types.ObjectId, ref: 'User' },requestDate:{ type: Date, default: Date.now }}],
});

module.exports = mongoose.model('Group', GroupsSchema);
