package com.formdev.flatlaf;

import com.formdev.flatlaf.ui.FlatEmptyBorder;
import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.DerivedColor;
import com.formdev.flatlaf.util.GrayFilter;
import com.formdev.flatlaf.util.HSLColor;
import com.formdev.flatlaf.util.StringUtils;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.logging.Level;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.InsetsUIResource;



































class UIDefaultsLoader
{
  private static final String TYPE_PREFIX = "{";
  private static final String TYPE_PREFIX_END = "}";
  private static final String VARIABLE_PREFIX = "@";
  private static final String PROPERTY_PREFIX = "$";
  private static final String OPTIONAL_PREFIX = "?";
  private static final String WILDCARD_PREFIX = "*.";
  
  static void loadDefaultsFromProperties(Class<?> lookAndFeelClass, List<FlatDefaultsAddon> addons, Properties additionalDefaults, boolean dark, UIDefaults defaults) {
    ArrayList<Class<?>> lafClasses = new ArrayList<>();
    Class<?> lafClass = lookAndFeelClass;
    for (; FlatLaf.class.isAssignableFrom(lafClass); 
      lafClass = lafClass.getSuperclass())
    {
      lafClasses.add(0, lafClass);
    }
    
    loadDefaultsFromProperties(lafClasses, addons, additionalDefaults, dark, defaults);
  }



  
  static void loadDefaultsFromProperties(List<Class<?>> lafClasses, List<FlatDefaultsAddon> addons, Properties additionalDefaults, boolean dark, UIDefaults defaults) {
    try {
      Properties properties = new Properties();
      for (Class<?> lafClass : lafClasses) {
        String propertiesName = '/' + lafClass.getName().replace('.', '/') + ".properties";
        try (InputStream in = lafClass.getResourceAsStream(propertiesName)) {
          if (in != null) {
            properties.load(in);
          }
        } 
      } 
      
      for (FlatDefaultsAddon addon : addons) {
        for (Class<?> lafClass : lafClasses) {
          try (InputStream in = addon.getDefaults(lafClass)) {
            if (in != null) {
              properties.load(in);
            }
          } 
        } 
      } 
      
      List<ClassLoader> addonClassLoaders = new ArrayList<>();
      for (FlatDefaultsAddon addon : addons) {
        ClassLoader addonClassLoader = addon.getClass().getClassLoader();
        if (!addonClassLoaders.contains(addonClassLoader)) {
          addonClassLoaders.add(addonClassLoader);
        }
      } 
      
      List<Object> customDefaultsSources = FlatLaf.getCustomDefaultsSources();
      int size = (customDefaultsSources != null) ? customDefaultsSources.size() : 0;
      for (int i = 0; i < size; i++) {
        Object source = customDefaultsSources.get(i);
        if (source instanceof String && i + 1 < size) {
          
          String packageName = (String)source;
          ClassLoader classLoader = (ClassLoader)customDefaultsSources.get(++i);

          
          if (classLoader != null && !addonClassLoaders.contains(classLoader)) {
            addonClassLoaders.add(classLoader);
          }
          packageName = packageName.replace('.', '/');
          if (classLoader == null) {
            classLoader = FlatLaf.class.getClassLoader();
          }
          for (Class<?> lafClass : lafClasses) {
            String propertiesName = packageName + '/' + lafClass.getSimpleName() + ".properties";
            try (InputStream in = classLoader.getResourceAsStream(propertiesName)) {
              if (in != null)
                properties.load(in); 
            } 
          } 
        } else if (source instanceof File) {
          
          File folder = (File)source;
          for (Class<?> lafClass : lafClasses) {
            File propertiesFile = new File(folder, lafClass.getSimpleName() + ".properties");
            if (!propertiesFile.isFile()) {
              continue;
            }
            try (InputStream in = new FileInputStream(propertiesFile)) {
              properties.load(in);
            } 
          } 
        } 
      } 

      
      if (additionalDefaults != null) {
        properties.putAll(additionalDefaults);
      }
      
      ArrayList<String> platformSpecificKeys = new ArrayList<>();
      for (Object okey : properties.keySet()) {
        String key = (String)okey;
        if (key.startsWith("[") && (key
          .startsWith("[win]") || key
          .startsWith("[mac]") || key
          .startsWith("[linux]") || key
          .startsWith("[light]") || key
          .startsWith("[dark]"))) {
          platformSpecificKeys.add(key);
        }
      } 

      
      if (!platformSpecificKeys.isEmpty()) {
        
        String lightOrDarkPrefix = dark ? "[dark]" : "[light]";
        for (String key : platformSpecificKeys) {
          if (key.startsWith(lightOrDarkPrefix)) {
            properties.put(key.substring(lightOrDarkPrefix.length()), properties.remove(key));
          }
        } 
        
        String platformPrefix = SystemInfo.isWindows ? "[win]" : (SystemInfo.isMacOS ? "[mac]" : (SystemInfo.isLinux ? "[linux]" : "[unknown]"));


        
        for (String key : platformSpecificKeys) {
          Object value = properties.remove(key);
          if (key.startsWith(platformPrefix)) {
            properties.put(key.substring(platformPrefix.length()), value);
          }
        } 
      } 
      
      HashMap<String, String> wildcards = new HashMap<>();
      Iterator<Map.Entry<Object, Object>> it = properties.entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry<Object, Object> e = it.next();
        String key = (String)e.getKey();
        if (key.startsWith("*.")) {
          wildcards.put(key.substring("*.".length()), (String)e.getValue());
          it.remove();
        } 
      } 

      
      for (Object key : defaults.keySet()) {
        int dot;
        if (!(key instanceof String) || properties
          .containsKey(key) || (
          dot = ((String)key).lastIndexOf('.')) < 0) {
          continue;
        }
        String wildcardKey = ((String)key).substring(dot + 1);
        String wildcardValue = wildcards.get(wildcardKey);
        if (wildcardValue != null) {
          properties.put(key, wildcardValue);
        }
      } 
      Function<String, String> propertiesGetter = key -> properties.getProperty(key);

      
      Function<String, String> resolver = value -> resolveValue(value, propertiesGetter);



      
      for (Map.Entry<Object, Object> e : properties.entrySet()) {
        String key = (String)e.getKey();
        if (key.startsWith("@")) {
          continue;
        }
        String value = resolveValue((String)e.getValue(), propertiesGetter);
        try {
          defaults.put(key, parseValue(key, value, null, resolver, addonClassLoaders));
        } catch (RuntimeException ex) {
          logParseError(Level.SEVERE, key, value, ex);
        } 
      } 
    } catch (IOException ex) {
      FlatLaf.LOG.log(Level.SEVERE, "FlatLaf: Failed to load properties files.", ex);
    } 
  }
  
  static void logParseError(Level level, String key, String value, RuntimeException ex) {
    FlatLaf.LOG.log(level, "FlatLaf: Failed to parse: '" + key + '=' + value + '\'', ex);
  }
  
  static String resolveValue(String value, Function<String, String> propertiesGetter) {
    value = value.trim();
    String value0 = value;
    
    if (value.startsWith("$")) {
      value = value.substring("$".length());
    } else if (!value.startsWith("@")) {
      return value;
    } 
    boolean optional = false;
    if (value.startsWith("?")) {
      value = value.substring("?".length());
      optional = true;
    } 
    
    String newValue = propertiesGetter.apply(value);
    if (newValue == null) {
      if (optional) {
        return "null";
      }
      throw new IllegalArgumentException("variable or property '" + value + "' not found");
    } 
    
    if (newValue.equals(value0)) {
      throw new IllegalArgumentException("endless recursion in variable or property '" + value + "'");
    }
    return resolveValue(newValue, propertiesGetter);
  }
  
  enum ValueType { UNKNOWN, STRING, BOOLEAN, CHARACTER, INTEGER, FLOAT, BORDER, ICON, INSETS, DIMENSION, COLOR,
    SCALEDINTEGER, SCALEDFLOAT, SCALEDINSETS, SCALEDDIMENSION, INSTANCE, CLASS, GRAYFILTER, NULL, LAZY; }
  
  private static ValueType[] tempResultValueType = new ValueType[1];
  
  static Object parseValue(String key, String value) {
    return parseValue(key, value, null, v -> v, Collections.emptyList());
  }


  
  static Object parseValue(String key, String value, ValueType[] resultValueType, Function<String, String> resolver, List<ClassLoader> addonClassLoaders) {
    if (resultValueType == null) {
      resultValueType = tempResultValueType;
    }
    value = value.trim();

    
    switch (value) { case "null":
        resultValueType[0] = ValueType.NULL; return null;
      case "false": resultValueType[0] = ValueType.BOOLEAN; return Boolean.valueOf(false);
      case "true": resultValueType[0] = ValueType.BOOLEAN; return Boolean.valueOf(true); }



    
    if (value.startsWith("lazy(") && value.endsWith(")")) {
      resultValueType[0] = ValueType.LAZY;
      String uiKey = value.substring(5, value.length() - 1).trim();
      return t -> lazyUIManagerGet(uiKey);
    } 


    
    ValueType valueType = ValueType.UNKNOWN;

    
    if (value.startsWith("#")) {
      valueType = ValueType.COLOR;
    } else if (value.startsWith("\"") && value.endsWith("\"")) {
      valueType = ValueType.STRING;
      value = value.substring(1, value.length() - 1);
    } else if (value.startsWith("{")) {
      int end = value.indexOf("}");
      if (end != -1) {
        try {
          String typeStr = value.substring("{".length(), end);
          valueType = ValueType.valueOf(typeStr.toUpperCase(Locale.ENGLISH));

          
          value = value.substring(end + "}".length());
        } catch (IllegalArgumentException illegalArgumentException) {}
      }
    } 



    
    if (valueType == ValueType.UNKNOWN) {
      if (key.endsWith("UI")) {
        valueType = ValueType.STRING;
      } else if (key.endsWith("Color") || (key
        .endsWith("ground") && (key
        .endsWith(".background") || key.endsWith("Background") || key
        .endsWith(".foreground") || key.endsWith("Foreground")))) {
        valueType = ValueType.COLOR;
      } else if (key.endsWith(".border") || key.endsWith("Border")) {
        valueType = ValueType.BORDER;
      } else if (key.endsWith(".icon") || key.endsWith("Icon")) {
        valueType = ValueType.ICON;
      } else if (key.endsWith(".margin") || key.endsWith(".padding") || key
        .endsWith("Margins") || key.endsWith("Insets")) {
        valueType = ValueType.INSETS;
      } else if (key.endsWith("Size")) {
        valueType = ValueType.DIMENSION;
      } else if (key.endsWith("Width") || key.endsWith("Height")) {
        valueType = ValueType.INTEGER;
      } else if (key.endsWith("Char")) {
        valueType = ValueType.CHARACTER;
      } else if (key.endsWith("grayFilter")) {
        valueType = ValueType.GRAYFILTER;
      } 
    }
    resultValueType[0] = valueType;

    
    switch (valueType) { case STRING:
        return value;
      case CHARACTER: return parseCharacter(value);
      case INTEGER: return parseInteger(value, true);
      case FLOAT: return parseFloat(value, true);
      case BORDER: return parseBorder(value, resolver, addonClassLoaders);
      case ICON: return parseInstance(value, addonClassLoaders);
      case INSETS: return parseInsets(value);
      case DIMENSION: return parseDimension(value);
      case COLOR: return parseColorOrFunction(value, resolver, true);
      case SCALEDINTEGER: return parseScaledInteger(value);
      case SCALEDFLOAT: return parseScaledFloat(value);
      case SCALEDINSETS: return parseScaledInsets(value);
      case SCALEDDIMENSION: return parseScaledDimension(value);
      case INSTANCE: return parseInstance(value, addonClassLoaders);
      case CLASS: return parseClass(value, addonClassLoaders);
      case GRAYFILTER: return parseGrayFilter(value); }


    
    Object color = parseColorOrFunction(value, resolver, false);
    if (color != null) {
      resultValueType[0] = ValueType.COLOR;
      return color;
    } 

    
    Integer integer = parseInteger(value, false);
    if (integer != null) {
      resultValueType[0] = ValueType.INTEGER;
      return integer;
    } 

    
    Float f = parseFloat(value, false);
    if (f != null) {
      resultValueType[0] = ValueType.FLOAT;
      return f;
    } 

    
    resultValueType[0] = ValueType.STRING;
    return value;
  }

  
  private static Object parseBorder(String value, Function<String, String> resolver, List<ClassLoader> addonClassLoaders) {
    if (value.indexOf(',') >= 0) {
      
      List<String> parts = split(value, ',');
      Insets insets = parseInsets(value);
      
      ColorUIResource lineColor = (parts.size() >= 5) ? (ColorUIResource)parseColorOrFunction(resolver.apply(parts.get(4)), resolver, true) : null;
      
      float lineThickness = (parts.size() >= 6) ? parseFloat(parts.get(5), true).floatValue() : 1.0F;
      
      return t -> (lineColor != null) ? new FlatLineBorder(insets, lineColor, lineThickness) : new FlatEmptyBorder(insets);
    } 



    
    return parseInstance(value, addonClassLoaders);
  }
  
  private static Object parseInstance(String value, List<ClassLoader> addonClassLoaders) {
    return t -> {
        try {
          return findClass(value, addonClassLoaders).newInstance();
        } catch (InstantiationException|IllegalAccessException|ClassNotFoundException ex) {
          FlatLaf.LOG.log(Level.SEVERE, "FlatLaf: Failed to instantiate '" + value + "'.", ex);
          return null;
        } 
      };
  }
  
  private static Object parseClass(String value, List<ClassLoader> addonClassLoaders) {
    return t -> {
        try {
          return findClass(value, addonClassLoaders);
        } catch (ClassNotFoundException ex) {
          FlatLaf.LOG.log(Level.SEVERE, "FlatLaf: Failed to find class '" + value + "'.", ex);
          return null;
        } 
      };
  }


  
  private static Class<?> findClass(String className, List<ClassLoader> addonClassLoaders) throws ClassNotFoundException {
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException ex) {
      
      for (ClassLoader addonClassLoader : addonClassLoaders) {
        try {
          return addonClassLoader.loadClass(className);
        } catch (ClassNotFoundException classNotFoundException) {}
      } 

      
      throw ex;
    } 
  }
  
  private static Insets parseInsets(String value) {
    List<String> numbers = split(value, ',');
    try {
      return new InsetsUIResource(
          Integer.parseInt(numbers.get(0)), 
          Integer.parseInt(numbers.get(1)), 
          Integer.parseInt(numbers.get(2)), 
          Integer.parseInt(numbers.get(3)));
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException("invalid insets '" + value + "'");
    } 
  }
  
  private static Dimension parseDimension(String value) {
    List<String> numbers = split(value, ',');
    try {
      return new DimensionUIResource(
          Integer.parseInt(numbers.get(0)), 
          Integer.parseInt(numbers.get(1)));
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException("invalid size '" + value + "'");
    } 
  }
  
  private static Object parseColorOrFunction(String value, Function<String, String> resolver, boolean reportError) {
    if (value.endsWith(")")) {
      return parseColorFunctions(value, resolver, reportError);
    }
    return parseColor(value, reportError);
  }
  
  static ColorUIResource parseColor(String value) {
    return parseColor(value, false);
  }
  
  private static ColorUIResource parseColor(String value, boolean reportError) {
    try {
      int rgba = parseColorRGBA(value);
      return ((rgba & 0xFF000000) == -16777216) ? new ColorUIResource(rgba) : new ColorUIResource(new Color(rgba, true));
    
    }
    catch (IllegalArgumentException ex) {
      if (reportError) {
        throw new IllegalArgumentException("invalid color '" + value + "'");
      }

      
      return null;
    } 
  }






  
  static int parseColorRGBA(String value) {
    int len = value.length();
    if ((len != 4 && len != 5 && len != 7 && len != 9) || value.charAt(0) != '#') {
      throw new IllegalArgumentException();
    }
    
    int n = 0;
    for (int i = 1; i < len; i++) {
      int digit; char ch = value.charAt(i);

      
      if (ch >= '0' && ch <= '9') {
        digit = ch - 48;
      } else if (ch >= 'a' && ch <= 'f') {
        digit = ch - 97 + 10;
      } else if (ch >= 'A' && ch <= 'F') {
        digit = ch - 65 + 10;
      } else {
        throw new IllegalArgumentException();
      } 
      n = n << 4 | digit;
    } 
    
    if (len <= 5) {
      
      int n1 = n & 0xF000;
      int n2 = n & 0xF00;
      int n3 = n & 0xF0;
      int n4 = n & 0xF;
      n = n1 << 16 | n1 << 12 | n2 << 12 | n2 << 8 | n3 << 8 | n3 << 4 | n4 << 4 | n4;
    } 
    
    return (len == 4 || len == 7) ? (0xFF000000 | n) : (n >> 8 & 0xFFFFFF | (n & 0xFF) << 24);
  }


  
  private static Object parseColorFunctions(String value, Function<String, String> resolver, boolean reportError) {
    int paramsStart = value.indexOf('(');
    if (paramsStart < 0) {
      if (reportError)
        throw new IllegalArgumentException("missing opening parenthesis in function '" + value + "'"); 
      return null;
    } 
    
    String function = value.substring(0, paramsStart).trim();
    List<String> params = splitFunctionParams(value.substring(paramsStart + 1, value.length() - 1), ',');
    if (params.isEmpty()) {
      throw new IllegalArgumentException("missing parameters in function '" + value + "'");
    }
    switch (function) { case "rgb":
        return parseColorRgbOrRgba(false, params, resolver, reportError);
      case "rgba": return parseColorRgbOrRgba(true, params, resolver, reportError);
      case "hsl": return parseColorHslOrHsla(false, params);
      case "hsla": return parseColorHslOrHsla(true, params);
      case "lighten": return parseColorHSLIncreaseDecrease(2, true, params, resolver, reportError);
      case "darken": return parseColorHSLIncreaseDecrease(2, false, params, resolver, reportError);
      case "saturate": return parseColorHSLIncreaseDecrease(1, true, params, resolver, reportError);
      case "desaturate": return parseColorHSLIncreaseDecrease(1, false, params, resolver, reportError);
      case "fadein": return parseColorHSLIncreaseDecrease(3, true, params, resolver, reportError);
      case "fadeout": return parseColorHSLIncreaseDecrease(3, false, params, resolver, reportError);
      case "fade": return parseColorFade(params, resolver, reportError);
      case "spin": return parseColorSpin(params, resolver, reportError); }

    
    throw new IllegalArgumentException("unknown color function '" + value + "'");
  }









  
  private static ColorUIResource parseColorRgbOrRgba(boolean hasAlpha, List<String> params, Function<String, String> resolver, boolean reportError) {
    if (hasAlpha && params.size() == 2) {


      
      String colorStr = params.get(0);
      int i = parseInteger(params.get(1), 0, 255, true).intValue();
      
      ColorUIResource color = (ColorUIResource)parseColorOrFunction(resolver.apply(colorStr), resolver, reportError);
      return new ColorUIResource(new Color((i & 0xFF) << 24 | color.getRGB() & 0xFFFFFF, true));
    } 
    
    int red = parseInteger(params.get(0), 0, 255, true).intValue();
    int green = parseInteger(params.get(1), 0, 255, true).intValue();
    int blue = parseInteger(params.get(2), 0, 255, true).intValue();
    int alpha = hasAlpha ? parseInteger(params.get(3), 0, 255, true).intValue() : 255;
    
    return hasAlpha ? new ColorUIResource(new Color(red, green, blue, alpha)) : new ColorUIResource(red, green, blue);
  }









  
  private static ColorUIResource parseColorHslOrHsla(boolean hasAlpha, List<String> params) {
    int hue = parseInteger(params.get(0), 0, 360, false).intValue();
    int saturation = parsePercentage(params.get(1));
    int lightness = parsePercentage(params.get(2));
    int alpha = hasAlpha ? parsePercentage(params.get(3)) : 100;
    
    float[] hsl = { hue, saturation, lightness };
    return new ColorUIResource(HSLColor.toRGB(hsl, alpha / 100.0F));
  }










  
  private static Object parseColorHSLIncreaseDecrease(int hslIndex, boolean increase, List<String> params, Function<String, String> resolver, boolean reportError) {
    String colorStr = params.get(0);
    int amount = parsePercentage(params.get(1));
    boolean relative = false;
    boolean autoInverse = false;
    boolean lazy = false;
    boolean derived = false;
    
    if (params.size() > 2) {
      String options = params.get(2);
      relative = options.contains("relative");
      autoInverse = options.contains("autoInverse");
      lazy = options.contains("lazy");
      derived = options.contains("derived");

      
      if (derived && !options.contains("noAutoInverse")) {
        autoInverse = true;
      }
    } 
    
    ColorFunctions.HSLIncreaseDecrease hSLIncreaseDecrease = new ColorFunctions.HSLIncreaseDecrease(hslIndex, increase, amount, relative, autoInverse);

    
    if (lazy) {
      return t -> {
          Object color = lazyUIManagerGet(colorStr);

          
          return (color instanceof Color) ? new ColorUIResource(ColorFunctions.applyFunctions((Color)color, new ColorFunctions.ColorFunction[] { function })) : null;
        };
    }

    
    return parseFunctionBaseColor(colorStr, (ColorFunctions.ColorFunction)hSLIncreaseDecrease, derived, resolver, reportError);
  }






  
  private static Object parseColorFade(List<String> params, Function<String, String> resolver, boolean reportError) {
    String colorStr = params.get(0);
    int amount = parsePercentage(params.get(1));
    boolean derived = false;
    
    if (params.size() > 2) {
      String options = params.get(2);
      derived = options.contains("derived");
    } 

    
    ColorFunctions.Fade fade = new ColorFunctions.Fade(amount);

    
    return parseFunctionBaseColor(colorStr, (ColorFunctions.ColorFunction)fade, derived, resolver, reportError);
  }






  
  private static Object parseColorSpin(List<String> params, Function<String, String> resolver, boolean reportError) {
    String colorStr = params.get(0);
    int amount = parseInteger(params.get(1), true).intValue();
    boolean derived = false;
    
    if (params.size() > 2) {
      String options = params.get(2);
      derived = options.contains("derived");
    } 

    
    ColorFunctions.HSLIncreaseDecrease hSLIncreaseDecrease = new ColorFunctions.HSLIncreaseDecrease(0, true, amount, false, false);

    
    return parseFunctionBaseColor(colorStr, (ColorFunctions.ColorFunction)hSLIncreaseDecrease, derived, resolver, reportError);
  }



  
  private static Object parseFunctionBaseColor(String colorStr, ColorFunctions.ColorFunction function, boolean derived, Function<String, String> resolver, boolean reportError) {
    String resolvedColorStr = resolver.apply(colorStr);
    ColorUIResource baseColor = (ColorUIResource)parseColorOrFunction(resolvedColorStr, resolver, reportError);
    if (baseColor == null) {
      return null;
    }
    
    Color newColor = ColorFunctions.applyFunctions(baseColor, new ColorFunctions.ColorFunction[] { function });
    
    if (derived) {
      ColorFunctions.ColorFunction[] functions;
      if (baseColor instanceof DerivedColor && resolvedColorStr == colorStr) {

        
        ColorFunctions.ColorFunction[] baseFunctions = ((DerivedColor)baseColor).getFunctions();
        functions = new ColorFunctions.ColorFunction[baseFunctions.length + 1];
        System.arraycopy(baseFunctions, 0, functions, 0, baseFunctions.length);
        functions[baseFunctions.length] = function;
      } else {
        functions = new ColorFunctions.ColorFunction[] { function };
      } 
      return new DerivedColor(newColor, functions);
    } 
    
    return new ColorUIResource(newColor);
  }
  private static int parsePercentage(String value) {
    int val;
    if (!value.endsWith("%")) {
      throw new NumberFormatException("invalid percentage '" + value + "'");
    }
    
    try {
      val = Integer.parseInt(value.substring(0, value.length() - 1));
    } catch (NumberFormatException ex) {
      throw new NumberFormatException("invalid percentage '" + value + "'");
    } 
    
    if (val < 0 || val > 100)
      throw new IllegalArgumentException("percentage out of range (0-100%) '" + value + "'"); 
    return val;
  }
  
  private static Character parseCharacter(String value) {
    if (value.length() != 1)
      throw new IllegalArgumentException("invalid character '" + value + "'"); 
    return Character.valueOf(value.charAt(0));
  }
  
  private static Integer parseInteger(String value, int min, int max, boolean allowPercentage) {
    if (allowPercentage && value.endsWith("%")) {
      int percent = parsePercentage(value);
      return Integer.valueOf(max * percent / 100);
    } 
    
    Integer integer = parseInteger(value, true);
    if (integer.intValue() < min || integer.intValue() > max)
      throw new NumberFormatException("integer '" + value + "' out of range (" + min + '-' + max + ')'); 
    return integer;
  }
  
  private static Integer parseInteger(String value, boolean reportError) {
    try {
      return Integer.valueOf(Integer.parseInt(value));
    } catch (NumberFormatException ex) {
      if (reportError) {
        throw new NumberFormatException("invalid integer '" + value + "'");
      }
      return null;
    } 
  }
  private static Float parseFloat(String value, boolean reportError) {
    try {
      return Float.valueOf(Float.parseFloat(value));
    } catch (NumberFormatException ex) {
      if (reportError) {
        throw new NumberFormatException("invalid float '" + value + "'");
      }
      return null;
    } 
  }
  private static UIDefaults.ActiveValue parseScaledInteger(String value) {
    int val = parseInteger(value, true).intValue();
    return t -> Integer.valueOf(UIScale.scale(val));
  }


  
  private static UIDefaults.ActiveValue parseScaledFloat(String value) {
    float val = parseFloat(value, true).floatValue();
    return t -> Float.valueOf(UIScale.scale(val));
  }


  
  private static UIDefaults.ActiveValue parseScaledInsets(String value) {
    Insets insets = parseInsets(value);
    return t -> UIScale.scale(insets);
  }


  
  private static UIDefaults.ActiveValue parseScaledDimension(String value) {
    Dimension dimension = parseDimension(value);
    return t -> UIScale.scale(dimension);
  }


  
  private static Object parseGrayFilter(String value) {
    List<String> numbers = split(value, ',');
    try {
      int brightness = Integer.parseInt(numbers.get(0));
      int contrast = Integer.parseInt(numbers.get(1));
      int alpha = Integer.parseInt(numbers.get(2));
      
      return t -> new GrayFilter(brightness, contrast, alpha);
    
    }
    catch (NumberFormatException ex) {
      throw new IllegalArgumentException("invalid gray filter '" + value + "'");
    } 
  }



  
  private static List<String> split(String str, char delim) {
    List<String> result = StringUtils.split(str, delim);

    
    int size = result.size();
    for (int i = 0; i < size; i++) {
      result.set(i, ((String)result.get(i)).trim());
    }
    return result;
  }




  
  private static List<String> splitFunctionParams(String str, char delim) {
    ArrayList<String> strs = new ArrayList<>();
    int nestLevel = 0;
    int start = 0;
    int strlen = str.length();
    for (int i = 0; i < strlen; i++) {
      char ch = str.charAt(i);
      if (ch == '(') {
        nestLevel++;
      } else if (ch == ')') {
        nestLevel--;
      } else if (nestLevel == 0 && ch == delim) {
        strs.add(str.substring(start, i).trim());
        start = i + 1;
      } 
    } 
    strs.add(str.substring(start).trim());
    
    return strs;
  }




  
  private static Object lazyUIManagerGet(String uiKey) {
    boolean optional = false;
    if (uiKey.startsWith("?")) {
      uiKey = uiKey.substring("?".length());
      optional = true;
    } 
    
    Object value = UIManager.get(uiKey);
    if (value == null && !optional)
      FlatLaf.LOG.log(Level.SEVERE, "FlatLaf: '" + uiKey + "' not found in UI defaults."); 
    return value;
  }
}
