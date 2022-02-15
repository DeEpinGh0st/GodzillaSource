package org.fife.rsta.ac.css;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;


























class IconFactory
{
  private static IconFactory INSTANCE;
  private Map<String, Icon> iconMap = new HashMap<>();







  
  public static IconFactory get() {
    if (INSTANCE == null) {
      INSTANCE = new IconFactory();
    }
    return INSTANCE;
  }







  
  public Icon getIcon(String key) {
    Icon icon = this.iconMap.get(key);
    if (icon == null) {
      icon = loadIcon(key + ".gif");
      this.iconMap.put(key, icon);
    } 
    return icon;
  }







  
  private Icon loadIcon(String name) {
    URL res = getClass().getResource("img/" + name);
    if (res == null)
    {

      
      throw new IllegalArgumentException("icon not found: img/" + name);
    }
    return new ImageIcon(res);
  }
}
