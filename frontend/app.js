var app = angular.module('firApp', ['ngRoute']);

// Constants
app.constant('API_URL', 'http://localhost:8080/api');

// Routing Configuration
app.config(['$routeProvider', function($routeProvider) {
    $routeProvider
        .when('/', {
            templateUrl: 'views/home.html?v=8'
        })
        .when('/login', {
            templateUrl: 'views/login.html?v=8',
            controller: 'AuthController'
        })
        .when('/register', {
            templateUrl: 'views/register.html?v=8',
            controller: 'AuthController'
        })
        .when('/user-dashboard', {
            templateUrl: 'views/user-dashboard.html?v=8',
            controller: 'UserController',
            resolve: { auth: authGuard }
        })
        .when('/admin-dashboard', {
            templateUrl: 'views/admin-dashboard.html?v=8',
            controller: 'AdminController',
            resolve: { auth: authGuard }
        })
        .when('/map', {
            templateUrl: 'views/map.html?v=8',
            controller: 'MapController',
            resolve: { auth: authGuard }
        })
        .otherwise({
            redirectTo: '/'
        });

    function authGuard($q, $location, AuthService) {
        if (AuthService.isLoggedIn()) {
            // If user tries to access admin-dashboard but isn't an admin
            if ($location.path() === '/admin-dashboard' && !AuthService.hasRole('ROLE_ADMIN')) {
                $location.path('/user-dashboard');
                return $q.reject('Unauthorized');
            }
            return $q.resolve();
        } else {
            $location.path('/login');
            return $q.reject('Not Authenticated');
        }
    }
}]);

// HTTP Interceptor to attach JWT token
app.factory('AuthInterceptor', ['$window', function($window) {
    return {
        request: function(config) {
            var token = $window.localStorage.getItem('jwtToken');
            // Only add token if it exists AND the request is NOT to an auth endpoint
            if (token && config.url.indexOf('/api/auth/') === -1) {
                config.headers.Authorization = 'Bearer ' + token;
            }
            return config;
        }
    };
}]);

app.config(['$httpProvider', function($httpProvider) {
    $httpProvider.interceptors.push('AuthInterceptor');
}]);
