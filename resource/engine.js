
window.less.environment.clearFileManagers();
//file manager should be the first one
window.less.environment.addFileManager(new FileManager());
window.less.environment.addFileManager(new HttpManager());

/**
 * 
 * @param source
 * @param path
 * @param options
 * @param callbackName (UUID)
 */
function compile(source, path, options, callbackName){
	
	var compress = options.compress;
	// because less deprecated their own internal compressor, so we use the yui compressor port
	delete options.compress;
	
	// call the less "compile" function via "render"
	// less.render(lessContent, options, callback)
	window.less.render(
		source,
		extend({
			filename: path
		}, options),
		function(err, output){
			if(err){
				//  console.error(JSON.stringify(err));
				renderCallback(callbackName, err); // renderCallback comes from LessCompilerV8.createCallback()
			}else{
				var css = output.css;
				if(compress){
					css = cssmin(css);
				}
				renderCallback(callbackName, null, css); // renderCallback comes from LessCompilerV8.createCallback()
			}
		}
	);
}