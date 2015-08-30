var express = require('express');
var router = express.Router();

/* GET users listing. */
router.get('/adduser', function(req, res, next) {
  res.send('respond with a resource');
});

router.get('/userinfo', function(req, res, next) {
  res.send('respond with a resource');
});

module.exports = router;
