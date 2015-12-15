package de.otris.lessV8;

public interface LessCompiler
{
	public String compileLess(String source) throws LessException;
	
	public String compileLess(String source, boolean compress) throws LessException;
	
	public String compileLess(String source, String path) throws LessException;
	
	public String compileLess(String source, String path, boolean compress) throws LessException;
	
	public void compileLessAsync(String source, LessCallback callback);
	
	public void compileLessAsync(String source, boolean compress, LessCallback callback);
	
	public void compileLessAsync(String source, String path, LessCallback callback);
	
	public void compileLessAsync(String source, String path, boolean compress, LessCallback callback);
	
	public void release();
}
