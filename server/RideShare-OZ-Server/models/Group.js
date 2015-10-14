var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var User = require('../models/User.js');

var GroupsSchema = new Schema({
  groupname: String,
  introduction: String,
  group_location: [Number],
  location :String,
  adminID: {type: Schema.Types.ObjectId, ref: 'User' },
  members: [{type: Schema.Types.ObjectId, ref: 'User' }],
  requests: [{
    user:{type: Schema.Types.ObjectId, ref: 'User' },
    requestDate:{ type: Date, default: Date.now }
  }],
});


GroupsSchema.statics.createGroup= function(req,callback) {
  var Group = mongoose.model('Group');
  var groups = new Group();
  groups.groupname = req.body.name;
  var lon=req.body.g_lon;
  var lat=req.body.g_lat;
  groups.gruop_location=[lon,lat];
  groups.location=rq.body.location;
  groups.introduction=req.body.introduction;
  User.findById(req.userinfo._id, function(err, user){
    groups.adminID=user;
    groups.save(function(err, doc){
      if (err) {
        console.log(err);
      }
      callback(doc);
      });
    });
  };


  GroupsSchema.statics.addRequest= function(req,callback){

          User.findById(req.userinfo._id,function(err,user){

          user.groups.push({'group':req.body.group_id,'state':'request'});
          user.save();

          });


    this.findByIdAndUpdate(req.body.group_id,{$push:{'requests':{'user':req.userinfo._id}}},function(err,doc){
        
        callback(doc);
       });
          

 
};



  GroupsSchema.statics.acceptRequest= function(req,callback){

 //add use to passenger


    this.findByIdAndUpdate(req.body.group_id,{$push:{'members':req.userinfo._id}},function(err,doc){
        
        console.log("add to members");
   });
          

  User.update(
    {'_id':req.userinfo._id,'groups.group':req.body.group_id},
    {'$set':{'groups.$.state':'joined'}},function(err,user){
      console.log("use save");
    }
    );

  //delete the user in requests
  
   this.findByIdAndUpdate(req.body.group_id,{$pull:{'requests':{'user':req.userinfo._id}}},function(err,doc){
    //doc.save();
    callback(doc);
  });

};



GroupsSchema.statics.rejectRequest= function(req,callback){

User.findByIdAndUpdate(req.userinfo._id,{$pull:{'groups':{'group':req.body.group_id}}},function(err,user){
   console.log("user group:Requst delete");
   callback("success");
});


this.findByIdAndUpdate(req.body.group_id,{$pull:{'requests':{'user':req.userinfo._id}}},function(err,doc){
  console.log(doc);
    callback(doc);
  });
};


GroupsSchema.statics.leaveGroup= function(req,callback){

	User.findByIdAndUpdate(
    req.userinfo._id, {
      $pull:{'groups':
        {'group':req.body.group_id}
      }
    },function(err,groups){
		//console.log(groups);
	});
	this.findByIdAndUpdate(
    req.body.group_id,{
      $pull:{'members':
        req.userinfo._id
      }
    },function(err,groups){
    
    callback(groups);
  });

};


module.exports = mongoose.model('Group', GroupsSchema);
