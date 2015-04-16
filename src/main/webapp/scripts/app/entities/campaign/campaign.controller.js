'use strict';

angular.module('mtApp')
    .controller('CampaignController', function ($scope, Campaign) {
        $scope.campaigns = [];
        $scope.loadAll = function() {
            Campaign.query(function(result) {
               $scope.campaigns = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Campaign.update($scope.campaign,
                function () {
                    $scope.loadAll();
                    $('#saveCampaignModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Campaign.get({id: id}, function(result) {
                $scope.campaign = result;
                $('#saveCampaignModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Campaign.get({id: id}, function(result) {
                $scope.campaign = result;
                $('#deleteCampaignConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Campaign.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteCampaignConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.campaign = {action: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
