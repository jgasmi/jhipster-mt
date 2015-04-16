'use strict';

angular.module('App')
    .controller('QueueController', function ($scope, Queue) {
        $scope.queues = [];
        $scope.loadAll = function() {
            Queue.query(function(result) {
               $scope.queues = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Queue.update($scope.queue,
                function () {
                    $scope.loadAll();
                    $('#saveQueueModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Queue.get({id: id}, function(result) {
                $scope.queue = result;
                $('#saveQueueModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Queue.get({id: id}, function(result) {
                $scope.queue = result;
                $('#deleteQueueConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Queue.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteQueueConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.queue = {name: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
