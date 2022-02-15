package org.yaml.snakeyaml.util;

























public class EnumUtils
{
  public static <T extends Enum<T>> T findEnumInsensitiveCase(Class<T> enumType, String name) {
    for (Enum enum_ : (Enum[])enumType.getEnumConstants()) {
      if (enum_.name().compareToIgnoreCase(name) == 0) {
        return (T)enum_;
      }
    } 
    throw new IllegalArgumentException("No enum constant " + enumType.getCanonicalName() + "." + name);
  }
}
