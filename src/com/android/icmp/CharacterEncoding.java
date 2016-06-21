package com.android.icmp;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class CharacterEncoding {
	public static void main(String args[]) throws Exception {
		
		String defaultCharacterEncoding = System.getProperty("file.encoding");
		System.out.println("defaultCharacterEncoding by property : " + defaultCharacterEncoding);
		System.out.println("defaultChracterEncoding by code : " + getDefaultCharEncoding());
		System.out.println("defaultChracterEncoding by charSet : " + Charset.defaultCharset());
		
		System.out.println("¹Ù²¸¶ó¾å!!");
		
		System.setProperty("file.encoding", "UTF-8");
		defaultCharacterEncoding = System.getProperty("file.encoding");
		System.out.println("defaultCharacterEncoding by property : " + defaultCharacterEncoding);
		System.out.println("defaultChracterEncoding by code : " + getDefaultCharEncoding());
		System.out.println("defaultChracterEncoding by charSet : " + Charset.defaultCharset());
		
	}
	
	public static String getDefaultCharEncoding() {
		byte[] bArray = {'w'};
		InputStream is = new ByteArrayInputStream(bArray);
		InputStreamReader reader = new InputStreamReader(is);
		String defaultCharacterEncoding = reader.getEncoding();
		return defaultCharacterEncoding;
	}
}
