'use strict';

angular.module('App')
    .controller('QueueDetailController', function ($scope, $stateParams, Queue) {
        $scope.queue = {};
        $scope.load = function (id) {
            Queue.get({id: id}, function(result) {
              $scope.queue = result;
            });
        };
        $scope.load($stateParams.id);
    });
