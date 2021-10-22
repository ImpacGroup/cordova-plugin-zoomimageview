

// Empty constructor
function Zoomimageview() {}


Zoomimageview.prototype.presentImage = function(info, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'Zoomimageview', 'presentImage', [info]);
}

Zoomimageview.install = function() {
    if (!window.plugins) {
        window.plugins = {};
    }
    window.plugins.zoomimageview = new Zoomimageview();
    return window.plugins.zoomimageview;
}
cordova.addConstructor(Zoomimageview.install);
