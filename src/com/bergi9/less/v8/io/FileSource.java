package com.bergi9.less.v8.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.bergi9.less.v8.util.LessUtils;

public class FileSource extends AbstractSource
{
	public FileSource(String path)
	{
		this.path = path;
	}
	
	public FileSource(File file)
	{
		this.path = file.getAbsolutePath();
	}
	
	@Override
	public String read() throws IOException
	{
		if(exists(path))
		{
			return readFile(path);
		}
		
		throw new IOException("File '" + path + "' not exist.");
	}
	
	public static boolean exists(final String path)
	{
		File f = new File(path);
		return f.exists() && !f.isDirectory();
	}
	
	public static String readFile(final String path) throws IOException
	{
		InputStream stream = new FileInputStream(path);
		return LessUtils.streamToString(stream);
	}
}
