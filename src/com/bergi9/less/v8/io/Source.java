package com.bergi9.less.v8.io;

import java.io.IOException;

public interface Source
{
	String read() throws IOException;
	
	String getPath();
}
