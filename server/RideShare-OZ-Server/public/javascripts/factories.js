'use strict';

/**
 * Route configuration for the RDash module.
 */
angular.module('RDash').factory('groupDataFactory', ['$http', '$q', '$timeout', function($http, $q, $timeout) {
  return {
    getGroupData: function() {
      var MAX_REQUESTS = 100,
          counter = 1,
          results = $q.defer();

      var request = function() {
        $http({method: 'GET', url: 'group/getall'})
          .success(function(response) {
            results.resolve(response);
          })
        .error(function() {
          if (counter < MAX_REQUESTS) {
            // request();
            $timeout(request, 800);
            counter++;
          } else {
            results.reject("Could not load after multiple tries");
          }
        });
      };
      request();
      return results.promise;
      // return "Hello, World!"
    }
  };
}]);
