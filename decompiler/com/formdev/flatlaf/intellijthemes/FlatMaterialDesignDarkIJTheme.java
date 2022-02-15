package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatMaterialDesignDarkIJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Material Design Dark";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatMaterialDesignDarkIJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Material Design Dark", FlatMaterialDesignDarkIJTheme.class);
  }
  
  public FlatMaterialDesignDarkIJTheme() {
    super(Utils.loadTheme("MaterialTheme.theme.json"));
  }

  
  public String getName() {
    return "Material Design Dark";
  }
}
