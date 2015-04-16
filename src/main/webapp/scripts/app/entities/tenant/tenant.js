'use strict';

angular.module('mtApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('tenant', {
                parent: 'entity',
                url: '/tenant',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'mtApp.tenant.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tenant/tenants.html',
                        controller: 'TenantController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tenant');
                        return $translate.refresh();
                    }]
                }
            })
            .state('tenantDetail', {
                parent: 'entity',
                url: '/tenant/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'mtApp.tenant.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tenant/tenant-detail.html',
                        controller: 'TenantDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tenant');
                        return $translate.refresh();
                    }]
                }
            });
    });
