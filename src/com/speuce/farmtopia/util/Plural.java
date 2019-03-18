package com.speuce.farmtopia.util;

public class Plural {
	public static String pluralize(String in){
		if(in.endsWith("s")){
			return in;
		}
		if(in.endsWith("eat")){
			return in;
		}
		if(in.endsWith("ch") || in.endsWith("z") || in.endsWith("sh") || in.endsWith("x")){
			return in + "es";
		}
		if(in.endsWith("f")){
			return in.substring(0, in.length()-1) + "ves";
		}
		return in + "s";
	}
}
