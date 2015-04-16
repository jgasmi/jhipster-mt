'use strict';

angular.module('App')
    .config(function ($stateProvider) {
        $stateProvider
            .state('queue', {
                parent: 'entity',
                url: '/queue',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'App.queue.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/queue/queues.html',
                        controller: 'QueueController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('queue');
                        return $translate.refresh();
                    }]
                }
            })
            .state('queueDetail', {
                parent: 'entity',
                url: '/queue/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'App.queue.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/queue/queue-detail.html',
                        controller: 'QueueDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('queue');
                        return $translate.refresh();
                    }]
                }
            });
    });
