'use strict';

angular.module('mtApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


