var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var schedule = require('node-schedule');
var Ride = require('./models/Ride.js');

var http = require('http');
var passport = require('passport');
var LocalStrategy = require('passport-local').Strategy;

// database
/* var mongo = require('mongodb'); */
var mongoose = require('mongoose');

mongoose.connect('mongodb://localhost/RideShare', function(err) {
  if(err) {
    console.log('database connection error', err);
  } else {
    console.log('database connection successful');
  }
});

var rule = new schedule.RecurrenceRule();  
rule.minute = [0, 20, 40];
var date=Date.now(); 
var j = schedule.scheduleJob(rule, function(){  
    Ride.find({},function(err,rides){
      rides.forEach(function(ride){
        if(new Date(ride.arrival_time) < new Date(date)){
          Ride.findByIdAndUpdate(ride._id,{$set:{'finished':true}},function(err,update){
            //console.log("update");
          });
        }
      });

    });
});


var auth = require('./authentication.js');
var index = require('./routes/index');
var user = require('./routes/user');
var ride = require('./routes/ride');
var group = require('./routes/group');
var msg = require('./routes/msg');
var test = require('./routes/test');
var events = require('./routes/event');
var credit= require('./routes/credit');
var admin= require('./routes/admin');

// model
var User = require('./models/User.js');

var app = express();

app.set('port', process.env.PORT || 1337);

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser('your secret here'));
app.use(express.static(path.join(__dirname, 'public')));
app.use('/bower_components',  express.static(__dirname + '/bower_components'));

// app.use(express.methodOverride());
// app.use(express.cookieParser('your secret here'));
// app.use(express.session());
app.use(passport.initialize());
app.use(passport.session());

app.use(function(req,res,next){
  if (req.method === 'POST') {
    auth.auth_token(req.body.token, function(doc){
      if (req.body.username) {
        doc = {'email': req.body.username};
      }
      if (!doc.email) {
        res.end('Invalid Token');
      } else {
        req.userinfo = doc;
        User.find({'username':doc.email},function(err, users){
          console.log(users);
          if (users.length !== 0) {
          req.userinfo._id = users[0]._id;
          }

          next();
        });
      }

    });
  } else {
    if (req.query.username) {
      console.log("ssss======");
      req.userinfo = {'email': req.query.username};
    }
    next();
  }
  // console.log("=========");
  // next();
});

app.use('/', index);
app.use('/user', user);
app.use('/ride', ride);
app.use('/group', group);
app.use('/msg', msg);
app.use('/test', test);
app.use('/event', events);
app.use('/credit', credit);
app.use('/admin', admin);

app.get('/templates/:name', function(req, res){
  res.render('templates/' + req.params.name);
});

// passport config
var Account = require('./models/Account');

// use static authenticate method of model in LocalStrategy
passport.use(new LocalStrategy(Account.authenticate()));

// use static serialize and deserialize of model for passport session support
passport.serializeUser(Account.serializeUser());
passport.deserializeUser(Account.deserializeUser());

app.get('/register', function(req, res) {
  res.render('register', { });
});

app.post('/register', function(req, res) {
  Account.register(new Account({ username : req.body.username }), req.body.password, function(err, account) {
    if (err) {
      return res.render('register', { account : account });
    }

    passport.authenticate('local')(req, res, function () {
      res.redirect('/admin');
    });
  });
});

app.get('/login', function(req, res) {
  res.render('login', { user : req.user });
});

app.post('/login', passport.authenticate('local'), function(req, res) {
  res.redirect('/');
});

app.get('/logout', function(req, res) {
  req.logout();
  res.redirect('/');
});

app.get('/ping', function(req, res){
  res.send("pong!", 200);
});

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
  app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
      message: err.message,
      error: err
    });
  });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
  res.status(err.status || 500);
  res.render('error', {
    message: err.message,
    error: {}
  });
});


module.exports = app;
