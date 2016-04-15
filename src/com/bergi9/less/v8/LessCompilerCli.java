package com.bergi9.less.v8;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import com.bergi9.less.v8.io.FileResult;
import com.bergi9.less.v8.io.FileSource;
import com.bergi9.less.v8.io.HttpSource;
import com.bergi9.less.v8.io.Source;
import com.bergi9.less.v8.io.StringResult;

public class LessCompilerCli
{
	private static String sourceFile = null;
	private static String outputFile = null;
	private static LessCompileOptions options = new LessCompileOptions();
	
	public static void main(String[] args)
	{
		if(args.length == 0)
		{
			System.out.println("-c | enables css-compressor");
			System.out.println("-s file | source file");
			System.out.println("-o file | output file (if not then it will print to StdOut)");
			return;
		}
		
		BasicConfigurator.configure();
		
		List<String> arguments = Arrays.asList(args);
		
		Iterator<String> itr = arguments.iterator();
		
		while (itr.hasNext())
		{
			String arg = itr.next();
			if("-c".equals(arg))
			{
				options.setCompress(true);
				continue;
			}
			
			if("-s".equals(arg))
			{
				if(itr.hasNext())
				{
					sourceFile = itr.next();
				}
				else
				{
					System.err.println("Invalid argument at " + arg);
					return;
				}
				continue;
			}
			
			if("-o".equals(arg))
			{
				if(itr.hasNext())
				{
					outputFile = itr.next();
				}
				else
				{
					System.err.println("Invalid argument at " + arg);
					return;
				}
				continue;
			}
			
			if("-g".equals(arg))
			{
				if(itr.hasNext())
				{
					String a = itr.next();
					String[] b = a.split(",");
					for(String c : b)
					{
						String[] d = c.split("=");
						
						if(d.length < 2)
						{
							System.err.println("Invalid argument at " + arg);
							return;
						}
						options.addGlobalVar(d[0], d[1]);
					}
				}
				else
				{
					System.err.println("Invalid argument at " + arg);
					return;
				}
				continue;
			}
			
			if("-m".equals(arg))
			{
				if(itr.hasNext())
				{
					String a = itr.next();
					String[] b = a.split(",");
					for(String c : b)
					{
						String[] d = c.split("=");
						
						if(d.length < 2)
						{
							System.err.println("Invalid argument at " + arg);
							return;
						}
						options.addModifyVar(d[0], d[1]);
					}
				}
				else
				{
					System.err.println("Invalid argument at " + arg);
					return;
				}
				continue;
			}
			
			if("-h".equals(arg))
			{
				System.out.println("-c | enables css-compressor");
				System.out.println("-s file or url | source file or url");
				System.out.println("-o file | output file (if not then it will print to StdOut)");
				System.out.println("-g \"variable=value,variable2=value2\" | add global variables, delimited by commas");
				System.out.println("-m \"variable=value,variable2=value2\" | add modify variables, delimited by commas");
				return;
			}
		}
		
		if(sourceFile == null)
		{
			System.err.println("Please insert a source with -s");
			return;
		}
		
		Source source = null;
		
		if(source == null && HttpSource.isValid(sourceFile))
		{
			source = new HttpSource(sourceFile);
		}
		
		if(source == null && FileSource.exists(sourceFile))
		{
			source = new FileSource(sourceFile);
		}
		
		if(source == null)
		{
			System.err.println("Source '" + sourceFile + "' not exist.");
			return;
		}
		
		StringResult result = null;
		try
		{
			LessCompiler compiler = new LessCompilerV8();
			compiler.init();
			
			result = new StringResult();
			
			compiler.compile(source, result, options);
			
			compiler.destroy();
		}
		catch (LessException e)
		{
			System.err.println(e.getMessage());
			return;
		}
		
		if(outputFile != null && result != null)
		{
			try
			{
				FileResult res = new FileResult(outputFile);
				res.write(result.getContent());
			}
			catch (IOException e)
			{
				System.err.println("Cannot write the output file");
				return;
			}
		}
		else
		{
			System.out.print(result.getContent());
		}
	}

}
