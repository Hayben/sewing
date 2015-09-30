package com.sidooo.crawl.instructment;


public class Inc extends BaseInstructment {

	private String variable;
	
	public Inc(String name) {
		this.variable = name;
	}

	public void setVariable(String varName) {
		this.variable = varName;
	}
	
	public String getVariable() {
		return this.variable;
	}

	@Override
	public boolean execute(Context context) {
		int value = Integer.parseInt(context.data.getVariable(variable));
		value += 1;
		context.data.setVariable(variable, Integer.toString(value));
		return true;
	}

}
