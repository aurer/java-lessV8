package com.bergi9.less.v8;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.bergi9.less.v8.io.FileSource;
import com.bergi9.less.v8.io.HttpSource;
import com.bergi9.less.v8.io.Result;
import com.bergi9.less.v8.io.Source;
import com.bergi9.less.v8.util.LessUtils;
import com.bergi9.less.v8.util.V8Console;
import com.eclipsesource.v8.JavaVoidCallback;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;


public class LessCompilerV8 implements LessCompiler
{
	private static Logger logger = Logger.getLogger(LessCompilerV8.class);

	private V8 runtime = null;
	
	private Map<String, LessCompileCallback> callbacks;
	private Map<String, Result> results;
	
	private String tempDir = null;
	
	/**
	 * Constructor with default configuration
	 */
	public LessCompilerV8()
	{
		this(null);
	}
	
	/**
	 * Constructor to define specific directory for the V8-library
	 * @param tempDir
	 */
	public LessCompilerV8(String tempDir)
	{
		this.tempDir = tempDir;
		callbacks = new HashMap<String, LessCompileCallback>();
		results = new HashMap<String, Result>();
	}
	
	/**
	 * Initializes the V8 runtime
	 */
	@Override
	public void init()
	{
		runtime = createRuntime();
	}
	
	/**
	 * read a javascript file from inside of .jar file
	 * @param jsfile
	 * @return
	 */
	private String getJSFile(String jsfile)
	{
		// reading JS files from resources
		String result = "";
		ClassLoader classLoader = getClass().getClassLoader();
		try {
			result = LessUtils.streamToString(classLoader.getResourceAsStream(jsfile));
		}
		catch (IOException e) {
			logger.error("Couldn't find javascript file in '" + jsfile + "'", e);
		}
		return result;
	}
	
	/**
	 * Disables some rows in the less code to prevent incompatibility
	 * @param less
	 * @return
	 */
	private String disableLessFunctions(String less)
	{
		less = less.replace("addDataAttr(options, browser.currentScript(window));", "");
		less = less.replace("less.registerStylesheetsImmediately();", "");
		less = less.replaceFirst("if\\s\\(compress\\)\\s\\{\\s+logger.+\\s+\\}", "");
		return less;
	}
	
	/**
	 * creates the V8 runtime and register all requires bindings and javascript-code
	 * @return
	 */
	private V8 createRuntime()
	{
		V8 runtime = V8.createV8Runtime(null, tempDir);
		
		V8Console.register(runtime);
		registerIOSystems(runtime);
		
		runtime.executeVoidScript(getJSFile("env.js"));
		
		runtime.executeVoidScript(disableLessFunctions(getJSFile("less-2.5.3.js")));
		
		runtime.executeVoidScript(getJSFile("cssmin.js").replace("module.exports = cssmin;", ""));
		
		runtime.executeVoidScript(getJSFile("fileManager.js"));
		runtime.executeVoidScript(getJSFile("httpManager.js"));
		
		runtime.executeVoidScript(getJSFile("engine.js"));
		
		createCallback(runtime);
		
		runtime.getLocker().release();
		
		return runtime;
	}
	
	/**
	 * binds IO systems into the V8
	 * @param v8
	 */
	private void registerIOSystems(V8 v8)
	{
		FileSource fileSource = new FileSource("");
		
		V8Object v8FileSystem = new V8Object(v8);
		v8.add("fs", v8FileSystem);
		v8FileSystem.registerJavaMethod(fileSource, "exists", "exists", new Class<?>[]{ String.class });
		v8FileSystem.registerJavaMethod(fileSource, "readFile", "readFile", new Class<?>[]{ String.class });
		v8FileSystem.release();
		
		HttpSource httpSource = new HttpSource("");
		
		V8Object v8Http = new V8Object(v8);
		v8.add("http", v8Http);
		v8Http.registerJavaMethod(httpSource, "isValid", "isValid", new Class<?>[] { String.class });
		v8Http.registerJavaMethod(httpSource, "readHttp", "readHttp", new Class<?>[] { String.class });
		v8Http.release();
	}
	
