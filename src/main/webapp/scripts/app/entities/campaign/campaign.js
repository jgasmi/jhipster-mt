'use strict';

angular.module('mtApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('campaign', {
                parent: 'entity',
                url: '/campaign',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'mtApp.campaign.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/campaign/campaigns.html',
                        controller: 'CampaignController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('campaign');
                        return $translate.refresh();
                    }]
                }
            })
            .state('campaignDetail', {
                parent: 'entity',
                url: '/campaign/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'mtApp.campaign.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/campaign/campaign-detail.html',
                        controller: 'CampaignDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('campaign');
                        return $translate.refresh();
                    }]
                }
            });
    });
