package com.bergi9.less.v8;

public interface LessCompileCallback
{
	public void onSuccess();
	
	public void onError(LessException e);
}
