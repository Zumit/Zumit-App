var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var User = require('../models/User.js');

var GroupsSchema = new Schema({
  groupname: String,
  introduction: String,
  group_location: [Number],
  location :String,
  adminID: {type: Schema.Types.ObjectId, ref: 'User' },
  members: [{user:{type: Schema.Types.ObjectId, ref: 'User' }}],
  requests: [{
    user:{type: Schema.Types.ObjectId, ref: 'User' },
    requestDate:{ type: Date, default: Date.now }
  }],
});


GroupsSchema.statics.createGroup= function(req,callback) {
  var Group = mongoose.model('Group');
  var groups = new Group();
  groups.groupname = req.query.name;
  var lon=req.query.g_lon;
  var lat=req.query.g_lat;
  groups.gruop_location=[lon,lat];
  groups.location=rq.query.location;
  groups.introduction=req.query.introduction;
  User.findById(req.query.admin_id, function(err, user){
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

    var check=0;

  this.findById(req.query.group_id,function(err,group){

      group.requests.forEach(function(request){
        if(String(request.user)==String(req.query.user_id))
        {
            check++;
            console.log(check);
        }
      });

       group.members.forEach(function(member){
        if(String(member.user)==String(req.query.user_id))
        {
            check++;
            console.log(check);
        }
      });

     if(check==0){
          console.log(check);
        User.findById(req.query.user_id,function(err,user){
          user.groups.push({'group':req.query.group_id,'state':'request'});
          user.save();
        });

  
          group.requests.push({'user':req.query.user_id});
          group.save(function(err,doc){callback(group);});
          
       
      }else{callback("already request!");}

  });

 
};



  GroupsSchema.statics.acceptRequest= function(req,callback){

 //add use to passenger

   this.findById(req.query.group_id,function(err,doc){
    doc.members.push({user:req.query.user_id});
    doc.save();
  });

  //change
  // User.findById(req.query.user_id,function(err,user){
  // 	user.groups.push(req.query.group_id);
  // 	user.save();
  // }); 

  User.update(
    {'_id':req.query.user_id,'groups.group':req.query.group_id},
    {'$set':{'groups.$.state':'joined'}},function(err,user){
      console.log("use save");
    }
    );

  //delete the user in requests
  
   this.findByIdAndUpdate(req.query.group_id,{$pull:{'requests':{'user':req.query.user_id}}},function(err,doc){
    //doc.save();
    callback(doc);
  });

};



GroupsSchema.statics.rejectRequest= function(req,callback){

User.findByIdAndUpdate(req.query.user_id,{$pull:{'groups':{'group':req.query.group_id}}},function(err,user){
   console.log("user group:Requst delete");
   //callback("success");
});


this.findByIdAndUpdate(req.query.group_id,{$pull:{'requests':{'user':req.query.user_id}}},function(err,doc){
  console.log(doc);
    callback(doc);
  });
};


GroupsSchema.statics.leaveGroup= function(req,callback){

	User.findByIdAndUpdate(req.query.user_id, {$pull:{'groups':{'group':req.query.group_id}}},function(err,groups){
		//console.log(groups);
	});
	this.findByIdAndUpdate(req.query.group_id,{$pull:{'members':{'user':req.query.user_id}}},function(err,groups){
    //console.log(groups);
    callback(groups);
  });

};


module.exports = mongoose.model('Group', GroupsSchema);
