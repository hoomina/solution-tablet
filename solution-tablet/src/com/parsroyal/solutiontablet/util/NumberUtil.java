package com.parsroyal.solutiontablet.util;

import java.text.DecimalFormat;

/**
 * Created by Mahyar on 8/25/2015.
 */
public class NumberUtil {

  public static String getCommaSeparated(Double displayAmount) {
    DecimalFormat decimalFormat = new DecimalFormat();
    decimalFormat.applyPattern("###,###");
    return decimalFormat.format(displayAmount);
  }

  public static String getCommaSeparated(Long displayAmount) {
    DecimalFormat decimalFormat = new DecimalFormat();
    decimalFormat.applyPattern("###,###");
    return decimalFormat.format(displayAmount);
  }

  public static String getCommaSeparated(Integer displayAmount) {
    DecimalFormat decimalFormat = new DecimalFormat();
    decimalFormat.applyPattern("###,###");
    return decimalFormat.format(displayAmount);
  }

  public static String formatDoubleWith2DecimalPlaces(Double number) {
    DecimalFormat df = new DecimalFormat("#.##");
    return df.format(number);
  }

  public static String formatPersian3DecimalPlaces(Double number) {
    DecimalFormat df = new DecimalFormat("#.###");
    return digitsToPersian(df.format(number));
  }

  public static String formatPersian1DecimalPlaces(Double number) {
    DecimalFormat df = new DecimalFormat("#.#");
    return digitsToPersian(df.format(number));
  }

  public static String digitsToEnglish(String input) {
    if (Empty.isEmpty(input)) {
      return input;
    }
    char[] persian = ("۰۱۲۳۴۵۶۷۸۹").toCharArray();
    char[] arabic = ("٠١٢٣٤٥٦٧٨٩").toCharArray();

    for (int i = 0; i < persian.length; i++) {
      input = input.replaceAll(String.valueOf(persian[i])
          , String.valueOf(i));
    }

    for (int i = 0; i < arabic.length; i++) {
      input = input.replaceAll(String.valueOf(arabic[i])
          , String.valueOf(i));
    }

    return input;
  }

  public static String digitsToPersian(String input) {
    if (Empty.isEmpty(input)) {
      return input;
    }
    char[] persian = ("۰۱۲۳۴۵۶۷۸۹").toCharArray();
    char[] english = ("0123456789").toCharArray();

    for (int i = 0; i < english.length; i++) {
      input = input.replaceAll(String.valueOf(english[i]), String.valueOf(persian[i]));
    }

    return input;
  }

  public static String digitsToPersian(Long number) {
    if (Empty.isEmpty(number)) {
      return "";
    }

    return digitsToPersian(String.valueOf(number));
  }

  public static String digitsToPersian(Double number) {
    if (Empty.isEmpty(number)) {
      return "";
    }

    return digitsToPersian(String.valueOf(number));
  }

  public static String digitsToPersian(int number) {
    return digitsToPersian(String.valueOf(number));
  }
}
