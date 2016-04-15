java-lessV8
===========

It uses J2V8 (V8 for Java) for less compiler. Currently it works on Windows & Linux.

It uses less-2.5.3.js (browser-version) and j2v8-3.16.

For logging i've choosed log4j because im using it for tomcat integration.

Requires Java 6 or higher.

Build & deploy
==============
Just call in command line:

Windows:
```
build.bat build
build.bat deploy
```

Linux:
```
./build.sh build
./build.sh deploy
```

Usage
=====
``` Java
// create new instance
LessCompiler c = new LessCompilerV8();

// change the J2V8 library location (in case if no file access):
LessCompiler c = new LessCompilerV8("C:\temp");

// initialize the compiler
c.init();

Source source = new StringSource(".foo { width: (1+1)px; }");
Result result = new StringResult();

// will call it synchronously (default)
c.compile(source, result);

String css = result.getContent();

// asynchronous version
LessCompileCallback callback = new LessCompileCallback()
{
	@Override
	public void onSuccess()
	{
		System.out.println(result.getContent());
	}
	
	@Override
	public void onError(LessException e)
	{
		System.out.println(e.getMessage());
	}
}
LessCompileOptions options = new LessCompileOptions();
options.setAsync(true);
options.setAsyncCallback(callback);
c.compile(source, result, options);

// API
new LessCompilerV8();
new LessCompilerV8(libraryLocation);
LessCompiler.init();
LessCompiler.compileLess(Source, Result);
LessCompiler.compileLess(Source, Result, LessCompileOptions);
LessCompiler.destroy();

new LessCompileOptions();
LessCompileOptions.addGlobalVar(String, String);
LessCompileOptions.removeGlobalVar(String);
LessCompileOptions.getGlobalVar(String);
LessCompileOptions.addModifyVar(String, String);
LessCompileOptions.removeModifyVar(String);
LessCompileOptions.getModifyVar(String);
LessCompileOptions.setCompress(boolean);
LessCompileOptions.isCompress();
LessCompileOptions.setStrictMath(boolean);
LessCompileOptions.isStrictMath();
LessCompileOptions.setRelativeUrls(boolean);
LessCompileOptions.isRelativeUrls();
LessCompileOptions.setAsync(boolean);
LessCompileOptions.isAsync();
LessCompileOptions.setAsyncCallback(LessCompileCallback);
LessCompileOptions.getAsyncCallback();

Interface LessCompileCallback
onSuccess();
onError(LessException);

Interface Source
String read() throws IOException;
String getPath();

FileSource(File)
FileSource(String)
HttpSource(URL)
HttpSource(String)
StringSource(String)

Interface Result
void write(String) throws IOException;

FileResult(File)
FileResult(String)
StringResult()

```

You can call it as cli too
```
java -jar lessV8.jar -s sourcefile [-o outputfile] [-c]
-o outputfile | if you want write it to file otherwise it will print on console
-c | compress css (minify)
```

Limitations
===========
* If you run multiple JVM then you must use different library locations
* LessCompilerV8 isn't threadsafe but can accessed from different threads (synchronization needed)
