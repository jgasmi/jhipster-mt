'use strict';

angular.module('mtApp')
    .controller('TenantConfigController', function ($scope, TenantConfig) {
        $scope.tenantConfigs = [];
        $scope.loadAll = function() {
            TenantConfig.query(function(result) {
               $scope.tenantConfigs = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            TenantConfig.update($scope.tenantConfig,
                function () {
                    $scope.loadAll();
                    $('#saveTenantConfigModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            TenantConfig.get({id: id}, function(result) {
                $scope.tenantConfig = result;
                $('#saveTenantConfigModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            TenantConfig.get({id: id}, function(result) {
                $scope.tenantConfig = result;
                $('#deleteTenantConfigConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            TenantConfig.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteTenantConfigConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.tenantConfig = {url: null, username: null, password: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
