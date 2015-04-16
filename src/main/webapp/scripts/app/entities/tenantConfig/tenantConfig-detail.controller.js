'use strict';

angular.module('mtApp')
    .controller('TenantConfigDetailController', function ($scope, $stateParams, TenantConfig) {
        $scope.tenantConfig = {};
        $scope.load = function (id) {
            TenantConfig.get({id: id}, function(result) {
              $scope.tenantConfig = result;
            });
        };
        $scope.load($stateParams.id);
    });
