var http = require('http');

http.createServer(function(req, res){
  
  res.writeHead(200, {'Content-Type': 'text/plain'});
  res.end('Hello World!\n This is a test.');

}).listen(3000, 'localhost');

console.log("Server is running.");
