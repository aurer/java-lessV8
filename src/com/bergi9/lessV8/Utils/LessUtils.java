package de.otris.lessV8.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class LessUtils
{

	public static String streamToString(InputStream stream) throws IOException
	{
		final char[] buffer = new char[4048];
		final StringBuilder out = new StringBuilder();
		Reader in = new InputStreamReader(stream, "UTF-8");
		int size = 0;
		while((size = in.read(buffer, 0, buffer.length)) > -1){
			out.append(buffer, 0, size);
		}
		in.close();
		return out.toString();
	}
	
}
