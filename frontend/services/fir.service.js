app.factory('FIRService', ['$http', 'API_URL', function($http, API_URL) {
    var firService = {};

    firService.getAllFIRs = function() {
        return $http.get(API_URL + '/firs');
    };

    firService.createFIR = function(fir) {
        return $http.post(API_URL + '/firs', fir);
    };

    firService.updateStatus = function(id, status) {
        return $http.put(API_URL + '/firs/' + id + '/status', status);
    };

    firService.updateFullFIR = function(fir) {
        return $http.put(API_URL + '/firs/' + fir.id, fir);
    };

    firService.getFIRById = function(id) {
        return $http.get(API_URL + '/firs/' + id);
    };

    firService.getStats = function() {
        return $http.get(API_URL + '/firs/stats');
    };

    firService.uploadEvidence = function(firId, file) {
        var fd = new FormData();
        fd.append('file', file);
        return $http.post(API_URL + '/evidence/upload/' + firId, fd, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        });
    };

    firService.getEvidence = function(firId) {
        return $http.get(API_URL + '/evidence/fir/' + firId);
    };

    firService.downloadEvidenceUrl = function(evidenceId) {
        return API_URL + '/evidence/download/' + evidenceId;
    };

    return firService;
}]);
