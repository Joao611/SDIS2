package chord;

public class UnsignedByte {
	private Short b;

	public UnsignedByte(short short1) {
		
		this.setB((short) (short1 & 0xFF));
		System.out.println("Create "+ this.getB());
		// TODO see unsigness
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UnsignedByte [b=" + getB() + "]";
	}

	/**
	 * Overloads Operator < in mod 2*m
	 * @param key
	 * @return
	 */
	public boolean smallerThan(UnsignedByte key) {
		return this.getB() <= key.getB();
	}

	/**
	 * @param key
	 * @return
	 */
	public boolean equalTo(UnsignedByte key) {
		// TODO Auto-generated method stub
		System.out.println("EqualTo "+this +" "+key);
		return this.getB() == key.getB();
	}

	/**
	 * @return the b
	 */
	public Short getB() {
		return b;
	}

	/**
	 * @param b the b to set
	 */
	public void setB(Short b) {
		this.b = b;
	}
	
}
