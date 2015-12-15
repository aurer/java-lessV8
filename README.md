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
You can look into [Example.java](https://github.com/bergi9/java-lessV8/commit/35e6a2f4d6630a279b1e74e58b8bfc81936e6a01).
But the Example.java works synchronously. You can call them asynchronously too.

``` Java
//create new instance
LessCompiler c = new LessCompilerV8();

//change the J2V8 library location (in case if no file access):
LessCompiler c = new LessCompilerV8("C:\temp");

//synchronous call as string
String result = c.compileLess(".foo { width: (1+1)px }");

//synchronous call with file
//it will import files with @imports automatically, yes even with dynamic imports
V8FileSystem fs = new V8FileSystem();
String source = fs.readFile("D:\\less\\main.less");
String result = c.compileLess(source, "D:\\less\\main.less");

//synchronous call with file and compression
String result = c.compileLess(source, "D:\\less\\main.less", true);

//asynchronous call
LessCallback callback = new LessCallback()
{
	@Override
	public void onLessCompiled(String css, LessException e)
	{
		//Code
	}
};
c.compileLessAsync(source, file, compress, callback);

// API
new LessCompilerV8();
new LessCompilerV8(libraryLocation);

LessCompiler.compileLess(source);
LessCompiler.compileLess(source, compress);
LessCompiler.compileLess(source, file);
LessCompiler.compileLess(source, file, compress);
LessCompiler.compileLessAsync(source, callback);
LessCompiler.compileLessAsync(source, compress, callback);
LessCompiler.compileLessAsync(source, file, callback);
LessCompiler.compileLessAsync(source, file, compress, callback);

V8FileSystem.exists(file);
V8FileSystem.readFile(file);
V8FileSystem.writeFile(file, content);
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
