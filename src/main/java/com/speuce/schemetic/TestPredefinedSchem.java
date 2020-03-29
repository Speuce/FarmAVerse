package main.java.com.speuce.schemetic;

import main.java.com.speuce.farmtopia.plot.BuildQueue;

public class TestPredefinedSchem extends PredefinedSchem{
	Long start;
	public TestPredefinedSchem() {
		super(null,null);
		this.start = System.currentTimeMillis();
	}
	@Override
	public void build(){
		Long end = System.currentTimeMillis();
		if(end - start > 10000){
			BuildQueue.dump(end - start);
		}
		return;
	}

}
