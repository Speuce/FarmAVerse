package com.speuce.schemetic;

import com.speuce.farmtopia.plot.BuildQueue;

public class TestPredefinedSchem extends PredefinedSchem{
	Long start;
	public TestPredefinedSchem() {
		super(null,null,null);
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
