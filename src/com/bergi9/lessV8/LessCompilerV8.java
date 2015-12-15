package de.otris.lessV8;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import com.eclipsesource.v8.JavaVoidCallback;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;

import de.otris.lessV8.Utils.LessUtils;
import de.otris.lessV8.Utils.V8Console;
import de.otris.lessV8.Utils.V8FileSystem;


public class LessCompilerV8 implements LessCompiler
{

	private V8 runtime;
	
	private Map<String, LessCallback> callbacks;
	
	private String tempDir = null;
	
	public LessCompilerV8()
	{
		this(null);
	}
	
	public LessCompilerV8(String tempDir)
	{
		this.tempDir = tempDir;
		runtime = createRuntime();
		callbacks = new HashMap<String, LessCallback>();
	}
	
	private String getJSFile(String jsfile)
	{
		String result = "";
		ClassLoader classLoader = getClass().getClassLoader();
		try {
			result = LessUtils.streamToString(classLoader.getResourceAsStream(jsfile));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private String disableFunctions(String less)
	{
		less = less.replace("addDataAttr(options, browser.currentScript(window));", "");
		less = less.replace("less.registerStylesheetsImmediately();", "");
		less = less.replaceFirst("if\\s\\(compress\\)\\s\\{\\s+logger.+\\s+\\}", "");
		return less;
	}
	
	private V8 createRuntime()
	{
		V8 runtime = V8.createV8Runtime(null, tempDir);
		V8Console.register(runtime);
		V8FileSystem.register(runtime);
		runtime.executeVoidScript(getJSFile("env.js"));
		runtime.executeVoidScript(disableFunctions(getJSFile("less-2.5.3.js")));
		runtime.executeVoidScript(getJSFile("engine.js"));
		
		createCallback(runtime);
		
		runtime.getLocker().release();
		
		return runtime;
	}
	
	private void createCallback(V8 v8)
	{
		JavaVoidCallback v8Callback = new JavaVoidCallback()
		{
			@Override
			public void invoke(V8Object receiver, V8Array parameters)
			{
				LessException exception = null;
				String css = null;
				
				String uuid = parameters.getString(0);
				
				if(parameters.length() == 2)
				{
					V8Object error = parameters.getObject(1);
					exception = new LessException(error);
					error.release();
				}
				else if(parameters.length() == 3)
				{
					css = parameters.getString(2);
				}
				
				parameters.release();
				
				LessCallback callback = callbacks.remove(uuid);
				
				if(callback != null){
					if(exception != null)
					{
						callback.onLessCompiled(null, exception);
					}
					else
					{
						callback.onLessCompiled(css, null);
					}
				}
			}
		};
		
		v8.registerJavaMethod(v8Callback, "renderCallback");
	}

	@Override
	public String compileLess(String source) throws LessException
	{
		return compileLess(source, false);
	}

	@Override
	public String compileLess(String source, boolean compress) throws LessException
	{
		return compileLess(source, "", compress);
	}

	@Override
	public String compileLess(String source, String path) throws LessException
	{
		return compileLess(source, path, false);
	}

	@Override
	public String compileLess(String source, String path, boolean compress) throws LessException
	{
		final CountDownLatch doneSignal = new CountDownLatch(1);
		
		final List<String> result = new ArrayList<String>(1);
		
		final List<LessException> exception = new ArrayList<LessException>(1);
		
		LessCallback callback = new LessCallback()
		{
			@Override
			public void onLessCompiled(String css, LessException e)
			{
				if(e != null)
				{
					exception.add(e);
				}
				
				result.add(css);
				
				doneSignal.countDown();
			}
		};
		
		compileLessAsync(source, path, compress, callback);
		
		try
		{
			doneSignal.await();
		}
		catch (InterruptedException e)
		{
			exception.add(new LessException(e.getMessage(), e.getCause()));
		}
		
		if(exception.size() > 0)
		{
			throw exception.get(0);
		}
		
		return result.size() > 0 ? result.get(0) : null;
	}
	
	@Override
	public void compileLessAsync(String source, final LessCallback callback)
	{
		compileLessAsync(source, false, callback);
	}
	
	@Override
	public void compileLessAsync(String source, boolean compress, final LessCallback callback)
	{
		compileLessAsync(source, "", compress, callback);
	}
	
	@Override
	public void compileLessAsync(String source, String path, final LessCallback callback)
	{
		compileLessAsync(source, path, false, callback);
	}
	
	@Override
	public void compileLessAsync(String source, String path, boolean compress, final LessCallback callback)
	{
		String uuid = "_" + UUID.randomUUID().toString().replace("-", "");
		
		runtime.getLocker().acquire();
		
		callbacks.put(uuid, callback);
		
		V8Array arguments = new V8Array(runtime).push(source).push(path).push(compress).push(uuid);

		runtime.executeVoidFunction("compile", arguments);
		arguments.release();
		
		runtime.getLocker().release();
	}

	@Override
	public void release()
	{
		if(runtime != null){
			runtime.getLocker().acquire();
			runtime.release();
		}
	}
}
