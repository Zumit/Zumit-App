var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var UserSchema = new Schema({
  username: String,
  note: String,
  address: String,
  phone: String,
  DoB: Date,
  driver_license: String,
  groups: [{group:{type: Schema.Types.ObjectId, ref: 'Group' }, state:String}],
  updated_at: { type: Date, default: Date.now },
  driver_rate:{ type: Number, default:0},
  passenger_rate:{ type: Number,default:0},
});

UserSchema.statics.createUser = function(username, callback){
  var User = mongoose.model('User');
  var new_user = new User({'username': username});
  new_user.save(function(err){
    /* console.log("========"); */
    /* console.log(new_user); */
    callback(new_user);
  });
};

UserSchema.statics.getAllUsers = function(callback){
  this.find({}).populate('groups.group', 'groupname').exec({}, function(err, users){
    callback(users);
  });
};

UserSchema.methods.getRides = function(callback){
  var conditions = {$or:[{'driver':this}, {'passengers.user':this}, {'requests.user':this}]};
  /* var conditions = {}; */
  this.model('Ride').find(conditions).populate('driver passengers.user requests.user',
      'username phone driver_license').exec({}, function(err, rides){
    callback(rides);
  });
};

UserSchema.statics.getGroups = function(req,callback){
  this.findById(req.userinfo._id).populate('groups.group',
      'groupname introduction').exec({}, function(err,user){
    callback(user.groups);  
  });
};

UserSchema.method.getAllGroups = function(req,callback){
  this.model('Group').find()
  // this.findById(req.userinfo._id).populate('groups.group',
      // 'groupname introduction').exec({}, function(err,user){
    // callback(user.groups);  
  // });
};

module.exports = mongoose.model('User', UserSchema);
