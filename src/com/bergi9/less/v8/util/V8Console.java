package com.bergi9.less.v8.util;

import org.apache.log4j.Logger;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;


public class V8Console
{
	private static final Logger logger = Logger.getLogger(V8Console.class);
	
	public void log(final String message)
	{
		logger.info(message);
	}
	
	public void debug(final String message)
	{
		logger.debug(message);
	}
	
	public void warn(final String message)
	{
		logger.warn(message);
	}
	
	public void error(final String message)
	{
		logger.error(message);
	}
	
	public static void register(V8 v8)
	{
		V8Console console = new V8Console();
		V8Object v8Console = new V8Object(v8);
		v8.add("console", v8Console);
		v8Console.registerJavaMethod(console, "log", "log", new Class<?>[] { String.class });
		v8Console.registerJavaMethod(console, "debug", "debug", new Class<?>[] { String.class });
		v8Console.registerJavaMethod(console, "warn", "warn", new Class<?>[] { String.class });
		v8Console.registerJavaMethod(console, "error", "error", new Class<?>[] { String.class });
		v8Console.release();
	}
}
