package com.bq.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
	public static Date parse(String str) {
		try {
			return new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy", Locale.US)
					.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}
}
