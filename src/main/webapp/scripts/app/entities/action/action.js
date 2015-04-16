'use strict';

angular.module('mtApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('action', {
                parent: 'entity',
                url: '/action',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'mtApp.action.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/action/actions.html',
                        controller: 'ActionController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('action');
                        return $translate.refresh();
                    }]
                }
            })
            .state('actionDetail', {
                parent: 'entity',
                url: '/action/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'mtApp.action.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/action/action-detail.html',
                        controller: 'ActionDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('action');
                        return $translate.refresh();
                    }]
                }
            });
    });
