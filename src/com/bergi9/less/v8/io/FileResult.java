package com.bergi9.less.v8.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileResult extends AbstractResult
{
	private String path;
	
	public FileResult(String path)
	{
		this.path = path;
	}
	
	public FileResult(File file)
	{
		this.path = file.getAbsolutePath();
	}

	@Override
	public void write(String css) throws IOException
	{
		OutputStream stream = new FileOutputStream(path);
		stream.write(css.getBytes("UTF-8"));
		stream.flush();
		stream.close();
	}
}
