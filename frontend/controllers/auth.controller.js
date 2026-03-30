app.controller('AuthController', ['$scope', 'AuthService', '$location', '$window', function($scope, AuthService, $location, $window) {
    
    $scope.credentials = { username: '', password: '' };
    $scope.registerData = { username: '', password: '', roles: [] };
    $scope.errorMessage = '';
    $scope.successMessage = '';
    $scope.isAdminRole = false;
    $scope.showPassword = false;

    $scope.togglePassword = function() {
        $scope.showPassword = !$scope.showPassword;
    };

    $scope.resetStep = 1;
    $scope.resetData = { email: '', otp: '', newPassword: '' };

    $scope.forgotPassword = function() {
        $scope.resetStep = 1;
        $scope.resetData = { email: '', otp: '', newPassword: '' };
        var modalEl = document.getElementById('forgotPasswordModal');
        var modal = bootstrap.Modal.getOrCreateInstance(modalEl);
        modal.show();
    };

    $scope.sendOTP = function() {
        if (!$scope.resetData.email) {
            alert("Please enter your registered email address.");
            return;
        }
        AuthService.requestPasswordReset($scope.resetData.email).then(function(response) {
            $scope.resetStep = 2;
            alert("SECURITY: A unique OTP has been dispatched to " + $scope.resetData.email + ". It will expire in 10 minutes.");
        }).catch(function(error) {
            var msg = (error.data && error.data.message) ? error.data.message : "Failed to initiate reset. Please verify your email.";
            alert(msg);
        });
    };

    $scope.verifyAndReset = function() {
        if (!$scope.resetData.otp || !$scope.resetData.newPassword) {
            alert("Please provide both the OTP and your new secure password.");
            return;
        }
        AuthService.resetPassword($scope.resetData).then(function(response) {
            alert("SUCCESS: Your credentials have been updated. You may now login with your new password.");
            var modalEl = document.getElementById('forgotPasswordModal');
            var modal = bootstrap.Modal.getInstance(modalEl);
            if (modal) modal.hide();
        }).catch(function(error) {
            var msg = (error.data && error.data.message) ? error.data.message : "Verification failed. Check your OTP and try again.";
            alert(msg);
        });
    };

    $scope.login = function(expectAdmin) {
        $scope.errorMessage = '';
        AuthService.login($scope.credentials).then(function(response) {
            var isAdmin = AuthService.hasRole('ROLE_ADMIN');

            if (expectAdmin && !isAdmin) {
                // User tried to login to Police Portal with a Citizen account
                AuthService.logout();
                $scope.errorMessage = "Access Denied: You do not have Police Administrator privileges.";
                return;
            }

            $scope.successMessage = "Login successful! Redirecting...";
            
            // Redirect based on role
            if (isAdmin) {
                $location.path('/admin-dashboard');
            } else {
                $location.path('/user-dashboard');
            }
        }, function(error) {
            $scope.errorMessage = "Invalid username or password";
        });
    };

    $scope.registerData = {};

    $scope.registerCitizen = function() {
        $scope.isAdminRole = false;
        $scope.executeRegistration();
    };

    $scope.registerAdmin = function() {
        $scope.isAdminRole = true;
        $scope.executeRegistration();
    };

    $scope.executeRegistration = function() {
        if ($scope.isAdminRole) {
            $scope.registerData.roles = ['admin'];
        } else {
            $scope.registerData.roles = ['user'];
        }

        AuthService.register($scope.registerData).then(function(response) {
            $scope.successMessage = 'Registration successful! You can now login.';
            $scope.errorMessage = '';
            $scope.registerData = { username: '', password: '', roles: [] };
            setTimeout(function() {
                $location.path('/login');
                $scope.$apply();
            }, 2000);
        }).catch(function(error) {
            $scope.errorMessage = error.data.message || 'Registration failed';
            $scope.successMessage = '';
        });
    };
}]);

app.controller('NavController', ['$scope', 'AuthService', '$location', '$window', function($scope, AuthService, $location, $window) {
    $scope.isLoggedIn = function() {
        return AuthService.isLoggedIn();
    };

    $scope.hasRole = function(role) {
        return AuthService.hasRole(role);
    };

    $scope.logout = function() {
        AuthService.logout();
        $window.location.href = '#!/login';
    };
}]);
