package segreto.crypto;

public abstract class CryptoObject {

	private Algorithm algorithm;

	public void setAlgorithm(Algorithm newAlgorithm) {
		algorithm = newAlgorithm;
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}





}
