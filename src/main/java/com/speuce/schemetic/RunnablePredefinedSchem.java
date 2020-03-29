package main.java.com.speuce.schemetic;


public class RunnablePredefinedSchem extends PredefinedSchem{
	private Runnable r;
	public RunnablePredefinedSchem(Runnable r) {
		super(null, null);
		this.r = r;
		// TODO Auto-generated constructor stub
	}

	public void build(){
		r.run();
	}


}
