package main.java.com.speuce.sql;


public enum DataType{
	INT("INT"),
	DATE("DATE"),
	DATETIME("DATETIME"),
	DOUBLE("DOUBLE"),
	TEXT("TEXT"),
	TINYTEXT("TINYTEXT"),
	FLOAT("FLOAT"),
	BOOLEAN("BOOLEAN"),
	UUID("CHAR(36)"),
	BIGINT("BIGINT"),
	BLOB("BLOB");
	
	private String name;
	private DataType(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name.toLowerCase();
	}
	
	
}
