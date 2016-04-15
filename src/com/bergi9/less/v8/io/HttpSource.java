package com.bergi9.less.v8.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.bergi9.less.v8.util.LessUtils;

public class HttpSource extends AbstractSource
{
	private URL url = null;
	
	public HttpSource(String path)
	{
		try
		{
			this.url = new URL(path);
			this.path = url.toString();
		}
		catch (MalformedURLException e)
		{
			this.url = null;
		}
	}
	
	public HttpSource(URL url)
	{
		this.url = url;
		path = url.toString();
	}

	@Override
	public String read() throws IOException
	{
		return readHttp(url);
	}
	
	public static boolean isValid(String url)
	{
		try
		{
			return isValid(new URL(url));
		}
		catch (MalformedURLException e)
		{
			return false;
		}
	}
	
	public static boolean isValid(URL url)
	{
		return url.getProtocol().matches("https?");
	}

	public static String readHttp(String url) throws IOException
	{
		try
		{
			return readHttp(new URL(url));
		}
		catch (MalformedURLException e)
		{
			throw new IOException("Invalid URL: " + url, e);
		}
	}
	
	public static String readHttp(URL url) throws IOException
	{
		if(url == null)
		{
			throw new NullPointerException("Url cannot be null. Maybe an invalid url in constructor?");
		}
		
		HttpURLConnection connection = null;
		
		if(url.getProtocol().equals("https"))
		{
			connection = (HttpsURLConnection)url.openConnection();
		}
		else
		{
			connection = (HttpURLConnection)url.openConnection();
		}
		
		connection.setUseCaches(false);
		
		// http://stackoverflow.com/a/1921530/2702940
		// prevents keep-alive connections which causes slowdown on .getInputStream()
		connection.setRequestProperty("Connection", "close");
		connection.setRequestMethod("GET");
		connection.connect();
		
		InputStream stream = connection.getInputStream();
		
		String source = LessUtils.streamToString(stream);
		
		stream.close();

		return source;
	}
	
}
