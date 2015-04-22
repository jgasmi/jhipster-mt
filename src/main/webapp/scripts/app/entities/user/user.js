'use strict';

angular.module('mtApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('user', {
                parent: 'entity',
                url: '/user',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'mtApp.user.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/user/users.html',
                        controller: 'UserController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('user');
                        return $translate.refresh();
                    }]
                }
            })
            .state('userDetail', {
                parent: 'entity',
                url: '/user/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'mtApp.user.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/user/user-detail.html',
                        controller: 'UserDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('user');
                        return $translate.refresh();
                    }]
                }
            });
    });
