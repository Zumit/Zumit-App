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
 * Group Controller
 */

angular
.module('RDash')
.controller('GroupCtrl', ['$scope', '$http', 'groupDataFactory', GroupCtrl]);

function GroupCtrl($scope, $http, groupDataFactory) {

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

  groupDataFactory.getGroupData().then(function(res){
    console.log(res);
    $scope.groups = res;
  });
  // $http.get('group/getall').success(function(data){
    // $scope.groups = data;
  // }).error(function(data, status){
    // console.log(data, status);
  // });

  $scope.testMsg = [{
    msg: '============test========='
  }];

  $scope.setMemReq = function(index){
    $scope.selectedIndex = index;
    var total_index = ($scope.groupCurrentPage - 1) * $scope.groupPageSize + index;
    // console.log("======================", total_index);
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

  $scope.testAlert = function(msg) {
    // $("#example").popover();
    alert(msg);
  };
  
  $scope.checkData = function(field, data){

    if (field === 1) {
      if(data.length > 100){
        return "group name is too long";
      }
    } else if(field === 2) {
      var num = Number(data);
      if (!num || num > 180 || num < -180) {
        // console.log(num);
        return "Invalid Input";
      }
      
    } else if(field === 3) {
      var num = Number(data);
      if (!num || num > 90 || num < -90) {
        // console.log(num);
        return "Invalid Input";
      }
      
    } else if(field === 4) {
      
    }

  };

  $scope.update = function(index) {
    var total_index = ($scope.groupCurrentPage - 1) * $scope.groupPageSize + index;
    var data = {
      'username': 'maxzhx@gmail.com',
      'group_id': $scope.groups[total_index]._id,
      'name': $scope.groups[total_index].groupname,
      'g_lon': $scope.groups[total_index].group_location[0],
      'g_lat': $scope.groups[total_index].group_location[1],
      'location': $scope.groups[total_index].location,
      'introduction': $scope.groups[total_index].introduction
    };
    $http.post('group/update', data).success(function(data){
      console.log(data);
      groupDataFactory.getGroupData().then(function(res){
        console.log(res);
        $scope.groups = res;
        $scope.requests = $scope.groups[total_index].requests;
        $scope.members = $scope.groups[total_index].members;
      });
    }).error(function(data, status){
      console.log(data, status);
    });
  };
  
  $scope.newGroup = function() {
    var newGroup={
      'groupname':'Click to edit name',
      'group_location': [0,0],
      'location':'Click to edit location',
      'introduction':'Click to edit info'
    };
    $scope.groups.unshift(newGroup);
    // $scope.groupCurrentPage = 1;
  };

  $scope.acptRejReq = function(action, username) {
    var total_index = ($scope.groupCurrentPage - 1) * $scope.groupPageSize + $scope.selectedIndex;
    var url = '';
    switch (action)
    {
      case 1:
        url = '/group/accept';
        break;
      case 2:
        url = '/group/reject';
        break;
      case 3: 
        url = '/group/leave';
        break;

      default: 
        alert('Error!');
    }
    // console.log(url);
    $http.post(url, {
      'username': username,
      'group_id': $scope.groups[total_index]._id
    }).success(function(data){
      console.log(data);
      groupDataFactory.getGroupData().then(function(res){
        console.log(res);
        $scope.groups = res;
        $scope.requests = $scope.groups[total_index].requests;
        $scope.members = $scope.groups[total_index].members;
      });
    }).error(function(data, status){
      console.log(data, status);
    });
  };
}

/**
 * Event Controller
 */

angular
.module('RDash')
.controller('EventCtrl', ['$scope', '$http', 'eventDataFactory', EventCtrl]);

function EventCtrl($scope, $http, eventDataFactory) {

  $scope.eventCurrentPage = 1;
  $scope.eventPageSize = 4;
  $scope.events = [];

  eventDataFactory.getEventData().then(function(res){
    $scope.events = res;
  });

  $scope.pageChangeHandler = function(num) {
    console.log('going to page ' + num);
    $scope.eventCurrentPage = num;
  };

  $scope.newEvent = function() {
    var newEvent={
      'eventName':'Click to edit name',
      'eventLocation': [0,0],
      'location':'Click to edit Location',
      'startTime':new Date().toISOString().slice(0,10),
      'endTime':new Date().toISOString().slice(0,10),
      'eventInfo':'Click to edit info'
    };
    // console.log(newEvent.startTime);
    $scope.events.unshift(newEvent);
    // $scope.groupCurrentPage = 1;
  };

  $scope.testAlert = function(msg) {
    // $("#example").popover();
    alert(msg);
  };

  $scope.checkData = function(field, data){

    if (field === 1) {
      if(data.length > 100){
        return "event name is too long";
      }
    } else if(field === 2) {
      var num = Number(data);
      if (!num || num > 180 || num < -180) {
        // console.log(num);
        return "Invalid Input";
      }
      
    } else if(field === 3) {
      var num = Number(data);
      if (!num || num > 90 || num < -90) {
        // console.log(num);
        return "Invalid Input";
      }
      
    } else if(field === 4) {
      
    }
  };

  $scope.update = function(index) {
    var total_index = ($scope.eventCurrentPage - 1) * $scope.eventPageSize + index;
    var data = {
      // for testing
      'username': 'maxzhx@gmail.com',
      'event_id': $scope.events[total_index]._id,
      'event_name': $scope.events[total_index].eventName,
      'e_lon': $scope.events[total_index].eventLocation[0],
      'e_lat': $scope.events[total_index].eventLocation[1],
      'start_time': $scope.events[total_index].startTime,
      'end_time': $scope.events[total_index].endTime,
      'location': $scope.events[total_index].location,
      'eventInfo': $scope.events[total_index].eventInfo
    };
    $http.post('event/update', data).success(function(data){
      // console.log(data);
      eventDataFactory.getEventData().then(function(res){
        // console.log(res);
        $scope.events = res;
      });
    }).error(function(data, status){
      console.log(data, status);
    });
  };

}
