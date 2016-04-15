package com.bergi9.less.v8;

import com.bergi9.less.v8.io.Result;
import com.bergi9.less.v8.io.Source;

public interface LessCompiler
{
	public void init();
	
	public void compile(Source source, Result result) throws LessException;
	
	public void compile(Source source, Result result, LessCompileOptions options) throws LessException;
	
	public void destroy();
}
