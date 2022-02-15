package com.formdev.flatlaf.demo.intellijthemes;

import com.formdev.flatlaf.json.Json;
import com.formdev.flatlaf.util.StringUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




















class IJThemesManager
{
  final List<IJThemeInfo> bundledThemes = new ArrayList<>();
  final List<IJThemeInfo> moreThemes = new ArrayList<>();
  private final Map<File, Long> lastModifiedMap = new HashMap<>();
  
  void loadBundledThemes() {
    Map<String, Object> json;
    this.bundledThemes.clear();


    
    try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("themes.json"), StandardCharsets.UTF_8)) {
      json = (Map<String, Object>)Json.parse(reader);
    } catch (IOException ex) {
      ex.printStackTrace();
      
      return;
    } 
    
    for (Map.Entry<String, Object> e : json.entrySet()) {
      String resourceName = e.getKey();
      Map<String, String> value = (Map<String, String>)e.getValue();
      String name = value.get("name");
      boolean dark = Boolean.parseBoolean(value.get("dark"));
      String license = value.get("license");
      String licenseFile = value.get("licenseFile");
      String sourceCodeUrl = value.get("sourceCodeUrl");
      String sourceCodePath = value.get("sourceCodePath");
      
      this.bundledThemes.add(new IJThemeInfo(name, resourceName, dark, license, licenseFile, sourceCodeUrl, sourceCodePath, null, null));
    } 
  }

  
  void loadThemesFromDirectory() {
    File directory = (new File("")).getAbsoluteFile();
    
    File[] themeFiles = directory.listFiles((dir, name) -> 
        (name.endsWith(".theme.json") || name.endsWith(".properties")));
    
    if (themeFiles == null) {
      return;
    }
    this.lastModifiedMap.clear();
    this.lastModifiedMap.put(directory, Long.valueOf(directory.lastModified()));
    
    this.moreThemes.clear();
    for (File f : themeFiles) {
      String fname = f.getName();

      
      String name = fname.endsWith(".properties") ? StringUtils.removeTrailing(fname, ".properties") : StringUtils.removeTrailing(fname, ".theme.json");
      this.moreThemes.add(new IJThemeInfo(name, null, false, null, null, null, null, f, null));
      this.lastModifiedMap.put(f, Long.valueOf(f.lastModified()));
    } 
  }
  
  boolean hasThemesFromDirectoryChanged() {
    for (Map.Entry<File, Long> e : this.lastModifiedMap.entrySet()) {
      if (((File)e.getKey()).lastModified() != ((Long)e.getValue()).longValue())
        return true; 
    } 
    return false;
  }
}
