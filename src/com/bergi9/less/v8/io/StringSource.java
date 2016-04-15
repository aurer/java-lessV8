package com.bergi9.less.v8.io;

import java.io.IOException;

public class StringSource extends AbstractSource
{
	private String content;
	
	public StringSource(String content)
	{
		this(content, null);
	}
	
	public StringSource(String content, String path)
	{
		this.content = content;
		this.path = path;
	}
	
	private void checkImports() throws IOException
	{
		if(content.contains("@import") && (path != null || path.isEmpty()))
		{
			throw new IOException("Path cannot be null or empty if there are imports");
		}
	}
	
	@Override
	public String read() throws IOException
	{
		checkImports();
		return content;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

}
