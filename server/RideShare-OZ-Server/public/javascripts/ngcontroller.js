angular.module('app', ['ngRoute','ngAnimate'])

//---------------
// Services
//---------------
  .factory('Items', ['$http', function($http){
    return $http.get('/test.php');
  }])
//---------------
// Controllers
//---------------
  .controller('EventsController', ['$scope', '$http', function ($scope, $http) {

    $scope.show_type = 0;
    // $scope.date_time = "2014-01-01T00:01:01";
    // $scope.edit_enable = false;
    // var http = $http.get('/test.php');

    $scope.alert_test = function(text){
    }

    $scope.add_data = function(){
      if ($scope.show_type == 1 ) {
        var new_cus = {'customerId': parseInt($scope.customers[0].customerId)+1,
                       'firstName':'',
                       'lastName':'',
                       'email':'',
                       'mobile':'',
                       'location':'',
                      };
        $scope.customers.unshift(new_cus);
        $scope.detail_id = new_cus.customerId;
      } else {
        var datetime = $scope.events[0].eventDateTime;
        // alert(new Date().toISOString().replace('T', ' ').slice(0, 19));
        var new_eve = {'eventId': parseInt($scope.events[0].eventId)+1,
                       'customerId':'',
                       'eventDateTime': new Date().toISOString().slice(0, 19),
                       'triggeredLocation':'',
                       'triggerType':'',
                      };
        $scope.events.unshift(new_eve);
        $scope.detail_id = new_eve.eventId;
      }
    }

    $scope.show_detail = function(id){
      // alert($scope.detail_id);
      if($scope.detail_id===0){
        $scope.detail_id = id;
      } else {
        update_data($scope.show_type, $scope.detail_id);
        $scope.detail_id = 0;
      }
    }

    function update_data(type, id) {
        if (type == 1) {
          var c = $scope.customers.filter(function(cus){
            return cus.customerId==id;
          });
          c = c[0];
          url = "/update_data.php?type=1" +
                "&customerId=" + c.customerId +
                "&firstName=" + c.firstName +
                "&lastName=" + c.lastName +
                "&email=" + c.email +
                "&mobile=" + c.mobile +
                "&location=" + c['location']
                ;
        $http.get(url).success(function(data){
          console.log('updated');
        }).error(function(data, status){
          console.log(data, status);
        });
      } else if(type == 2) {
          var e = $scope.events.filter(function(eve){
            return eve.eventId==id;
          });
          e = e[0];
          url = "/update_data.php?type=2" +
                "&eventId=" + e.eventId +
                "&customerId=" + e.customerId +
                "&eventDateTime=" + e.eventDateTime +
                "&triggeredLocation=" + e.triggeredLocation +
                "&triggerType=" + e.triggerType
                ;
          // alert(url);
        $http.get(url).success(function(data){
          $scope.show_events();
          console.log('updated');
        }).error(function(data, status){
          console.log(data, status);
        });
      }
    }

    $scope.del_data = function(id){
      $scope.detail_id = 0;
      var url = "/del_data.php?type=" + $scope.show_type +
                "&id=" + id;
      // alert(url);

      $http.get(url).success(function(data){
        if ($scope.show_type == 1) {
          $scope.show_customer();
        } else {
          $scope.show_events();
        }
        console.log('deleted');
      }).error(function(data, status){
        console.log(data, status);
      });

    }

    $scope.show_customer=function(){
      // alert("test");
      // var Items = $http.get('/get_data.php');
      $scope.detail_id = 0;
      $scope.show_type = 1;
      $http.get('/get_data.php?type=1').success(function(data){
        $scope.customers = data;
      }).error(function(data, status){
        console.log(data, status);
        $scope.customers = [];
      });
    }

    $scope.show_events=function(){
      // alert("test");
      $scope.detail_id = 0;
      $scope.show_type = 2;
      $http.get('/get_data.php?type=2').success(function(data){
        $scope.events = data;
      }).error(function(data, status){
        console.log(data, status);
        $scope.events = [];
      });
    }
  }])

//---------------
// Routes
//---------------
  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider
      .when('/', {
        // templateUrl: '/todos.html',
        controller: 'EventsController'
      })
  }]);
