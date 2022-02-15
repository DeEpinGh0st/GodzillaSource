package org.springframework.util;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.StringJoiner;
import java.util.StringTokenizer;
import java.util.TimeZone;
import org.springframework.lang.Nullable;








































public abstract class StringUtils
{
  private static final String[] EMPTY_STRING_ARRAY = new String[0];




  
  private static final String FOLDER_SEPARATOR = "/";




  
  private static final String WINDOWS_FOLDER_SEPARATOR = "\\";



  
  private static final String TOP_PATH = "..";



  
  private static final String CURRENT_PATH = ".";



  
  private static final char EXTENSION_SEPARATOR = '.';




  
  @Deprecated
  public static boolean isEmpty(@Nullable Object str) {
    return (str == null || "".equals(str));
  }
















  
  public static boolean hasLength(@Nullable CharSequence str) {
    return (str != null && str.length() > 0);
  }









  
  public static boolean hasLength(@Nullable String str) {
    return (str != null && !str.isEmpty());
  }



















  
  public static boolean hasText(@Nullable CharSequence str) {
    return (str != null && str.length() > 0 && containsText(str));
  }












  
  public static boolean hasText(@Nullable String str) {
    return (str != null && !str.isEmpty() && containsText(str));
  }
  
  private static boolean containsText(CharSequence str) {
    int strLen = str.length();
    for (int i = 0; i < strLen; i++) {
      if (!Character.isWhitespace(str.charAt(i))) {
        return true;
      }
    } 
    return false;
  }







  
  public static boolean containsWhitespace(@Nullable CharSequence str) {
    if (!hasLength(str)) {
      return false;
    }
    
    int strLen = str.length();
    for (int i = 0; i < strLen; i++) {
      if (Character.isWhitespace(str.charAt(i))) {
        return true;
      }
    } 
    return false;
  }







  
  public static boolean containsWhitespace(@Nullable String str) {
    return containsWhitespace(str);
  }






  
  public static String trimWhitespace(String str) {
    if (!hasLength(str)) {
      return str;
    }
    
    int beginIndex = 0;
    int endIndex = str.length() - 1;
    
    while (beginIndex <= endIndex && Character.isWhitespace(str.charAt(beginIndex))) {
      beginIndex++;
    }
    
    while (endIndex > beginIndex && Character.isWhitespace(str.charAt(endIndex))) {
      endIndex--;
    }
    
    return str.substring(beginIndex, endIndex + 1);
  }







  
  public static String trimAllWhitespace(String str) {
    if (!hasLength(str)) {
      return str;
    }
    
    int len = str.length();
    StringBuilder sb = new StringBuilder(str.length());
    for (int i = 0; i < len; i++) {
      char c = str.charAt(i);
      if (!Character.isWhitespace(c)) {
        sb.append(c);
      }
    } 
    return sb.toString();
  }






  
  public static String trimLeadingWhitespace(String str) {
    if (!hasLength(str)) {
      return str;
    }
    
    int beginIdx = 0;
    while (beginIdx < str.length() && Character.isWhitespace(str.charAt(beginIdx))) {
      beginIdx++;
    }
    return str.substring(beginIdx);
  }






  
  public static String trimTrailingWhitespace(String str) {
    if (!hasLength(str)) {
      return str;
    }
    
    int endIdx = str.length() - 1;
    while (endIdx >= 0 && Character.isWhitespace(str.charAt(endIdx))) {
      endIdx--;
    }
    return str.substring(0, endIdx + 1);
  }






  
  public static String trimLeadingCharacter(String str, char leadingCharacter) {
    if (!hasLength(str)) {
      return str;
    }
    
    int beginIdx = 0;
    while (beginIdx < str.length() && leadingCharacter == str.charAt(beginIdx)) {
      beginIdx++;
    }
    return str.substring(beginIdx);
  }






  
  public static String trimTrailingCharacter(String str, char trailingCharacter) {
    if (!hasLength(str)) {
      return str;
    }
    
    int endIdx = str.length() - 1;
    while (endIdx >= 0 && trailingCharacter == str.charAt(endIdx)) {
      endIdx--;
    }
    return str.substring(0, endIdx + 1);
  }






  
  public static boolean matchesCharacter(@Nullable String str, char singleCharacter) {
    return (str != null && str.length() == 1 && str.charAt(0) == singleCharacter);
  }







  
  public static boolean startsWithIgnoreCase(@Nullable String str, @Nullable String prefix) {
    return (str != null && prefix != null && str.length() >= prefix.length() && str
      .regionMatches(true, 0, prefix, 0, prefix.length()));
  }







  
  public static boolean endsWithIgnoreCase(@Nullable String str, @Nullable String suffix) {
    return (str != null && suffix != null && str.length() >= suffix.length() && str
      .regionMatches(true, str.length() - suffix.length(), suffix, 0, suffix.length()));
  }







  
  public static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
    if (index + substring.length() > str.length()) {
      return false;
    }
    for (int i = 0; i < substring.length(); i++) {
      if (str.charAt(index + i) != substring.charAt(i)) {
        return false;
      }
    } 
    return true;
  }





  
  public static int countOccurrencesOf(String str, String sub) {
    if (!hasLength(str) || !hasLength(sub)) {
      return 0;
    }
    
    int count = 0;
    int pos = 0;
    int idx;
    while ((idx = str.indexOf(sub, pos)) != -1) {
      count++;
      pos = idx + sub.length();
    } 
    return count;
  }







  
  public static String replace(String inString, String oldPattern, @Nullable String newPattern) {
    if (!hasLength(inString) || !hasLength(oldPattern) || newPattern == null) {
      return inString;
    }
    int index = inString.indexOf(oldPattern);
    if (index == -1)
    {
      return inString;
    }
    
    int capacity = inString.length();
    if (newPattern.length() > oldPattern.length()) {
      capacity += 16;
    }
    StringBuilder sb = new StringBuilder(capacity);
    
    int pos = 0;
    int patLen = oldPattern.length();
    while (index >= 0) {
      sb.append(inString, pos, index);
      sb.append(newPattern);
      pos = index + patLen;
      index = inString.indexOf(oldPattern, pos);
    } 

    
    sb.append(inString, pos, inString.length());
    return sb.toString();
  }






  
  public static String delete(String inString, String pattern) {
    return replace(inString, pattern, "");
  }







  
  public static String deleteAny(String inString, @Nullable String charsToDelete) {
    if (!hasLength(inString) || !hasLength(charsToDelete)) {
      return inString;
    }
    
    int lastCharIndex = 0;
    char[] result = new char[inString.length()];
    for (int i = 0; i < inString.length(); i++) {
      char c = inString.charAt(i);
      if (charsToDelete.indexOf(c) == -1) {
        result[lastCharIndex++] = c;
      }
    } 
    if (lastCharIndex == inString.length()) {
      return inString;
    }
    return new String(result, 0, lastCharIndex);
  }










  
  @Nullable
  public static String quote(@Nullable String str) {
    return (str != null) ? ("'" + str + "'") : null;
  }







  
  @Nullable
  public static Object quoteIfString(@Nullable Object obj) {
    return (obj instanceof String) ? quote((String)obj) : obj;
  }





  
  public static String unqualify(String qualifiedName) {
    return unqualify(qualifiedName, '.');
  }






  
  public static String unqualify(String qualifiedName, char separator) {
    return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
  }







  
  public static String capitalize(String str) {
    return changeFirstCharacterCase(str, true);
  }







  
  public static String uncapitalize(String str) {
    return changeFirstCharacterCase(str, false);
  }
  private static String changeFirstCharacterCase(String str, boolean capitalize) {
    char updatedChar;
    if (!hasLength(str)) {
      return str;
    }
    
    char baseChar = str.charAt(0);
    
    if (capitalize) {
      updatedChar = Character.toUpperCase(baseChar);
    } else {
      
      updatedChar = Character.toLowerCase(baseChar);
    } 
    if (baseChar == updatedChar) {
      return str;
    }
    
    char[] chars = str.toCharArray();
    chars[0] = updatedChar;
    return new String(chars);
  }






  
  @Nullable
  public static String getFilename(@Nullable String path) {
    if (path == null) {
      return null;
    }
    
    int separatorIndex = path.lastIndexOf("/");
    return (separatorIndex != -1) ? path.substring(separatorIndex + 1) : path;
  }






  
  @Nullable
  public static String getFilenameExtension(@Nullable String path) {
    if (path == null) {
      return null;
    }
    
    int extIndex = path.lastIndexOf('.');
    if (extIndex == -1) {
      return null;
    }
    
    int folderIndex = path.lastIndexOf("/");
    if (folderIndex > extIndex) {
      return null;
    }
    
    return path.substring(extIndex + 1);
  }






  
  public static String stripFilenameExtension(String path) {
    int extIndex = path.lastIndexOf('.');
    if (extIndex == -1) {
      return path;
    }
    
    int folderIndex = path.lastIndexOf("/");
    if (folderIndex > extIndex) {
      return path;
    }
    
    return path.substring(0, extIndex);
  }








  
  public static String applyRelativePath(String path, String relativePath) {
    int separatorIndex = path.lastIndexOf("/");
    if (separatorIndex != -1) {
      String newPath = path.substring(0, separatorIndex);
      if (!relativePath.startsWith("/")) {
        newPath = newPath + "/";
      }
      return newPath + relativePath;
    } 
    
    return relativePath;
  }












  
  public static String cleanPath(String path) {
    if (!hasLength(path)) {
      return path;
    }
    
    String normalizedPath = replace(path, "\\", "/");
    String pathToUse = normalizedPath;

    
    if (pathToUse.indexOf('.') == -1) {
      return pathToUse;
    }




    
    int prefixIndex = pathToUse.indexOf(':');
    String prefix = "";
    if (prefixIndex != -1) {
      prefix = pathToUse.substring(0, prefixIndex + 1);
      if (prefix.contains("/")) {
        prefix = "";
      } else {
        
        pathToUse = pathToUse.substring(prefixIndex + 1);
      } 
    } 
    if (pathToUse.startsWith("/")) {
      prefix = prefix + "/";
      pathToUse = pathToUse.substring(1);
    } 
    
    String[] pathArray = delimitedListToStringArray(pathToUse, "/");
    
    Deque<String> pathElements = new ArrayDeque<>(pathArray.length);
    int tops = 0;
    int i;
    for (i = pathArray.length - 1; i >= 0; i--) {
      String element = pathArray[i];
      if (!".".equals(element))
      {
        
        if ("..".equals(element)) {
          
          tops++;
        
        }
        else if (tops > 0) {
          
          tops--;
        }
        else {
          
          pathElements.addFirst(element);
        } 
      }
    } 

    
    if (pathArray.length == pathElements.size()) {
      return normalizedPath;
    }
    
    for (i = 0; i < tops; i++) {
      pathElements.addFirst("..");
    }
    
    if (pathElements.size() == 1 && ((String)pathElements.getLast()).isEmpty() && !prefix.endsWith("/")) {
      pathElements.addFirst(".");
    }
    
    String joined = collectionToDelimitedString(pathElements, "/");
    
    return prefix.isEmpty() ? joined : (prefix + joined);
  }






  
  public static boolean pathEquals(String path1, String path2) {
    return cleanPath(path1).equals(cleanPath(path2));
  }















  
  public static String uriDecode(String source, Charset charset) {
    int length = source.length();
    if (length == 0) {
      return source;
    }
    Assert.notNull(charset, "Charset must not be null");
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream(length);
    boolean changed = false;
    for (int i = 0; i < length; i++) {
      int ch = source.charAt(i);
      if (ch == 37) {
        if (i + 2 < length) {
          char hex1 = source.charAt(i + 1);
          char hex2 = source.charAt(i + 2);
          int u = Character.digit(hex1, 16);
          int l = Character.digit(hex2, 16);
          if (u == -1 || l == -1) {
            throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
          }
          baos.write((char)((u << 4) + l));
          i += 2;
          changed = true;
        } else {
          
          throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
        } 
      } else {
        
        baos.write(ch);
      } 
    } 
    return changed ? StreamUtils.copyToString(baos, charset) : source;
  }













  
  @Nullable
  public static Locale parseLocale(String localeValue) {
    String[] tokens = tokenizeLocaleSource(localeValue);
    if (tokens.length == 1) {
      validateLocalePart(localeValue);
      Locale resolved = Locale.forLanguageTag(localeValue);
      if (resolved.getLanguage().length() > 0) {
        return resolved;
      }
    } 
    return parseLocaleTokens(localeValue, tokens);
  }














  
  @Nullable
  public static Locale parseLocaleString(String localeString) {
    return parseLocaleTokens(localeString, tokenizeLocaleSource(localeString));
  }
  
  private static String[] tokenizeLocaleSource(String localeSource) {
    return tokenizeToStringArray(localeSource, "_ ", false, false);
  }
  
  @Nullable
  private static Locale parseLocaleTokens(String localeString, String[] tokens) {
    String language = (tokens.length > 0) ? tokens[0] : "";
    String country = (tokens.length > 1) ? tokens[1] : "";
    validateLocalePart(language);
    validateLocalePart(country);
    
    String variant = "";
    if (tokens.length > 2) {

      
      int endIndexOfCountryCode = localeString.indexOf(country, language.length()) + country.length();
      
      variant = trimLeadingWhitespace(localeString.substring(endIndexOfCountryCode));
      if (variant.startsWith("_")) {
        variant = trimLeadingCharacter(variant, '_');
      }
    } 
    
    if (variant.isEmpty() && country.startsWith("#")) {
      variant = country;
      country = "";
    } 
    
    return (language.length() > 0) ? new Locale(language, country, variant) : null;
  }
  
  private static void validateLocalePart(String localePart) {
    for (int i = 0; i < localePart.length(); i++) {
      char ch = localePart.charAt(i);
      if (ch != ' ' && ch != '_' && ch != '-' && ch != '#' && !Character.isLetterOrDigit(ch)) {
        throw new IllegalArgumentException("Locale part \"" + localePart + "\" contains invalid characters");
      }
    } 
  }








  
  @Deprecated
  public static String toLanguageTag(Locale locale) {
    return locale.getLanguage() + (hasText(locale.getCountry()) ? ("-" + locale.getCountry()) : "");
  }







  
  public static TimeZone parseTimeZoneString(String timeZoneString) {
    TimeZone timeZone = TimeZone.getTimeZone(timeZoneString);
    if ("GMT".equals(timeZone.getID()) && !timeZoneString.startsWith("GMT"))
    {
      throw new IllegalArgumentException("Invalid time zone specification '" + timeZoneString + "'");
    }
    return timeZone;
  }












  
  public static String[] toStringArray(@Nullable Collection<String> collection) {
    return !CollectionUtils.isEmpty(collection) ? collection.<String>toArray(EMPTY_STRING_ARRAY) : EMPTY_STRING_ARRAY;
  }







  
  public static String[] toStringArray(@Nullable Enumeration<String> enumeration) {
    return (enumeration != null) ? toStringArray(Collections.list(enumeration)) : EMPTY_STRING_ARRAY;
  }








  
  public static String[] addStringToArray(@Nullable String[] array, String str) {
    if (ObjectUtils.isEmpty((Object[])array)) {
      return new String[] { str };
    }
    
    String[] newArr = new String[array.length + 1];
    System.arraycopy(array, 0, newArr, 0, array.length);
    newArr[array.length] = str;
    return newArr;
  }








  
  @Nullable
  public static String[] concatenateStringArrays(@Nullable String[] array1, @Nullable String[] array2) {
    if (ObjectUtils.isEmpty((Object[])array1)) {
      return array2;
    }
    if (ObjectUtils.isEmpty((Object[])array2)) {
      return array1;
    }
    
    String[] newArr = new String[array1.length + array2.length];
    System.arraycopy(array1, 0, newArr, 0, array1.length);
    System.arraycopy(array2, 0, newArr, array1.length, array2.length);
    return newArr;
  }












  
  @Deprecated
  @Nullable
  public static String[] mergeStringArrays(@Nullable String[] array1, @Nullable String[] array2) {
    if (ObjectUtils.isEmpty((Object[])array1)) {
      return array2;
    }
    if (ObjectUtils.isEmpty((Object[])array2)) {
      return array1;
    }
    
    List<String> result = new ArrayList<>(Arrays.asList(array1));
    for (String str : array2) {
      if (!result.contains(str)) {
        result.add(str);
      }
    } 
    return toStringArray(result);
  }





  
  public static String[] sortStringArray(String[] array) {
    if (ObjectUtils.isEmpty((Object[])array)) {
      return array;
    }
    
    Arrays.sort((Object[])array);
    return array;
  }






  
  public static String[] trimArrayElements(String[] array) {
    if (ObjectUtils.isEmpty((Object[])array)) {
      return array;
    }
    
    String[] result = new String[array.length];
    for (int i = 0; i < array.length; i++) {
      String element = array[i];
      result[i] = (element != null) ? element.trim() : null;
    } 
    return result;
  }






  
  public static String[] removeDuplicateStrings(String[] array) {
    if (ObjectUtils.isEmpty((Object[])array)) {
      return array;
    }
    
    Set<String> set = new LinkedHashSet<>(Arrays.asList(array));
    return toStringArray(set);
  }









  
  @Nullable
  public static String[] split(@Nullable String toSplit, @Nullable String delimiter) {
    if (!hasLength(toSplit) || !hasLength(delimiter)) {
      return null;
    }
    int offset = toSplit.indexOf(delimiter);
    if (offset < 0) {
      return null;
    }
    
    String beforeDelimiter = toSplit.substring(0, offset);
    String afterDelimiter = toSplit.substring(offset + delimiter.length());
    return new String[] { beforeDelimiter, afterDelimiter };
  }










  
  @Nullable
  public static Properties splitArrayElementsIntoProperties(String[] array, String delimiter) {
    return splitArrayElementsIntoProperties(array, delimiter, null);
  }
















  
  @Nullable
  public static Properties splitArrayElementsIntoProperties(String[] array, String delimiter, @Nullable String charsToDelete) {
    if (ObjectUtils.isEmpty((Object[])array)) {
      return null;
    }
    
    Properties result = new Properties();
    for (String element : array) {
      if (charsToDelete != null) {
        element = deleteAny(element, charsToDelete);
      }
      String[] splittedElement = split(element, delimiter);
      if (splittedElement != null)
      {
        
        result.setProperty(splittedElement[0].trim(), splittedElement[1].trim()); } 
    } 
    return result;
  }
















  
  public static String[] tokenizeToStringArray(@Nullable String str, String delimiters) {
    return tokenizeToStringArray(str, delimiters, true, true);
  }





















  
  public static String[] tokenizeToStringArray(@Nullable String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
    if (str == null) {
      return EMPTY_STRING_ARRAY;
    }
    
    StringTokenizer st = new StringTokenizer(str, delimiters);
    List<String> tokens = new ArrayList<>();
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if (trimTokens) {
        token = token.trim();
      }
      if (!ignoreEmptyTokens || token.length() > 0) {
        tokens.add(token);
      }
    } 
    return toStringArray(tokens);
  }













  
  public static String[] delimitedListToStringArray(@Nullable String str, @Nullable String delimiter) {
    return delimitedListToStringArray(str, delimiter, null);
  }

















  
  public static String[] delimitedListToStringArray(@Nullable String str, @Nullable String delimiter, @Nullable String charsToDelete) {
    if (str == null) {
      return EMPTY_STRING_ARRAY;
    }
    if (delimiter == null) {
      return new String[] { str };
    }
    
    List<String> result = new ArrayList<>();
    if (delimiter.isEmpty()) {
      for (int i = 0; i < str.length(); i++) {
        result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
      }
    } else {
      
      int pos = 0;
      int delPos;
      while ((delPos = str.indexOf(delimiter, pos)) != -1) {
        result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
        pos = delPos + delimiter.length();
      } 
      if (str.length() > 0 && pos <= str.length())
      {
        result.add(deleteAny(str.substring(pos), charsToDelete));
      }
    } 
    return toStringArray(result);
  }






  
  public static String[] commaDelimitedListToStringArray(@Nullable String str) {
    return delimitedListToStringArray(str, ",");
  }








  
  public static Set<String> commaDelimitedListToSet(@Nullable String str) {
    String[] tokens = commaDelimitedListToStringArray(str);
    return new LinkedHashSet<>(Arrays.asList(tokens));
  }











  
  public static String collectionToDelimitedString(@Nullable Collection<?> coll, String delim, String prefix, String suffix) {
    if (CollectionUtils.isEmpty(coll)) {
      return "";
    }
    
    int totalLength = coll.size() * (prefix.length() + suffix.length()) + (coll.size() - 1) * delim.length();
    for (Object element : coll) {
      totalLength += String.valueOf(element).length();
    }
    
    StringBuilder sb = new StringBuilder(totalLength);
    Iterator<?> it = coll.iterator();
    while (it.hasNext()) {
      sb.append(prefix).append(it.next()).append(suffix);
      if (it.hasNext()) {
        sb.append(delim);
      }
    } 
    return sb.toString();
  }







  
  public static String collectionToDelimitedString(@Nullable Collection<?> coll, String delim) {
    return collectionToDelimitedString(coll, delim, "", "");
  }






  
  public static String collectionToCommaDelimitedString(@Nullable Collection<?> coll) {
    return collectionToDelimitedString(coll, ",");
  }







  
  public static String arrayToDelimitedString(@Nullable Object[] arr, String delim) {
    if (ObjectUtils.isEmpty(arr)) {
      return "";
    }
    if (arr.length == 1) {
      return ObjectUtils.nullSafeToString(arr[0]);
    }
    
    StringJoiner sj = new StringJoiner(delim);
    for (Object elem : arr) {
      sj.add(String.valueOf(elem));
    }
    return sj.toString();
  }







  
  public static String arrayToCommaDelimitedString(@Nullable Object[] arr) {
    return arrayToDelimitedString(arr, ",");
  }
}
