/**
 * Encode long type number into customized Base32/64 string.
 */
package com.shntec.bp.util;

import java.math.BigInteger;

/**
 * @author 1
 *
 */
public class NumberConverter {
	
	// Base 64 character set, notice not the standard base 64
	static final private char BASE64_CHARACTER_TABLE[] = { 
		'0', '1', '2', '3', '4', '5', '6', '7', 
		'8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 
		'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 
		'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 
		'W', 'X', 'Y', 'Z', '_', '.', 'a', 'b', 
		'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
		'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 
		's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
	};

	// Mode for base 64 is 6, 2^6=64, composed by 11 character
	static final private int MODE_BASE64 = 6;
	
	// Mode for base 32 is 5, 2^5=32, composed by 13 character
	// Base 32 has only number 0 to 9 and top 22 capital letters
	static final private int MODE_BASE32 = 5;
	
	// Default is base64
	static final private int DEFAULT_MODE = MODE_BASE64;
	// Max mode value is base64
	static final private int MAX_MODE = MODE_BASE64;
	
	private int mode = DEFAULT_MODE;
	
	static private NumberConverter instance = null;
	
	static public synchronized NumberConverter getInstance() {

		if (null == instance ) {
			instance = new NumberConverter();
		}
		
		return instance;
	}
	
	private NumberConverter() {

	}
	
	// Easy to use wrapper
	public String convertInteger(int number) {
		return convert(BigInteger.valueOf(number), Integer.SIZE);
	}

	public String convertLong(long number) {
		return convert(BigInteger.valueOf(number), Long.SIZE);
	}
	
	public String convertInt128(BigInteger number) {
		return convert(number, 128);
	}
	
	// bitLength is the length of processes bits. If the real bit length of number is 
	// less than bitLength, the zero will be appended to the left. If the real bit 
	// length of number is exceed bitLength, only bits within bitLength are processed
	
	public String convert(BigInteger number, int bitLength) {

		BigInteger one = BigInteger.valueOf(1);
		BigInteger two = BigInteger.valueOf(2);
		BigInteger mask = two.pow(mode).subtract(one);
		
		int length =  (int) Math.ceil(bitLength/(mode*1.0));
		char base[] = new char[length];
		for (int i=0; i<length; ++i) {
			int shift = i*mode;
			int index = number.shiftRight(shift).and(mask).intValue();
			base[i] = BASE64_CHARACTER_TABLE[index];
		}
				
		return new String(base);
	}
	
	public BigInteger revert(String base) {

		BigInteger number = BigInteger.ZERO;
		String ct = new String(BASE64_CHARACTER_TABLE);
		
		BigInteger two = BigInteger.valueOf(2);
		BigInteger bn = two.pow(mode);
		
		for (int i=0; i<base.length(); ++i) {
			BigInteger value = BigInteger.valueOf(ct.indexOf(base.charAt(i)));			
			number = number.add(value.multiply(bn.pow(i)));
		}
		
		return number;
	}

	public int getMode() {
		return mode;
	}
	
	public void setMode(int mode) {
		this.mode = mode > MAX_MODE ? MAX_MODE : mode;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		NumberConverter converter = NumberConverter.getInstance();

		//Big number: 3DA386C000BFCF6E9FC1A0BB87BC01E9
		BigInteger bigNumber = new BigInteger("3DA386C000BFCF6E7FC1A0BB77BC01E9", 16);
		BigInteger bigNumber2 = new BigInteger("A1233DA386C000BFCF6E7FC1A0BB77BC01E9", 16);
		Long longNumber = Long.parseLong("7FC1A0BB77BC01E9", 16);
		Integer integerNumber = Integer.parseInt("77BC01E9", 16);
		System.out.println(converter.convertInteger(integerNumber));
		System.out.println(converter.convertLong(longNumber));
		System.out.println(converter.convert(BigInteger.valueOf(longNumber), Long.SIZE));
		System.out.println(converter.convert(bigNumber, 128));
		System.out.println(converter.convertInt128(bigNumber));
		System.out.println(converter.convert(bigNumber2, 128));
		System.out.printf("0x%X\n", bigNumber);
		System.out.printf("0x%X\n", converter.revert(converter.convert(bigNumber, 128)));

		converter.setMode(MODE_BASE32);

		System.out.println(converter.convertLong(longNumber));
		System.out.println(converter.convert(BigInteger.valueOf(longNumber), Long.SIZE));
		System.out.println(converter.convert(bigNumber, 128));
		System.out.println(converter.convert(bigNumber2, 128));
	}

}
