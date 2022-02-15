package com.formdev.flatlaf.demo;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatPropertiesLaf;
import com.formdev.flatlaf.IntelliJTheme;
import com.formdev.flatlaf.demo.intellijthemes.IJThemesPanel;
import com.formdev.flatlaf.util.StringUtils;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.prefs.Preferences;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;





















public class DemoPrefs
{
  public static final String KEY_LAF = "laf";
  public static final String KEY_LAF_THEME = "lafTheme";
  public static final String RESOURCE_PREFIX = "res:";
  public static final String FILE_PREFIX = "file:";
  public static final String THEME_UI_KEY = "__FlatLaf.demo.theme";
  private static Preferences state;
  
  public static Preferences getState() {
    return state;
  }
  
  public static void init(String rootPath) {
    state = Preferences.userRoot().node(rootPath);
  }

  
  public static void initLaf(String[] args) {
    try {
      if (args.length > 0) {
        UIManager.setLookAndFeel(args[0]);
      } else {
        String lafClassName = state.get("laf", FlatLightLaf.class.getName());
        if (IntelliJTheme.ThemeLaf.class.getName().equals(lafClassName)) {
          String theme = state.get("lafTheme", "");
          if (theme.startsWith("res:")) {
            IntelliJTheme.install(IJThemesPanel.class.getResourceAsStream("/com/formdev/flatlaf/intellijthemes/themes/" + theme.substring("res:".length())));
          } else if (theme.startsWith("file:")) {
            FlatLaf.install((LookAndFeel)IntelliJTheme.createLaf(new FileInputStream(theme.substring("file:".length()))));
          } else {
            FlatLightLaf.install();
          } 
          
          if (!theme.isEmpty()) {
            UIManager.getLookAndFeelDefaults().put("__FlatLaf.demo.theme", theme);
          }
        } else if (FlatPropertiesLaf.class.getName().equals(lafClassName)) {
          String theme = state.get("lafTheme", "");
          if (theme.startsWith("file:")) {
            File themeFile = new File(theme.substring("file:".length()));
            String themeName = StringUtils.removeTrailing(themeFile.getName(), ".properties");
            FlatLaf.install((LookAndFeel)new FlatPropertiesLaf(themeName, themeFile));
          } else {
            FlatLightLaf.install();
          } 
          
          if (!theme.isEmpty()) {
            UIManager.getLookAndFeelDefaults().put("__FlatLaf.demo.theme", theme);
          }
        } else {
          UIManager.setLookAndFeel(lafClassName);
        } 
      } 
    } catch (Throwable ex) {
      ex.printStackTrace();

      
      FlatLightLaf.install();
    } 

    
    UIManager.addPropertyChangeListener(e -> {
          if ("lookAndFeel".equals(e.getPropertyName()))
            state.put("laf", UIManager.getLookAndFeel().getClass().getName()); 
        });
  }
}
