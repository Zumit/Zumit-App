var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var CreditSchema = new Schema({
  Username: {type: Schema.Types.ObjectId, ref: 'User' },
  passengerCredit: double,
  driverCredit: double,
  rideID: [{type: Schema.Types.ObjectId, ref: 'Ride' }],
  updateDate:{ type: Date, default: Date.now }
 
});

module.exports = mongoose.model('Credit', CreditSchema);