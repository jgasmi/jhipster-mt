'use strict';

angular.module('mtApp')
    .controller('TenantController', function ($scope, Tenant, DbType) {
        $scope.tenants = [];
        $scope.dbtypes = DbType.query();
        $scope.loadAll = function() {
            Tenant.query(function(result) {
               $scope.tenants = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Tenant.update($scope.tenant,
                function () {
                    $scope.loadAll();
                    $('#saveTenantModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Tenant.get({id: id}, function(result) {
                $scope.tenant = result;
                $('#saveTenantModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Tenant.get({id: id}, function(result) {
                $scope.tenant = result;
                $('#deleteTenantConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Tenant.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteTenantConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.tenant = {tenantName: null, dbHost: null, dbPort: null, dbName: null, dbUserName: null, dbPassword: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
