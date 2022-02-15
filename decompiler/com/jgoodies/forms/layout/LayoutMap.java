package com.jgoodies.forms.layout;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.forms.util.LayoutStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
























































































































public final class LayoutMap
{
  private static final char VARIABLE_PREFIX_CHAR = '$';
  private static final Map<String, String> COLUMN_ALIASES = new HashMap<String, String>();






  
  private static final Map<String, String> ROW_ALIASES = new HashMap<String, String>();





  
  private static LayoutMap root = null;





  
  private final LayoutMap parent;





  
  private final Map<String, String> columnMap;





  
  private final Map<String, String> columnMapCache;





  
  private final Map<String, String> rowMap;





  
  private final Map<String, String> rowMapCache;





  
  public LayoutMap() {
    this(getRoot());
  }






  
  public LayoutMap(LayoutMap parent) {
    this.parent = parent;
    this.columnMap = new HashMap<String, String>();
    this.rowMap = new HashMap<String, String>();
    this.columnMapCache = new HashMap<String, String>();
    this.rowMapCache = new HashMap<String, String>();
  }









  
  public static synchronized LayoutMap getRoot() {
    if (root == null) {
      root = createRoot();
    }
    return root;
  }















  
  public boolean columnContainsKey(String key) {
    String resolvedKey = resolveColumnKey(key);
    return (this.columnMap.containsKey(resolvedKey) || (this.parent != null && this.parent.columnContainsKey(resolvedKey)));
  }
















  
  public String columnGet(String key) {
    String resolvedKey = resolveColumnKey(key);
    String cachedValue = this.columnMapCache.get(resolvedKey);
    if (cachedValue != null) {
      return cachedValue;
    }
    String value = this.columnMap.get(resolvedKey);
    if (value == null && this.parent != null) {
      value = this.parent.columnGet(resolvedKey);
    }
    if (value == null) {
      return null;
    }
    String expandedString = expand(value, true);
    this.columnMapCache.put(resolvedKey, expandedString);
    return expandedString;
  }





















  
  public String columnPut(String key, String value) {
    Preconditions.checkNotNull(value, "The column expression value must not be null.");
    String resolvedKey = resolveColumnKey(key);
    this.columnMapCache.clear();
    return this.columnMap.put(resolvedKey, value.toLowerCase(Locale.ENGLISH));
  }



  
  public String columnPut(String key, ColumnSpec value) {
    return columnPut(key, value.encode());
  }

  
  public String columnPut(String key, Size value) {
    return columnPut(key, value.encode());
  }


















  
  public String columnRemove(String key) {
    String resolvedKey = resolveColumnKey(key);
    this.columnMapCache.clear();
    return this.columnMap.remove(resolvedKey);
  }















  
  public boolean rowContainsKey(String key) {
    String resolvedKey = resolveRowKey(key);
    return (this.rowMap.containsKey(resolvedKey) || (this.parent != null && this.parent.rowContainsKey(resolvedKey)));
  }
















  
  public String rowGet(String key) {
    String resolvedKey = resolveRowKey(key);
    String cachedValue = this.rowMapCache.get(resolvedKey);
    if (cachedValue != null) {
      return cachedValue;
    }
    String value = this.rowMap.get(resolvedKey);
    if (value == null && this.parent != null) {
      value = this.parent.rowGet(resolvedKey);
    }
    if (value == null) {
      return null;
    }
    String expandedString = expand(value, false);
    this.rowMapCache.put(resolvedKey, expandedString);
    return expandedString;
  }

  
  public String rowPut(String key, String value) {
    Preconditions.checkNotNull(value, "The row expression value must not be null.");
    String resolvedKey = resolveRowKey(key);
    this.rowMapCache.clear();
    return this.rowMap.put(resolvedKey, value.toLowerCase(Locale.ENGLISH));
  }






















  
  public String rowPut(String key, RowSpec value) {
    return rowPut(key, value.encode());
  }

  
  public String rowPut(String key, Size value) {
    return rowPut(key, value.encode());
  }


















  
  public String rowRemove(String key) {
    String resolvedKey = resolveRowKey(key);
    this.rowMapCache.clear();
    return this.rowMap.remove(resolvedKey);
  }










  
  public String toString() {
    StringBuffer buffer = new StringBuffer(super.toString());
    buffer.append("\n  Column associations:");
    for (Map.Entry<String, String> entry : this.columnMap.entrySet()) {
      buffer.append("\n    ");
      buffer.append(entry.getKey());
      buffer.append("->");
      buffer.append(entry.getValue());
    } 
    buffer.append("\n  Row associations:");
    for (Map.Entry<String, String> entry : this.rowMap.entrySet()) {
      buffer.append("\n    ");
      buffer.append(entry.getKey());
      buffer.append("->");
      buffer.append(entry.getValue());
    } 
    return buffer.toString();
  }



  
  String expand(String expression, boolean horizontal) {
    int cursor = 0;
    int start = expression.indexOf('$', cursor);
    if (start == -1) {
      return expression;
    }
    StringBuffer buffer = new StringBuffer();
    while (true) {
      buffer.append(expression.substring(cursor, start));
      String variableName = nextVariableName(expression, start);
      buffer.append(expansion(variableName, horizontal));
      cursor = start + variableName.length() + 1;
      start = expression.indexOf('$', cursor);
      if (start == -1) {
        buffer.append(expression.substring(cursor));
        return buffer.toString();
      } 
    } 
  }
  private static String nextVariableName(String expression, int start) {
    int length = expression.length();
    if (length <= start) {
      FormSpecParser.fail(expression, start, "Missing variable name after variable char '$'.");
    }
    if (expression.charAt(start + 1) == '{') {
      int i = expression.indexOf('}', start + 1);
      if (i == -1) {
        FormSpecParser.fail(expression, start, "Missing closing brace '}' for variable.");
      }
      return expression.substring(start + 1, i + 1);
    } 
    int end = start + 1;
    
    while (end < length && Character.isUnicodeIdentifierPart(expression.charAt(end))) {
      end++;
    }
    return expression.substring(start + 1, end);
  }

  
  private String expansion(String variableName, boolean horizontal) {
    String key = stripBraces(variableName);
    String expansion = horizontal ? columnGet(key) : rowGet(key);
    if (expansion == null) {
      String orientation = horizontal ? "column" : "row";
      throw new IllegalArgumentException("Unknown " + orientation + " layout variable \"" + key + "\"");
    } 
    return expansion;
  }

  
  private static String stripBraces(String variableName) {
    return (variableName.charAt(0) == '{') ? variableName.substring(1, variableName.length() - 1) : variableName;
  }





  
  private static String resolveColumnKey(String key) {
    Preconditions.checkNotNull(key, "The column key must not be null.");
    String lowercaseKey = key.toLowerCase(Locale.ENGLISH);
    String defaultKey = COLUMN_ALIASES.get(lowercaseKey);
    return (defaultKey == null) ? lowercaseKey : defaultKey;
  }

  
  private static String resolveRowKey(String key) {
    Preconditions.checkNotNull(key, "The row key must not be null.");
    String lowercaseKey = key.toLowerCase(Locale.ENGLISH);
    String defaultKey = ROW_ALIASES.get(lowercaseKey);
    return (defaultKey == null) ? lowercaseKey : defaultKey;
  }

  
  private static LayoutMap createRoot() {
    LayoutMap map = new LayoutMap(null);

    
    map.columnPut("label-component-gap", new String[] { "lcg", "lcgap" }, FormSpecs.LABEL_COMPONENT_GAP_COLSPEC);


    
    map.columnPut("related-gap", new String[] { "rg", "rgap" }, FormSpecs.RELATED_GAP_COLSPEC);


    
    map.columnPut("unrelated-gap", new String[] { "ug", "ugap" }, FormSpecs.UNRELATED_GAP_COLSPEC);


    
    map.columnPut("button", new String[] { "b" }, FormSpecs.BUTTON_COLSPEC);


    
    map.columnPut("growing-button", new String[] { "gb" }, FormSpecs.GROWING_BUTTON_COLSPEC);


    
    map.columnPut("dialog-margin", new String[] { "dm", "dmargin" }, ColumnSpec.createGap(LayoutStyle.getCurrent().getDialogMarginX()));


    
    map.columnPut("tabbed-dialog-margin", new String[] { "tdm", "tdmargin" }, ColumnSpec.createGap(LayoutStyle.getCurrent().getTabbedDialogMarginX()));


    
    map.columnPut("glue", FormSpecs.GLUE_COLSPEC.toShortString());



    
    map.rowPut("label-component-gap", new String[] { "lcg", "lcgap" }, FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC);


    
    map.rowPut("related-gap", new String[] { "rg", "rgap" }, FormSpecs.RELATED_GAP_ROWSPEC);


    
    map.rowPut("unrelated-gap", new String[] { "ug", "ugap" }, FormSpecs.UNRELATED_GAP_ROWSPEC);


    
    map.rowPut("narrow-line-gap", new String[] { "nlg", "nlgap" }, FormSpecs.NARROW_LINE_GAP_ROWSPEC);


    
    map.rowPut("line-gap", new String[] { "lg", "lgap" }, FormSpecs.LINE_GAP_ROWSPEC);


    
    map.rowPut("paragraph-gap", new String[] { "pg", "pgap" }, FormSpecs.PARAGRAPH_GAP_ROWSPEC);


    
    map.rowPut("dialog-margin", new String[] { "dm", "dmargin" }, RowSpec.createGap(LayoutStyle.getCurrent().getDialogMarginY()));


    
    map.rowPut("tabbed-dialog-margin", new String[] { "tdm", "tdmargin" }, RowSpec.createGap(LayoutStyle.getCurrent().getTabbedDialogMarginY()));


    
    map.rowPut("button", new String[] { "b" }, FormSpecs.BUTTON_ROWSPEC);


    
    map.rowPut("glue", FormSpecs.GLUE_ROWSPEC);


    
    return map;
  }

  
  private void columnPut(String key, String[] aliases, ColumnSpec value) {
    ensureLowerCase(key);
    columnPut(key, value);
    for (String aliase : aliases) {
      ensureLowerCase(aliase);
      COLUMN_ALIASES.put(aliase, key);
    } 
  }

  
  private void rowPut(String key, String[] aliases, RowSpec value) {
    ensureLowerCase(key);
    rowPut(key, value);
    for (String aliase : aliases) {
      ensureLowerCase(aliase);
      ROW_ALIASES.put(aliase, key);
    } 
  }

  
  private static void ensureLowerCase(String str) {
    String lowerCase = str.toLowerCase(Locale.ENGLISH);
    if (!lowerCase.equals(str))
      throw new IllegalArgumentException("The string \"" + str + "\" should be lower case."); 
  }
}
