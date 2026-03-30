app.controller('UserController', ['$scope', 'FIRService', 'SOSService', 'AuthService', function($scope, FIRService, SOSService, AuthService) {
    
    $scope.firs = [];
    $scope.loading = true;
    $scope.fir = { description: '', location: '', category: '' };
    $scope.successMessage = '';
    $scope.errorMessage = '';
    $scope.systemAlerts = [];

    // SOS / Broadcast Listener
    SOSService.connect(function(payload) {
        $scope.$apply(function() {
            if (payload.isAdminBroadcast) {
                // Add to visible alerts
                $scope.systemAlerts.unshift(payload);
                console.log("CRITICAL SYSTEM BROADCAST:", payload);
                
                // Sound a high-priority chime
                var audio = new Audio('https://assets.mixkit.co/active_storage/sfx/2869/2869-preview.mp3');
                audio.play().catch(e => console.log("Audio play blocked"));
            }
        });
    });

    // SOS Emergency Trigger
    $scope.triggerEmergency = function() {
        if (!navigator.geolocation) {
            alert("Error: Geolocation is not supported by your device. Please contact emergency services manually.");
            return;
        }

        if (confirm("WARNING: You are about to trigger an Emergency SOS. This will broadcast your LIVE GPS location to the Police Command Center. Proceed?")) {
            navigator.geolocation.getCurrentPosition(function(position) {
                var user = AuthService.getUser();
                var sosData = {
                    senderEmail: user.username,
                    senderName: user.name || user.username,
                    latitude: position.coords.latitude,
                    longitude: position.coords.longitude,
                    message: "CITIZEN IN DISTRESS: LIVE GPS POSITION BROADCAST"
                };
                SOSService.triggerSOS(sosData);
                alert("EMERGENCY SIGNAL BROADCASTED! \nPolice response units have been notified of your precise location. Stay safe.");
            }, function(error) {
                alert("Failed to capture location. Broadcast sent with location error.");
            });
        }
    };

    $scope.loadFIRs = function() {
        $scope.loading = true;
        FIRService.getAllFIRs().then(function(response) {
            $scope.firs = response.data;
            $scope.loading = false;
        }).catch(function(error) {
            $scope.errorMessage = 'Failed to load FIRs';
            $scope.loading = false;
        });
    };

    $scope.submitFIR = function() {
        FIRService.createFIR($scope.fir).then(function(response) {
            $scope.successMessage = 'FIR submitted successfully! Transaction Hash: ' + response.data.blockchainTxHash;
            $scope.fir = { description: '', location: '', category: '' };
            $scope.loadFIRs();
            // hide success after 5s
            setTimeout(function() {
                $scope.successMessage = '';
                $scope.$apply();
            }, 5000);
        }).catch(function(error) {
            $scope.errorMessage = 'Error submitting FIR';
            setTimeout(function() {
                $scope.errorMessage = '';
                $scope.$apply();
            }, 3000);
        });
    };

    $scope.getStatusClass = function(status) {
        switch(status) {
            case 'OPEN': return 'status-open';
            case 'CLOSED': return 'status-closed';
            case 'IN_PROGRESS': return 'status-inprogress';
            default: return 'bg-secondary text-white';
        }
    };

    $scope.loadFIRs();

    // Features: Evidence Handling
    $scope.handleFileUpload = function(element, fir) {
        var file = element.files[0];
        if (!file) return;

        FIRService.uploadEvidence(fir.id, file).then(function(response) {
            alert("Evidence uploaded successfully!");
            $scope.loadEvidence(fir);
        }).catch(function(error) {
            alert("Error uploading evidence: " + (error.data || "Unknown error"));
        });
    };

    $scope.loadEvidence = function(fir) {
        FIRService.getEvidence(fir.id).then(function(response) {
            fir.evidence = response.data;
        });
    };

    $scope.getDownloadUrl = function(ev) {
        return FIRService.downloadEvidenceUrl(ev.id);
    };

    // Features: Open Detailed Modal view
    $scope.viewDetails = function(fir) {
        $scope.selectedFIR = fir;
        $scope.loadEvidence(fir);
        // Simple bootstrap modal trigger using vanilla JS fallback if jQuery isn't present
        var modal = new bootstrap.Modal(document.getElementById('caseDetailsModal'));
        modal.show();
    };
}]);
