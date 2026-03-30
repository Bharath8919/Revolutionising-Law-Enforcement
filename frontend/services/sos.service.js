app.factory('SOSService', ['$rootScope', function($rootScope) {
    var stompClient = null;
    var service = {};

    service.connect = function(callback) {
        var socket = new SockJS('http://localhost:8080/ws-sos');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function(frame) {
            console.log('Connected to SOS WebSocket: ' + frame);
            stompClient.subscribe('/topic/sos-alerts', function(alert) {
                callback(JSON.parse(alert.body));
            });
        });
    };

    service.triggerSOS = function(sosData) {
        if (stompClient && stompClient.connected) {
            stompClient.send("/app/sos-trigger", {}, JSON.stringify(sosData));
        } else {
            console.error("WebSocket not connected. Attempting to reconnect...");
            service.connect(function() {
                 stompClient.send("/app/sos-trigger", {}, JSON.stringify(sosData));
            });
        }
    };

    return service;
}]);
