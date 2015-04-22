'use strict';

angular.module('mtApp')
    .controller('UserDetailController', function ($scope, $stateParams, User, Tenant) {
        $scope.user = {};
        $scope.load = function (id) {
            User.get({id: id}, function(result) {
              $scope.user = result;
            });
        };
        $scope.load($stateParams.id);
    });
