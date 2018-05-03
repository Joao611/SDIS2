package utils;

public class UnsignedByte {
	private Short unsigned_byte;

	public UnsignedByte(short unsigned_byte) {
		this.unsigned_byte = ((short) (unsigned_byte & 0xFF));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UnsignedByte [b=" + getUsignedByte() + "]";
	}

	/**
	 * Overloads Operator < in mod 2*m
	 * @param key
	 * @return
	 */
	public boolean smallerThan(UnsignedByte key) {
		return this.getUsignedByte() <= key.getUsignedByte();
	}

	/**
	 * @param key
	 * @return
	 */
	public boolean equalTo(UnsignedByte key) {
		System.out.println("EqualTo "+this +" "+key);
		return this.getUsignedByte() == key.getUsignedByte();
	}

	/**
	 * @return the unsigned byte
	 */
	public Short getUsignedByte() {
		return this.unsigned_byte;
	}
	
}
