package com.formdev.flatlaf;

import com.formdev.flatlaf.util.StringUtils;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;




















class LinuxFontPolicy
{
  static Font getFont() {
    return SystemInfo.isKDE ? getKDEFont() : getGnomeFont();
  }





  
  private static Font getGnomeFont() {
    Object fontName = Toolkit.getDefaultToolkit().getDesktopProperty("gnome.Gtk/FontName");
    if (!(fontName instanceof String)) {
      fontName = "sans 10";
    }
    String family = "";
    int style = 0;
    int size = 10;
    
    StringTokenizer st = new StringTokenizer((String)fontName);
    while (st.hasMoreTokens()) {
      String word = st.nextToken();
      
      if (word.equalsIgnoreCase("italic")) {
        style |= 0x2; continue;
      }  if (word.equalsIgnoreCase("bold")) {
        style |= 0x1; continue;
      }  if (Character.isDigit(word.charAt(0))) {
        try {
          size = Integer.parseInt(word);
        } catch (NumberFormatException numberFormatException) {}
        
        continue;
      } 
      family = family.isEmpty() ? word : (family + ' ' + word);
    } 


    
    if (family.startsWith("Ubuntu") && !SystemInfo.isJetBrainsJVM && 
      
      !FlatSystemProperties.getBoolean("flatlaf.useUbuntuFont", false)) {
      family = "Liberation Sans";
    }
    
    double dsize = size * getGnomeFontScale();
    size = (int)(dsize + 0.5D);
    if (size < 1) {
      size = 1;
    }
    
    String logicalFamily = mapFcName(family.toLowerCase());
    if (logicalFamily != null) {
      family = logicalFamily;
    }
    return createFont(family, style, size, dsize);
  }
  
  private static Font createFont(String family, int style, int size, double dsize) {
    Font font = FlatLaf.createCompositeFont(family, style, size);

    
    font = font.deriveFont(style, (float)dsize);
    
    return font;
  }

  
  private static double getGnomeFontScale() {
    if (isSystemScaling()) {
      return 1.3333333333333333D;
    }

    
    Object value = Toolkit.getDefaultToolkit().getDesktopProperty("gnome.Xft/DPI");
    if (value instanceof Integer) {
      int dpi = ((Integer)value).intValue() / 1024;
      if (dpi == -1)
        dpi = 96; 
      if (dpi < 50)
        dpi = 50; 
      return dpi / 72.0D;
    } 
    return GraphicsEnvironment.getLocalGraphicsEnvironment()
      .getDefaultScreenDevice().getDefaultConfiguration()
      .getNormalizingTransform().getScaleY();
  }




  
  private static String mapFcName(String name) {
    switch (name) { case "sans":
        return "sansserif";
      case "sans-serif": return "sansserif";
      case "serif": return "serif";
      case "monospace": return "monospaced"; }
    
    return null;
  }












  
  private static Font getKDEFont() {
    List<String> kdeglobals = readConfig("kdeglobals");
    List<String> kcmfonts = readConfig("kcmfonts");
    
    String generalFont = getConfigEntry(kdeglobals, "General", "font");
    String forceFontDPI = getConfigEntry(kcmfonts, "General", "forceFontDPI");
    
    String family = "sansserif";
    int style = 0;
    int size = 10;
    
    if (generalFont != null) {
      List<String> strs = StringUtils.split(generalFont, ',');
      try {
        family = strs.get(0);
        size = Integer.parseInt(strs.get(1));
        if ("75".equals(strs.get(4)))
          style |= 0x1; 
        if ("1".equals(strs.get(5)))
          style |= 0x2; 
      } catch (RuntimeException ex) {
        FlatLaf.LOG.log(Level.CONFIG, "FlatLaf: Failed to parse 'font=" + generalFont + "'.", ex);
      } 
    } 

    
    int dpi = 96;
    if (forceFontDPI != null && !isSystemScaling()) {
      try {
        dpi = Integer.parseInt(forceFontDPI);
        if (dpi <= 0)
          dpi = 96; 
        if (dpi < 50)
          dpi = 50; 
      } catch (NumberFormatException ex) {
        FlatLaf.LOG.log(Level.CONFIG, "FlatLaf: Failed to parse 'forceFontDPI=" + forceFontDPI + "'.", ex);
      } 
    }

    
    double fontScale = dpi / 72.0D;
    double dsize = size * fontScale;
    size = (int)(dsize + 0.5D);
    if (size < 1) {
      size = 1;
    }
    return createFont(family, style, size, dsize);
  }
  
  private static List<String> readConfig(String filename) {
    File userHome = new File(System.getProperty("user.home"));

    
    String[] configDirs = { ".config", ".kde4/share/config", ".kde/share/config" };



    
    File file = null;
    for (String configDir : configDirs) {
      file = new File(userHome, configDir + "/" + filename);
      if (file.isFile())
        break; 
    } 
    if (!file.isFile()) {
      return Collections.emptyList();
    }
    
    ArrayList<String> lines = new ArrayList<>(200);
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line = null;
      while ((line = reader.readLine()) != null)
        lines.add(line); 
    } catch (IOException ex) {
      FlatLaf.LOG.log(Level.CONFIG, "FlatLaf: Failed to read '" + filename + "'.", ex);
    } 
    return lines;
  }
  
  private static String getConfigEntry(List<String> config, String group, String key) {
    int groupLength = group.length();
    int keyLength = key.length();
    boolean inGroup = false;
    for (String line : config) {
      if (!inGroup) {
        if (line.length() >= groupLength + 2 && line
          .charAt(0) == '[' && line
          .charAt(groupLength + 1) == ']' && line
          .indexOf(group) == 1)
        {
          inGroup = true; } 
        continue;
      } 
      if (line.startsWith("[")) {
        return null;
      }
      if (line.length() >= keyLength + 2 && line
        .charAt(keyLength) == '=' && line
        .startsWith(key))
      {
        return line.substring(keyLength + 1);
      }
    } 
    
    return null;
  }






  
  private static boolean isSystemScaling() {
    GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    return (UIScale.getSystemScaleFactor(gc) > 1.0D);
  }
}
