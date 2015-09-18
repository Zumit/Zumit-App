var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var CreditSchema = new Schema({
  type : String,
  receiver: {type: Schema.Types.ObjectId, ref: 'User' },
  sender : {type: Schema.Types.ObjectId, ref: 'User' },
  rate : Double,
  rideID: [{type: Schema.Types.ObjectId, ref: 'Ride' }],
  updateDate:{ type: Date, default: Date.now }
  comment: String,
});

module.exports = mongoose.model('Credit', CreditSchema);