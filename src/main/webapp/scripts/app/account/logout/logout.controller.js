'use strict';

angular.module('mtApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
