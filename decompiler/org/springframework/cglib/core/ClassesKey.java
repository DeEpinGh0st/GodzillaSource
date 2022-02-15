package org.springframework.cglib.core;















public class ClassesKey
{
  private static final Key FACTORY = (Key)KeyFactory.create(Key.class);







  
  public static Object create(Object[] array) {
    return FACTORY.newInstance((Object[])classNames(array));
  }
  
  private static String[] classNames(Object[] objects) {
    if (objects == null) {
      return null;
    }
    String[] classNames = new String[objects.length];
    for (int i = 0; i < objects.length; i++) {
      Object object = objects[i];
      if (object != null) {
        Class<?> aClass = object.getClass();
        classNames[i] = (aClass == null) ? null : aClass.getName();
      } 
    } 
    return classNames;
  }
  
  static interface Key {
    Object newInstance(Object[] param1ArrayOfObject);
  }
}
