package de.otris.lessV8;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import de.otris.lessV8.Utils.V8FileSystem;

public class LessCompilerCli
{
	private static boolean compress = false;
	private static String sourceFile = null;
	private static String outputFile = null;
	
	private static V8FileSystem fs = null;
	
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
				compress = true;
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
			
			if("-h".equals(arg))
			{
				System.out.println("-c | enables css-compressor");
				System.out.println("-s file | source file");
				System.out.println("-o file | output file (if not then it will print to StdOut)");
				return;
			}
		}
		
		fs = new V8FileSystem();
		
		if(sourceFile == null)
		{
			System.err.println("Please insert a source file with -s");
			return;
		}
		
		if(!fs.exists(sourceFile))
		{
			System.err.println("File not exist or no access");
			return;
		}
		
		String result = null;
		try
		{
			LessCompiler compiler = new LessCompilerV8();
			String source = fs.readFile(sourceFile);
			result = compiler.compileLess(source, sourceFile, compress);
			compiler.release();
		}
		catch (IOException e)
		{
			System.err.println("Cannot read the source file");
			return;
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
				fs.writeFile(outputFile, result);
			}
			catch (IOException e)
			{
				System.err.println("Cannot write the output file");
				return;
			}
		}
		else
		{
			System.out.print(result);
		}
	}

}
