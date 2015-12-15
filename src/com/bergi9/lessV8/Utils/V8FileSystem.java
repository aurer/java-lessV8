package de.otris.lessV8.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;

public class V8FileSystem
{
	public boolean exists(final String path)
	{
		File f = new File(path);
		return f.exists() && !f.isDirectory();
	}
	
	public String readFile(final String fileName) throws IOException
	{
		InputStream stream = new FileInputStream(fileName);
		return LessUtils.streamToString(stream);
	}
	
	public void writeFile(final String fileName, final String content) throws IOException
	{
		OutputStream stream = new FileOutputStream(fileName);
		stream.write(content.getBytes("UTF-8"));
		stream.flush();
		stream.close();
	}
	
	public static void register(V8 v8)
	{
		V8FileSystem fs = new V8FileSystem();
		V8Object v8fs = new V8Object(v8);
		v8.add("fs", v8fs);
		v8fs.registerJavaMethod(fs, "exists", "exists", new Class<?>[] { String.class });
		v8fs.registerJavaMethod(fs, "readFile", "readFile", new Class<?>[] { String.class });
		v8fs.registerJavaMethod(fs, "writeFile", "writeFile", new Class<?>[] { String.class, String.class });
		v8fs.release();
	}
}
