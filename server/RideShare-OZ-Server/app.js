var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');

// database
var mongo = require('mongodb');
var monk = require('monk');
var db = monk('localhost:27017/RideShare');

var index = require('./routes/index');
var user = require('./routes/user');
var ride = require('./routes/ride');
var group = require('./routes/group');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

// Make our db accessible to our router
app.use(function(req,res,next){
  req.db = db;
  next();
});

app.use('/', index);
app.use('/user', user);
app.use('/ride', ride);
app.use('/group', group);
app.use('/msg', group);

app.get('/gettest', function(req, res){
      console.log(req.query.msg);
      res.end("GET request received\n --RideShare-OZ-Server\n msg = " + req.query.msg);
      // console.log(request.body.user.email);
});

app.post('/posttest', function(req, res){
      console.log(req.body.token);
      res.end("POST request received\n --RideShare-OZ-Server\n token = " + req.body.token);
      // console.log(request.body.user.email);
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
