package com.rzteam.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {

	/***
	 * 从流中获取数据
	 * @param inpustream 输入流
	 * @return
	 * @throws IOException 
	 */
	public static String getStringFromStream(InputStream inpuStream) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while( (len = inpuStream.read(buffer)) != -1) {
			out.write(buffer, 0, len);
		}
		String result = out.toString();
		inpuStream.close();
		out.close();
		return result;
	}
}
