package com.speuce.farmtopia.main;

public enum DebugLevel {
	SPAM(1),
	SEMI(2),
	MAJOR(3);
	
	private int value;
	private DebugLevel(int value){
		this.value = value;
	}
	public int getValue(){
		return this.value;
	}
}