	/**
	 * register a static callback to V8 for the javascript-function of "renderCallback"
	 * @param v8
	 */
	private void createCallback(V8 v8)
	{
		// this is a static callback registered to v8
		// it will always get called if less has been compiled
		JavaVoidCallback v8Callback = new JavaVoidCallback()
		{
			@Override
			public void invoke(V8Object receiver, V8Array parameters)
			{
				LessException exception = null;
				String css = null;
				
				String uuid = parameters.getString(0);
				
				// if it has only 2 the uuid and error then it has an exception
				if(parameters.length() == 2)
				{
					V8Object error = parameters.getObject(1);
					exception = new LessException(error);
					error.release();
				}
				// if it has uuid, empty error and the css string, then it has compiled successfully
				else if(parameters.length() == 3)
				{
					css = parameters.getString(2);
				}
				
				parameters.release();
				
				// using uuid like pointers to get the correct callback and result
				LessCompileCallback callback = callbacks.remove(uuid);
				Result result = results.remove(uuid);
				
				if(exception != null)
				{
					// fires the defined callback as error
					callback.onError(exception);
				}
				else
				{
					try
					{
						// css will written to defined Result class
						result.write(css);
					}
					catch (IOException e)
					{
						// fires the defined callback as error
						callback.onError(new LessException(e));
						return;
					}
					// fires the defined callback as successful
					callback.onSuccess();
				}
			}
		};
		
		// register a static callback function into v8
		v8.registerJavaMethod(v8Callback, "renderCallback");
	}

	/**
	 * compiles less with default options
	 * it will only executed synchronously
	 */
	@Override
	public void compile(Source source, Result result) throws LessException
	{
		compile(source, result, new LessCompileOptions());
	}
	
	/**
	 * compiles less with options
	 * with options it can be called asynchronously
	 */
	@Override
	public void compile(Source source, Result result, LessCompileOptions options) throws LessException
	{
		if(runtime == null)
		{
			throw new NullPointerException("V8 not initialized. Please use .init() before calling .compile()");
		}
		
		if(options.isAsync())
		{
			if(options.getAsyncCallback() == null)
			{
				throw new IllegalArgumentException("AsyncCallback not defined");
			}
			
			compileAsync(source, result, options);
			return;
		}
		
		compileSync(source, result, options);
	}
	
	/**
	 * compiles synchronously by wrapping compileAsync with CountDownLatch
	 * @param source
	 * @param result
	 * @param options
	 * @throws LessException
	 */
	private void compileSync(Source source, Result result, LessCompileOptions options) throws LessException
	{
		// using CountDownLatch to make async to sync
		final CountDownLatch doneSignal = new CountDownLatch(1);
		
		// because the lessCompileCallback is a closure, so we need to declare the outerscoped variable as final variables.
		// to able adding exceptions we use a list
		final List<LessException> exception = new ArrayList<LessException>(1);
		
		LessCompileCallback callback = new LessCompileCallback()
		{
			@Override
			public void onError(LessException e)
			{
				exception.add(e);
				
				// on .countDown() will release the blocking of .await()
				doneSignal.countDown();
			}
			
			@Override
			public void onSuccess()
			{
				doneSignal.countDown();
			}
		};
		
		// since less nature is async, so add an async callback before calling compileAsync
		options.setAsyncCallback(callback);
		
		compileAsync(source, result, options);
		
		try
		{
			// get blocked until .countDown() got called
			doneSignal.await();
		}
		catch (InterruptedException e)
		{
			exception.add(new LessException(e));
		}
		
		if(exception.size() > 0)
		{
			throw exception.get(0);
		}
	}
	
	private void compileAsync(Source source, Result result, LessCompileOptions options)
	{
		String sourceLess = null;
		try
		{
			sourceLess = source.read();
		}
		catch (IOException e)
		{
			options.getAsyncCallback().onError(new LessException(e));
			return;
		}
		
		// get lock to the current thread
		runtime.getLocker().acquire();
		
		// uuid is used like a pointer to point the callback and result for get the correct callback if it has been compiled.
		// all callbacks will be called by a static callback (see on createCallback())
		String uuid = UUID.randomUUID().toString();
		
		callbacks.put(uuid, options.getAsyncCallback());
		results.put(uuid, result);
		
		String path = source.getPath();
		
		// construct the options java-object to v8-object
		V8Object v8lessOptions = options.toV8Object(runtime);
		
		// creates arguments before call compile
		V8Array arguments = new V8Array(runtime).push(sourceLess).push(path).push(v8lessOptions).push(uuid);

		v8lessOptions.release();
		
		runtime.executeVoidFunction("compile", arguments);
		arguments.release();
		
		runtime.getLocker().release();
	}
	
	/**
	 * releases the V8 runtime
	 */
	@Override
	public void destroy()
	{
		if(runtime != null){
			runtime.getLocker().acquire();
			runtime.release();
		}
	}
}
