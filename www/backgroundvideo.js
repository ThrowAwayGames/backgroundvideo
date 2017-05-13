var cordova = require('cordova');

var backgroundvideo = {
    start : function(filename, camera, x, y, w, h, rw, rh, shouldRecordAudio, successFunction, errorFunction) {
    	camera = camera || 'back';
        cordova.exec(successFunction, errorFunction, "backgroundvideo","start", [filename, camera, String(x), String(y), String(w), String(h), String(rw), String(rh), shouldRecordAudio]);
    },
    stop : function(successFunction, errorFunction) {
        cordova.exec(successFunction, errorFunction, "backgroundvideo","stop", []);
    }
};

module.exports = backgroundvideo;
