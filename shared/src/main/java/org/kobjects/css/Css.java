package org.kobjects.css;

public class Css {
  /**
   * Specificity weight for element name and pseudoclass selectors.
   */
  static final int SPECIFICITY_D = 1;
  /**
   * Specificity weight for element name selectors.
   */
  static final int SPECIFICITY_C = 100 * SPECIFICITY_D;
  /**
   * Specificity weight for id selectors.
   */
  static final int SPECIFICITY_B = 100 * SPECIFICITY_C;
  /**
   * Specificity weight for !important selectors
   */
  static final int SPECIFICITY_IMPORTANT = 100 * SPECIFICITY_B;

  static int indexOfIgnoreCase(String[] array, String s) {
    int len = array.length;
    if (s == null) {
      for (int i = 0; i < len; i++) {
        if (array[i] == null) {
          return i;
        }
      }
    } else {
      s = identifierToLowerCase(s);
      for (int i = 0; i < len; i++) {
        if (s.equals(array[i])) {
          return i;
        }
      }
    }
    return -1;
  }

  public static String identifierToLowerCase(String s) {
    int len = s.length();
    for (int i = 0; i < len; i++) {
      char c = s.charAt(i);
      if (c >= 'A' && c <= 'Z') {
        StringBuilder sb = new StringBuilder(s.substring(0, i));
        do {
          c = s.charAt(i++);
          if (c >= 'A' && c <= 'Z') {
            c += (char) ('a' - 'A');
          }
          sb.append(c);
        } while (i < len);
        return sb.toString();
      }
    }
    return s;
  }

  /**
   * Returns a string array created by splitting the given string at the given separator.
   */
  static String[] split(String target, char separator) {
    int separatorInstances = 0;
    int targetLength = target.length();
    for (int index = target.indexOf(separator, 0);
         index != -1 && index < targetLength;
         index = target.indexOf(separator, index)) {
      separatorInstances++;
      // Skip over separators
      if (index >= 0) {
        index++;
      }
    }
    String[] results = new String[separatorInstances + 1];
    int beginIndex = 0;
    for (int i = 0; i < separatorInstances; i++) {
      int endIndex = target.indexOf(separator, beginIndex);
      results[i] = target.substring(beginIndex, endIndex);
      beginIndex = endIndex + 1;
    }
    // Last piece (or full string if there were no separators).
    results[separatorInstances] = target.substring(beginIndex);
    return results;
  }

  static String cssName(String name) {
    StringBuilder sb = new StringBuilder(name.length());
    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      if (c >= 'A' && c <= 'Z') {
        sb.append((char) (c - 'A' + 'a'));
      } else if (c == '_') {
        sb.append('-');
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }
}
