'use strict';

angular.module('mtApp')
    .controller('CampaignDetailController', function ($scope, $stateParams, Campaign) {
        $scope.campaign = {};
        $scope.load = function (id) {
            Campaign.get({id: id}, function(result) {
              $scope.campaign = result;
            });
        };
        $scope.load($stateParams.id);
    });
