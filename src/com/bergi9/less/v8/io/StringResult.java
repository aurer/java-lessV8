package com.bergi9.less.v8.io;

public class StringResult extends AbstractResult
{
	private String content;

	@Override
	public void write(String css)
	{
		content = css;
	}

	public String getContent()
	{
		return content;
	}

}
