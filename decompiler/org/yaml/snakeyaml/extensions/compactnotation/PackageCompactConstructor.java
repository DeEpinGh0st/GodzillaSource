package org.yaml.snakeyaml.extensions.compactnotation;

import org.yaml.snakeyaml.SecClass;














public class PackageCompactConstructor
  extends CompactConstructor
{
  private String packageName;
  
  public PackageCompactConstructor(String packageName) {
    this.packageName = packageName;
  }

  
  protected Class<?> getClassForName(String name) throws ClassNotFoundException {
    if (name.indexOf('.') < 0) {
      try {
        Class<?> clazz = SecClass.forName(this.packageName + "." + name);
        return clazz;
      } catch (ClassNotFoundException classNotFoundException) {}
    }

    
    return super.getClassForName(name);
  }
}
