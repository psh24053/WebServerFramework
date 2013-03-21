/**
 * 
 */
package com.shntec.bp.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 1
 *
 */
public final class CommonUtil {

	public static final Long MILLISECOND_IN_DAY = (Long)(24*60*60*1000L);
	
	public static byte[] readBinaryRequestContent (HttpServletRequest request) {
		
		int len = request.getContentLength();
		
		if ( len <= 0 ) {
			return null;
		}

		byte[] content = new byte[len];
		
		try {
			// Read complete request content
			InputStream ins = request.getInputStream();
			
			int ch = 0;
			int i = 0;
			while ((ch = ins.read()) != -1 ) {
				content[i++] =  ((byte)ch);
			}
			
			ins.close();

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return content;
	}
	
	public static String readUtf8RequestContent (HttpServletRequest request) {
		
		byte content[] = readBinaryRequestContent(request);

		if ( null == content) {
			return null;
		}
		
		String message = null;
		try {
			message = new String(content, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		
		return message;
	}
	
	public static void writeUtf8ResponseContent (HttpServletResponse response, String responseContent) {
		
		PrintWriter out = null;

		try {
			response.setCharacterEncoding("utf8");
			out = response.getWriter();
			out.write(responseContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			out.close();
		}
	}
	
	// Return path looks like "/var/www/webapp/appname"
	public static String getWebappRoot() {
		
		String webappRoot = null;
		
		// /XXXX/WEB-INF/classes
		URL urlOfClasses = CommonUtil.class.getClassLoader().getResource("/");
		if ( null != urlOfClasses ) {
			String pathOfClasses = urlOfClasses.getPath();
			webappRoot = pathOfClasses.substring(0, pathOfClasses.indexOf("/WEB-INF/classes"));
		}
		
		return webappRoot;
		
	}
	
	public static boolean isEmail(String text) {
		
		boolean isEmail = false;
		
		String regex ="^[a-zA-Z0-9]+([-_+.a-zA-Z0-9])*@[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$";
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		
		if(matcher.matches()) {
			isEmail = true;
		}
		
		return isEmail;
		
	}
	
	public static boolean isMobilePhoneNumber(String text){
		
		boolean isPhoneNumber = false;

		// 13812345678
		// +8613812345678
		// +86-13812345678
		// 86-13812345678
		String regex ="^[+]{0,1}[0-9]{0,3}[-]{0,1}[0-9]{11}$";
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		
		if(matcher.matches()) {
			isPhoneNumber = true;
		}
		
		
		return isPhoneNumber;
		
	}

	/** 
	 * Return : 
	 *   1 (>0): if date1 after date2
	 *   0 (=0): if date1 equal date2
	 *  -1 (<0): if date1 before date2
	 */
	public static int compareDate (Date date1, Date date2) {
		
		int ret = 0;
		
		if (date1 == null && date2 == null) {
			ret = 0;
		}
		else if (date1 != null) {
			if (date2 == null) {
				ret = 1;
			}
			else {
				if (date1.after(date2)) {
					ret = 1;
				}
				else if (date1.before(date2)) {
					ret = -1;
				}
				else {
					ret = 0;
				}
			}
		}
		else {
			ret = -1;
		}
		
		return ret;
	}
	
	/** 
	 * Only compare day, without hour minute second
	 * 
	 * Return : 
	 *   1 (>0): if date1 after date2
	 *   0 (=0): if date1 equal date2
	 *  -1 (<0): if date1 before date2
	 */
	public static int compareDate1 (Date date1, Date date2) {

		int ret = 0;
		
		if (date1 == null && date2 == null) {
			return ret;
		}
		
		if (date1 != null) {
			if (date2 == null) {
				ret = 1;
			}
			else {
				
				if (timeRoundToDay(date1).after(timeRoundToDay(date2))) {
					ret = 1;
				}
				else if (timeRoundToDay(date1).before(timeRoundToDay(date2))) {
					ret = -1;
				}
				else {
					ret = 0;
				}
			}
		}
		else {
			ret = -1;
		}
		
		return ret;
	}

	/*
	 * Drop Hour, minute and second
	 **/
	public static Date timeRoundToDay (Date time) {

		Date timeInDay = null;

		Calendar cal = Calendar.getInstance();
		TimeZone timeZone = cal.getTimeZone();

		// Time in millisecond adjust by time zone offset
		Long timeAfterOffset = time.getTime() + timeZone.getRawOffset();
		// Time in millisecond after by drop hour, minute, second
		Long timeAfterRound = (MILLISECOND_IN_DAY) * (timeAfterOffset / MILLISECOND_IN_DAY);
		// Adjust time back to current time zone
		cal.setTimeInMillis(timeAfterRound - timeZone.getRawOffset());

		timeInDay = cal.getTime();
		
		return timeInDay;
	}
	
	/**
	 * 获取日期信息，接受索引信息，如果为0则代表今天，1则代表昨天，2代表前天，以此类推。
	 * 当然，如果输入负数，就是往后的天数
	 * @param index
	 * @return
	 */
	public static String getDate(int index){
		
		Calendar calendar = Calendar.getInstance(); 
		
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - index);
		
		int date = calendar.get(Calendar.DATE);
		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);
		
		return year+"-"+month+"-"+date;
	}
	
	/*
	 * 
	 */
	public static Date addDate (Date date, Long addedInMillis) {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.setTimeInMillis(cal.getTimeInMillis() + addedInMillis);
		
		return cal.getTime();
		
	}
	
	/*
	 *
	 */
	public static String encryptPwd(String password)
			throws NoSuchAlgorithmException {
		String pwdmd5str = null;
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(password.getBytes());
		pwdmd5str = HexCodec.hexEncode(md5.digest());
		return pwdmd5str;
	}

	/**
	 * 比较两个字符串数组是否相等，a1和a2可能为空，a1和a2的所有元素都相等才认为a1与a2相等，
	 * 数组的元素的顺序不影响比较
	 */
	public static boolean isArrayListEquals(
			ArrayList<String> a1, ArrayList<String> a2) {
		
		boolean isEqual = false;
		
		// 
		if ((null == a1) && (null == a2)) {
			// 都为空认为相等
			isEqual = true;
		}
		else if ((null != a1) && (null != a2) 
				&& (a1.size() == a2.size())) {
			// 数组元素数量一致，比较所有元素
			int i = 0;
			for (i=0; i<a1.size(); ++i){
				int j = 0;
				for (j=0;j<a2.size(); ++j) {
					if ( 0 == a1.get(i).compareTo(a2.get(j))){
						// 在a2中找到匹配的项就退出循环
						break;
					}
				}
				if (j == a2.size()) {
					// 循环执行完成，未找到匹配的项，则退出剩余的比较，此时a1未处理完
					break;
				}
			}
			if (i == a1.size()) {
				// 所有 a1的条目都比较完，则a1等于a2
				isEqual = true;
			}
		}

		return isEqual;
		
	}
	
	/** 
	 * Return : 
	 *   1 (>0): if bd1 > bd2
	 *   0 (=0): if bd1 = bd2
	 *  -1 (<0): if bd1 < bd2
	 */
	public static int compareBigDecimal (BigDecimal bd1, BigDecimal bd2) {
	
		int ret = 0;
		
		if (bd1 == null && bd2 == null) {
			ret = 0;
		}
		else if (bd1 != null) {
			if (bd2 == null) {
				ret = 1;
			}
			else {
				ret = bd1.compareTo(bd2);
			}
		}
		else {
			ret = -1;
		}
		
		return ret;
		
	}

}