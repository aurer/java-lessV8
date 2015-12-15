
var FileManager = function() {
};

FileManager.prototype = new (window.less.AbstractFileManager)();

FileManager.prototype.supports = function(filename, currentDirectory, options, environment) {
	return true;
};

FileManager.prototype.loadFile = function(filename, currentDirectory, options, environment, callback) {
	
	var fullFilename, paths, filenamesTried = [], isAbsoluteFilename = this.isPathAbsolute(filename) , data;
	options = options || {};

	paths = isAbsoluteFilename ? [""] : [currentDirectory];
	if (options.paths) {
		paths.push.apply(paths, options.paths);
	}
	if (!isAbsoluteFilename && paths.indexOf('.') === -1) {
		paths.push('.');
	}
	
	var err, result;
	for (var i = 0; i < paths.length; i++) {
		try {
			fullFilename = filename;
			if (paths[i]) {
				fullFilename = paths[i] + fullFilename;
			}
			filenamesTried.push(fullFilename);
			if(fs.exists(fullFilename)){
				break;
			}else{
				throw new Error('File not exists');
			}
		} catch (e) {
			fullFilename = null;
		}
	}

	if (!fullFilename) {
		err = { type: 'File', message: "'" + filename + "' wasn't found. Tried - " + filenamesTried.join(",") };
		result = { error: err };
	} else {
		data = fs.readFile(fullFilename);
		result = { contents: data, filename: fullFilename};
	}
	
	callback(err, result);
};

window.less.environment.clearFileManagers();
window.less.environment.addFileManager(new FileManager());

/**
 * 
 * @param source
 * @param path
 * @param compress
 * @returns
 */
function compile(source, path, compress, callbackName){
	window.less.render(
		source,
		{
			filename: path,
			compress: !!compress
		},
		function(err, output){
			if(err){
				//console.error(JSON.stringify(err));
				renderCallback(callbackName, err)
			}else{
				renderCallback(callbackName, null, output.css);
			}
		}
	);
}
