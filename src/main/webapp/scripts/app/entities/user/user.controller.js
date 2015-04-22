'use strict';

angular.module('mtApp')
    .controller('UserController', function ($scope, User, Tenant) {
        $scope.users = [];
        $scope.tenants = Tenant.query();
        $scope.loadAll = function() {
            User.query(function(result) {
               $scope.users = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            User.update($scope.user,
                function () {
                    $scope.loadAll();
                    $('#saveUserModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            User.get({id: id}, function(result) {
                $scope.user = result;
                $('#saveUserModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            User.get({id: id}, function(result) {
                $scope.user = result;
                $('#deleteUserConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            User.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteUserConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.user = {id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
