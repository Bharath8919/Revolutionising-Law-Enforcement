app.factory('AuthService', ['$http', '$window', 'API_URL', function($http, $window, API_URL) {
    var authService = {};

    authService.login = function(credentials) {
        return $http.post(API_URL + '/auth/signin', credentials)
            .then(function(response) {
                if (response.data.token) {
                    $window.localStorage.setItem('jwtToken', response.data.token);
                    $window.localStorage.setItem('userRoles', JSON.stringify(response.data.roles));
                    $window.localStorage.setItem('username', response.data.username);
                }
                return response.data;
            });
    };

    authService.register = function(user) {
        return $http.post(API_URL + '/auth/signup', user);
    };

    authService.logout = function() {
        $window.localStorage.removeItem('jwtToken');
        $window.localStorage.removeItem('userRoles');
        $window.localStorage.removeItem('username');
    };

    authService.isLoggedIn = function() {
        return $window.localStorage.getItem('jwtToken') !== null;
    };

    authService.hasRole = function(role) {
        var rolesStr = $window.localStorage.getItem('userRoles');
        if (rolesStr) {
            var roles = JSON.parse(rolesStr);
            return roles.indexOf(role) !== -1;
        }
        return false;
    };

    authService.getUsername = function() {
        return $window.localStorage.getItem('username');
    };

    authService.requestPasswordReset = function(email) {
        return $http.post(API_URL + '/auth/forgot-password', { email: email });
    };

    authService.resetPassword = function(resetData) {
        return $http.post(API_URL + '/auth/reset-password', resetData);
    };

    return authService;
}]);
