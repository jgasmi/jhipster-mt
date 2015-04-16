'use strict';

angular.module('mtApp')
    .controller('ActionDetailController', function ($scope, $stateParams, Action, Campaign) {
        $scope.action = {};
        $scope.load = function (id) {
            Action.get({id: id}, function(result) {
              $scope.action = result;
            });
        };
        $scope.load($stateParams.id);
    });
