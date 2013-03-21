package com.shntec.bp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleDateParser 
{
	public static Date parse(String dateStr) throws ParseException {
		Date				ret = null;
		SimpleDateFormat	sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			ret = sdf.parse(dateStr);
		}
		catch (ParseException e) {
			throw e;
		}
		
		return ret;
	}
	
	public static Date parser1(String dateStr) throws ParseException {
		Date				ret = null;
		SimpleDateFormat	sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			ret = sdf.parse(dateStr);
		}
		catch (ParseException e) {
			throw e;
		}
		
		return ret;
	}
	
	public static Date parse3(String dateStr) throws ParseException {
		Date				ret = null;
		SimpleDateFormat	sdf = new SimpleDateFormat("yyyyMMdd");
		
		try {
			ret = sdf.parse(dateStr);
		}
		catch (ParseException e) {
			throw e;
		}
		
		return ret;
	}
	
	public static String format(Date dt) {
		String				dateStr = null;
		SimpleDateFormat	sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		if (dt != null) {
			dateStr = sdf.format(dt);
		}
		
		return dateStr;
	}

	public static String format1(Date dt) {
		String				dateStr = null;
		SimpleDateFormat	sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		if (dt != null) {
			dateStr = sdf.format(dt);
		}

		return dateStr;
		
	}

	public static String format2(Date dt) {
		String				dateStr = null;
		SimpleDateFormat	sdf = new SimpleDateFormat("HH:mm:ss");
		
		if (dt != null) {
			dateStr = sdf.format(dt);
		}

		return dateStr;
		
	}

	public static String format3(Date dt) {
		String				dateStr = null;
		SimpleDateFormat	sdf = new SimpleDateFormat("yyyyMMdd");
		
		if (dt != null) {
			dateStr = sdf.format(dt);
		}

		return dateStr;
		
	}
	
	public static String format4(Date dt) {
		String				dateStr = null;
		SimpleDateFormat	sdf = new SimpleDateFormat("HHmmss");
		
		if (dt != null) {
			dateStr = sdf.format(dt);
		}

		return dateStr;
		
	}
	
	public static void main (String args[]) {
		
		try {
			System.out.println(parser1("2012-1-01"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
