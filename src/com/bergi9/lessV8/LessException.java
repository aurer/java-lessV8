package de.otris.lessV8;

import java.util.ArrayList;
import java.util.List;

import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;

public class LessException extends Exception
{
	private static final long serialVersionUID = 4154228266662276768L;

	private String type;
	private String filename;
	private String stack;
	private int index;
	private int line;
	private int callLine;
	private int column;
	private List<String> extract;
	
	public LessException(String message)
	{
		super(message);
	}
	
	public LessException(String message, Throwable e)
	{
		super(message, e);
	}
	
	public LessException(V8Object v8Object)
	{
		super(v8Object.getString("message"));
		
		if(v8Object.getType("stack") == V8Object.STRING)
		{
			stack = v8Object.getString("stack");
			return; // if javascript exception, in example typeerror, then only show the stack
		}
		
		if(v8Object.getType("type") == V8Object.STRING)
		{
			type = v8Object.getString("type");
		}
		
		if(v8Object.getType("filename") == V8Object.STRING)
		{
			filename = v8Object.getString("filename");
		}
		
		if(v8Object.getType("index") == V8Object.INTEGER)
		{
			index = v8Object.getInteger("index");
		}
		
		if(v8Object.getType("line") == V8Object.INTEGER)
		{
			line = v8Object.getInteger("line");
		}
		
		if(v8Object.getType("callLine") == V8Object.INTEGER)
		{
			callLine = v8Object.getInteger("callLine");
		}
		
		if(v8Object.getType("column") == V8Object.INTEGER)
		{
			column = v8Object.getInteger("column");
		}
		
		if(v8Object.getType("extract") == V8Object.V8_ARRAY)
		{
			V8Array array = v8Object.getArray("extract");
			
			extract = new ArrayList<String>(array.length());
			
			for(int i = 0, len = array.length(); i < len; i++)
			{
				if(array.getType(i) == V8Array.STRING)
				{
					String e = array.getString(i);
					extract.add(e);
				}
				else
				{
					extract.add("");
				}
			}
			
			array.release();
		}
	}
	
	@Override
	public String getMessage()
	{
		if (stack != null)
		{
			StringBuilder sb = new StringBuilder();
			
			sb.append(super.getMessage());
			sb.append("\n");
			sb.append(stack);
			
			return sb.toString();
		}
		
		if (type != null) {
			
			StringBuilder sb = new StringBuilder();
			
			sb.append(String.format("%s: %s on line %s, column %s:\n", type, super.getMessage(), line, column));
			
			if (extract != null && !extract.isEmpty()) {
				
				for(int i = 0, j = -1, len = extract.size(); i < len; i++, j++)
				{
					sb.append(line + j);
					sb.append(' ');
					sb.append(extract.get(i));
					sb.append("\n");
					if(j == 0)
					{
						sb.append(new String(new char[column + 2]).replace('\0', ' '));
						sb.append("^\n");
					}
				}
			}
			
			return sb.toString();
		}
		
		return super.getMessage();
	}
	
	public String getType()
	{
		return type;
	}
	
	public String getFilename()
	{
		return filename;
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public int getLine()
	{
		return line;
	}
	
	public int getCallLine()
	{
		return callLine;
	}
	
	public int getColumn()
	{
		return column;
	}
	
	public List<String> getExtract()
	{
		return extract;
	}
}
