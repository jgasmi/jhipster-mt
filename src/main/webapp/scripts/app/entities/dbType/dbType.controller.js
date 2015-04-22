'use strict';

angular.module('mtApp')
    .controller('DbTypeController', function ($scope, DbType) {
        $scope.dbTypes = [];
        $scope.loadAll = function() {
            DbType.query(function(result) {
               $scope.dbTypes = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            DbType.update($scope.dbType,
                function () {
                    $scope.loadAll();
                    $('#saveDbTypeModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            DbType.get({id: id}, function(result) {
                $scope.dbType = result;
                $('#saveDbTypeModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            DbType.get({id: id}, function(result) {
                $scope.dbType = result;
                $('#deleteDbTypeConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            DbType.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteDbTypeConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.dbType = {dbType: null, driver: null, url: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
