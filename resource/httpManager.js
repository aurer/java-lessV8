var HttpManager = function() {
};

HttpManager.prototype = new (window.less.AbstractFileManager)();

HttpManager.prototype.supports = function(filename, currentDirectory, options, environment) {
	return http.isValid(filename) || http.isValid(currentDirectory);
};

HttpManager.prototype.loadFile = function(filename, currentDirectory, options, environment, callback) {
	
	var urlStr = http.isValid(filename) ? filename : currentDirectory + (currentDirectory.match('/$')=='/' ? '' : '/') + filename;
	
	var err, result, data;
	
	try {
		
		data = http.readHttp(urlStr);
		
	} catch (e) {
		data = null;
	}
	
	if(!data) {
		err = { type: 'File', message: "'" + filename + "' wasn't found." };
		result = { error: err };
	} else {
		result = { contents: data, filename: urlStr };
	}
	
	callback(err, result);
};