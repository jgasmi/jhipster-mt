'use strict';

angular.module('mtApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('dbType', {
                parent: 'entity',
                url: '/dbType',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'mtApp.dbType.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/dbType/dbTypes.html',
                        controller: 'DbTypeController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('dbType');
                        return $translate.refresh();
                    }]
                }
            })
            .state('dbTypeDetail', {
                parent: 'entity',
                url: '/dbType/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'mtApp.dbType.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/dbType/dbType-detail.html',
                        controller: 'DbTypeDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('dbType');
                        return $translate.refresh();
                    }]
                }
            });
    });
