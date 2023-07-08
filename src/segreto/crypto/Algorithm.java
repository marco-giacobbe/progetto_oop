package segreto.crypto;

public enum Algorithm {
	AESCBC("AES"),
        AESECB("AES"),
        DESCBC("DES"),
        DESECB("DES"),
	RSAECB("RSA"),
	MD5("MD5"),
	SHA256("SHA-256");

	private String value;

	private Algorithm(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		switch (this) {
			case AESCBC:
				return "AES/CBC/PKCS5Padding";
			case AESECB:
				return "AES/ECB/PKCS5Padding";
			case DESCBC:
				return "DES/CBC/PKCS5Padding";
			case DESECB:
				return "DES/ECB/PKCS5Padding";
			case RSAECB:
				return "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
			default:
				return super.toString();
		}
	}

	public String getValue() {
		return value;
	}
}
