package org.kobjects.nativehtml.util;

import org.kobjects.nativehtml.css.CssEnum;

public class Strings {
  

  // Visible for testing
  static final String toLetters(int n, char c0, int base) {
    StringBuilder sb = new StringBuilder();
    do {
      n--; // 1->0=a
      sb.insert(0, (char) (c0 + n % base));
      n /= base;
    } while (n != 0);
    return sb.toString();
  }

  private static final String[] ROMAN_DIGITS = {"M", "CM","D","CD","C", "XC", "L", "XL", "X","IX","V","IV","I"};
  private static final int[] ROMAN_VALUES = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};

  static final String toRoman(int n) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < ROMAN_VALUES.length; i++) {
      while (n % ROMAN_VALUES[i] < n) {
        sb.append(ROMAN_DIGITS[i]);
        n -= ROMAN_VALUES[i];
      }
    }
    return sb.toString();
  }

  
  public static String getBullet(CssEnum listStyleType, int listIndex) {
    
    switch (listStyleType) {
      case DECIMAL:
        return String.valueOf(listIndex) + ". ";
      case LOWER_LATIN:
        return toLetters(listIndex, 'a', 26) + " ";
      case LOWER_GREEK:
        return toLetters(listIndex, '\u03b1', 25) + " ";
      case LOWER_ROMAN:
        return toRoman(listIndex) + " ";
      case UPPER_LATIN:
        return toLetters(listIndex, 'a', 26) + " ";
      case UPPER_ROMAN:
        return toRoman(listIndex).toUpperCase() + " ";
      case SQUARE:
        return "\u25aa ";
      default:
        return "\u2022 ";
    }
    
  }

}
