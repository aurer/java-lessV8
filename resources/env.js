// create browser environment
var global = this;
var window = {};
var document = {};
window.document = document;
window.location = {};
window.location.protocol='file';
window.location.hostname='0.0.0.0';
