package org.yaml.snakeyaml.constructor;

import org.yaml.snakeyaml.SecClass;

















public class CustomClassLoaderConstructor
  extends Constructor
{
  private ClassLoader loader = CustomClassLoaderConstructor.class.getClassLoader();
  
  public CustomClassLoaderConstructor(ClassLoader cLoader) {
    this(Object.class, cLoader);
  }
  
  public CustomClassLoaderConstructor(Class<? extends Object> theRoot, ClassLoader theLoader) {
    super(theRoot);
    if (theLoader == null) {
      throw new NullPointerException("Loader must be provided.");
    }
    this.loader = theLoader;
  }

  
  protected Class<?> getClassForName(String name) throws ClassNotFoundException {
    return SecClass.forName(name, true, this.loader);
  }
}
