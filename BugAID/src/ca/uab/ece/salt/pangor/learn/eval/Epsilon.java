package ca.uab.ece.salt.pangor.learn.eval;

import ca.ubc.ece.salt.pangor.learn.EvaluationResult;

class Epsilon implements Data {

	@Override
	public String[] getData(EvaluationResult[] dataSetResult) {
		String[] data = new String[dataSetResult.length];
		for(int j = 0; j < dataSetResult.length; j++) {
			data[j] = String.valueOf(Math.round(dataSetResult[j].epsilon*100.0)/100.0);
		}
		return data;
	}

	@Override
	public String[] getLim() {
		return new String[]{ "0.1", "5.9" };
	}

	@Override
	public String[] getAxp() {
		return new String[]{ "0.1", "5.9", "29.0" };
	}

	@Override
	public String getLabel() {
		return "Epsilon";
	}

}