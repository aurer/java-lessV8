// create browser environment
var global = this;
var window = {};
var document = {};
window.document = document;
window.location = {};
window.location.protocol='file';
window.location.hostname='0.0.0.0';

// some utils

// path util from: https://gist.github.com/creationix/7435851
var path = {
	join: function() {
		var parts = [];
		for (var i = 0, l = arguments.length; i < l; i++) {
			parts = parts.concat(arguments[i].split("/"));
		}
		var newParts = [];
		for (i = 0, l = parts.length; i < l; i++) {
			var part = parts[i];
			if (!part || part === ".") continue;
			if (part === "..") newParts.pop();
			else newParts.push(part);
		}
		if (parts[0] === "") newParts.unshift("");
		return newParts.join("/") || (newParts.length ? "/" : ".");
	},

	dirname:function(path) {
		return join(path, "..");
	}
};