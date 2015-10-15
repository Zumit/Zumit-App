/**
 * Master Controller
 */

angular.module('RDash')
.controller('MasterCtrl', ['$scope', '$cookieStore', MasterCtrl]);

function MasterCtrl($scope, $cookieStore) {
  /**
   * Sidebar Toggle & Cookie Control
   */
  var mobileView = 992;

  $scope.getWidth = function() {
    return window.innerWidth;
  };

  $scope.$watch($scope.getWidth, function(newValue, oldValue) {
    if (newValue >= mobileView) {
      if (angular.isDefined($cookieStore.get('toggle'))) {
        $scope.toggle = ! $cookieStore.get('toggle') ? false : true;
      } else {
        $scope.toggle = true;
      }
    } else {
      $scope.toggle = false;
    }

  });

  $scope.toggleSidebar = function() {
    $scope.toggle = !$scope.toggle;
    $cookieStore.put('toggle', $scope.toggle);
  };

  window.onresize = function() {
    $scope.$apply();
  };
}

/**
 * Alerts Controller
 */

angular
.module('RDash')
.controller('AlertsCtrl', ['$scope', AlertsCtrl]);

function AlertsCtrl($scope) {
  $scope.alerts = [{
    type: 'success',
    msg: 'Thanks for visiting! Feel free to create pull requests to improve the dashboard!'
  }, {
    type: 'danger',
    msg: 'Found a bug? Create an issue with as many details as you can.'
  }];

  $scope.addAlert = function() {
    $scope.alerts.push({
      msg: 'Another alert!'
    });
  };

  $scope.closeAlert = function(index) {
    $scope.alerts.splice(index, 1);
  };
}

/**
 * Table Controller
 */

angular
.module('RDash')
.controller('TableCtrl', ['$scope', '$http', TableCtrl]);

function TableCtrl($scope, $http) {

  $scope.groupCurrentPage = 1;
  $scope.memberCurrentPage = 1;
  $scope.requestCurrentPage = 1;
  $scope.groupPageSize = 4;
  $scope.memberPageSize = 6;
  $scope.requestPageSize = 6;
  $scope.groups = [];
  $scope.requests = [];
  $scope.members = [];
  $scope.selectedIndex = -1;

  $http.get('group/getall').success(function(data){
    $scope.groups = data;
  }).error(function(data, status){
    console.log(data, status);
  });

  $scope.testMsg = [{
    msg: '============test========='
  }];

  $scope.setMemReq = function(index){
    $scope.selectedIndex = index;
    var total_index = ($scope.groupCurrentPage - 1) * $scope.groupPageSize + index;
    console.log("======================", total_index);
    $scope.requests = $scope.groups[total_index].requests;
    $scope.members = $scope.groups[total_index].members;
    // console.log($scope.requests);
  };

  $scope.pageChangeHandler = function(num) {
    console.log('going to page ' + num);
    $scope.groupCurrentPage = num;
    $scope.selectedIndex = -1;
    $scope.requests = [];
    $scope.members = [];
  };

  $scope.acceptReq = function(username) {
    // console.log("=============acc");
    var request = $http({
      method: "post",
      url: "test",
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8'
      },
      data: {
        username: 'maxzhx@gmail.com',
        // name: "Kim",
        // status: "Best Friend"
      }
    });
    request.success(function(data){
      console.log(data);
    }).error(function(data, status){
      console.log(data, status);
    });
  };
}
