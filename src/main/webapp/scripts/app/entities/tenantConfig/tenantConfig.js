'use strict';

angular.module('mtApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('tenantConfig', {
                parent: 'entity',
                url: '/tenantConfig',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'mtApp.tenantConfig.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tenantConfig/tenantConfigs.html',
                        controller: 'TenantConfigController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tenantConfig');
                        return $translate.refresh();
                    }]
                }
            })
            .state('tenantConfigDetail', {
                parent: 'entity',
                url: '/tenantConfig/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'mtApp.tenantConfig.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tenantConfig/tenantConfig-detail.html',
                        controller: 'TenantConfigDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tenantConfig');
                        return $translate.refresh();
                    }]
                }
            });
    });
