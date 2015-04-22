'use strict';

angular.module('mtApp')
    .controller('DbTypeDetailController', function ($scope, $stateParams, DbType) {
        $scope.dbType = {};
        $scope.load = function (id) {
            DbType.get({id: id}, function(result) {
              $scope.dbType = result;
            });
        };
        $scope.load($stateParams.id);
    });
