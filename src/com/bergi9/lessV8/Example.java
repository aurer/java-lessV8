package de.otris.lessV8;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import de.otris.lessV8.Utils.V8FileSystem;


public class Example
{
	private static final Logger logger = Logger.getLogger(Example.class);
	
	public static void main(String[] args) throws IOException
	{
		BasicConfigurator.configure();
		
		LessCompiler c = new LessCompilerV8();
		
		try
		{
			withoutImports(c);
			withImports(c);
			withImportsBootstrap(c);
		}
		catch (LessException e)
		{
			e.printStackTrace();
		}
		
		c.release();
	}
	
	private static void withoutImports(LessCompiler c) throws LessException
	{
		String result = c.compileLess(".foo { width: (1+1)px }");
		logger.debug(result);
	}
	
	private static void withImports(LessCompiler c) throws LessException, IOException
	{
		V8FileSystem fs = new V8FileSystem();
		String source = fs.readFile("D:\\less\\main.less");
		String result = c.compileLess(source, "D:\\less\\main.less");
		logger.debug(result);
	}

	private static void withImportsBootstrap(LessCompiler c) throws LessException, IOException
	{
		V8FileSystem fs = new V8FileSystem();
		String source = fs.readFile("D:\\bootstrap-3.3.6\\less\\bootstrap.less");
		String result = c.compileLess(source, "D:\\bootstrap-3.3.6\\less\\bootstrap.less", true);
		logger.debug(result);
	}
}
