'use strict';

angular.module('mtApp')
    .controller('TenantDetailController', function ($scope, $stateParams, Tenant, DbType) {
        $scope.tenant = {};
        $scope.load = function (id) {
            Tenant.get({id: id}, function(result) {
              $scope.tenant = result;
            });
        };
        $scope.load($stateParams.id);
    });
