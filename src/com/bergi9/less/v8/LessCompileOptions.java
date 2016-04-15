package com.bergi9.less.v8;

import java.util.HashMap;
import java.util.Map;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.utils.V8ObjectUtils;

public class LessCompileOptions
{
	private boolean compress = false;
	private boolean strictMath = false;
	private boolean relativeUrls = false;
	
	private Map<String, String> globalVars = null;
	private Map<String, String> modifyVars = null;
	
	private boolean async = false;
	private LessCompileCallback asyncCallback = null;

	/**
	 * Convert options to V8-Object
	 * Release the V8Object after using it or it will not cleaned by GC
	 * @param runtime
	 * @return
	 */
	public V8Object toV8Object(V8 runtime)
	{
		V8Object object = new V8Object(runtime);
		
		object.add("compress", compress);
		object.add("strictMath", strictMath);
		object.add("relativeUrls", relativeUrls);
		
		if(globalVars != null)
		{
			V8Object v8GlobalVars = V8ObjectUtils.toV8Object(object.getRuntime(), globalVars);
			object.add("globalVars", v8GlobalVars);
			v8GlobalVars.release();
		}
		
		if(modifyVars != null)
		{
			V8Object v8ModifyVars = V8ObjectUtils.toV8Object(object.getRuntime(), modifyVars);
			object.add("modifyVars", v8ModifyVars);
			v8ModifyVars.release();
		}
		
		return object;
	}
	
	/**
	 * Add global variable by key/value
	 * @param variableName
	 * @param value
	 */
	public void addGlobalVar(String variableName, String value)
	{
		if(globalVars == null)
		{
			globalVars = new HashMap<String, String>();
		}
		
		globalVars.put(variableName, value);
	}
	
	/**
	 * Remove global variable by key
	 * @param variableName
	 */
	public void removeGlobalVar(String variableName)
	{
		if(globalVars != null)
		{
			globalVars.remove(variableName);
		}
	}
	
	public String getGlobalVar(String variableName)
	{
		return globalVars == null ? null : globalVars.get(variableName);
	}
	
	public void addModifyVar(String variableName, String value)
	{
		if(modifyVars == null)
		{
			modifyVars = new HashMap<String, String>();
		}
		
		modifyVars.put(variableName, value);
	}
	
	public void removeModifyVar(String variableName)
	{
		if(modifyVars != null)
		{
			modifyVars.remove(variableName);
		}
	}
	
	public String getModifyVar(String variableName)
	{
		return modifyVars == null ? null : modifyVars.get(variableName);
	}
	
	public boolean isCompress()
	{
		return compress;
	}

	public void setCompress(boolean compress)
	{
		this.compress = compress;
	}

	public boolean isStrictMath()
	{
		return strictMath;
	}

	public void setStrictMath(boolean strictMath)
	{
		this.strictMath = strictMath;
	}

	public boolean isRelativeUrls()
	{
		return relativeUrls;
	}

	public void setRelativeUrls(boolean relativeUrls)
	{
		this.relativeUrls = relativeUrls;
	}

	public boolean isAsync()
	{
		return async;
	}

	public void setAsync(boolean async)
	{
		this.async = async;
	}

	public LessCompileCallback getAsyncCallback()
	{
		return asyncCallback;
	}

	public void setAsyncCallback(LessCompileCallback asyncCallback)
	{
		this.asyncCallback = asyncCallback;
	}
}
