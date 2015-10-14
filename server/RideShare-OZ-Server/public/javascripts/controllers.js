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

  $http.get('group/getall').success(function(data){
    $scope.groups = data;
  }).error(function(data, status){
    console.log(data, status);
  });

  $scope.groups;
  $scope.request;
  $scope.members;

  $scope.testMsg = [{
    msg: '============test========='
  }];

  $scope.setMemReq = function(index){
    // console.log("======================", index);
    $scope.request = $scope.groups[index].requests;
    $scope.members = $scope.groups[index].members;
    // console.log($scope.members);
  };
}
