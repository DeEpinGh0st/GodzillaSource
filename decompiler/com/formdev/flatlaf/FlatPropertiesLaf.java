package com.formdev.flatlaf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;






























public class FlatPropertiesLaf
  extends FlatLaf
{
  private final String name;
  private final String baseTheme;
  private final boolean dark;
  private final Properties properties;
  
  public FlatPropertiesLaf(String name, File propertiesFile) throws IOException {
    this(name, new FileInputStream(propertiesFile));
  }


  
  public FlatPropertiesLaf(String name, InputStream in) throws IOException {
    this(name, loadProperties(in));
  }


  
  private static Properties loadProperties(InputStream in) throws IOException {
    Properties properties = new Properties();
    try (InputStream in2 = in) {
      properties.load(in2);
    } 
    return properties;
  }
  
  public FlatPropertiesLaf(String name, Properties properties) {
    this.name = name;
    this.properties = properties;
    
    this.baseTheme = properties.getProperty("@baseTheme", "light");
    this.dark = ("dark".equalsIgnoreCase(this.baseTheme) || "darcula".equalsIgnoreCase(this.baseTheme));
  }

  
  public String getName() {
    return this.name;
  }

  
  public String getDescription() {
    return this.name;
  }

  
  public boolean isDark() {
    return this.dark;
  }
  
  public Properties getProperties() {
    return this.properties;
  }

  
  protected ArrayList<Class<?>> getLafClassesForDefaultsLoading() {
    ArrayList<Class<?>> lafClasses = new ArrayList<>();
    lafClasses.add(FlatLaf.class);
    switch (this.baseTheme.toLowerCase())
    
    { default:
        lafClasses.add(FlatLightLaf.class);















        
        return lafClasses;case "dark": lafClasses.add(FlatDarkLaf.class); return lafClasses;case "intellij": lafClasses.add(FlatLightLaf.class); lafClasses.add(FlatIntelliJLaf.class); return lafClasses;case "darcula": break; }  lafClasses.add(FlatDarkLaf.class); lafClasses.add(FlatDarculaLaf.class); return lafClasses;
  }

  
  protected Properties getAdditionalDefaults() {
    return this.properties;
  }
}
