'use strict';

angular.module('mtApp')
    .controller('ActionController', function ($scope, Action, Campaign) {
        $scope.actions = [];
        $scope.campaigns = Campaign.query();
        $scope.loadAll = function() {
            Action.query(function(result) {
               $scope.actions = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Action.update($scope.action,
                function () {
                    $scope.loadAll();
                    $('#saveActionModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Action.get({id: id}, function(result) {
                $scope.action = result;
                $('#saveActionModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Action.get({id: id}, function(result) {
                $scope.action = result;
                $('#deleteActionConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Action.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteActionConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.action = {id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
