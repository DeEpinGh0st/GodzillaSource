package org.fife.rsta.ac.demo;

import java.io.File;
import javax.swing.filechooser.FileFilter;
























class ExtensionFileFilter
  extends FileFilter
{
  private String desc;
  private String ext;
  
  public ExtensionFileFilter(String desc, String ext) {
    this.desc = desc;
    this.ext = ext;
  }





  
  public boolean accept(File f) {
    return (f.isDirectory() || f.getName().endsWith(this.ext));
  }





  
  public String getDescription() {
    return this.desc + " (*." + this.ext + ")";
  }
}
