package com.bergi9.less.v8.io;

public abstract class AbstractSource implements Source
{
	protected String path;

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}
}
