package ca.uab.ece.salt.pangor.learn.eval;

import ca.ubc.ece.salt.pangor.learn.EvaluationResult;

public class FMeasure implements Data {

	@Override
	public String[] getData(EvaluationResult[] dataSetResult) {
		String[] data = new String[dataSetResult.length];
		for(int j = 0; j < dataSetResult.length; j++) {
			data[j] = String.valueOf(dataSetResult[j].fMeasure);
		}
		return data;
	}

	@Override
	public String[] getLim() {
		return new String[]{ "0.0", "0.3" };
	}

	@Override
	public String[] getAxp() {
		return new String[]{ "0.0", "0.3", "3.0" };
	}

	@Override
	public String getLabel() {
		return "F-Measure";
	}

}