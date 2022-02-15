package com.formdev.flatlaf.intellijthemes;

import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.LookAndFeel;
























public class FlatCobalt2IJTheme
  extends IntelliJTheme.ThemeLaf
{
  public static final String NAME = "Cobalt 2";
  
  public static boolean install() {
    try {
      return install((LookAndFeel)new FlatCobalt2IJTheme());
    } catch (RuntimeException ex) {
      return false;
    } 
  }
  
  public static void installLafInfo() {
    installLafInfo("Cobalt 2", FlatCobalt2IJTheme.class);
  }
  
  public FlatCobalt2IJTheme() {
    super(Utils.loadTheme("Cobalt_2.theme.json"));
  }

  
  public String getName() {
    return "Cobalt 2";
  }
}
