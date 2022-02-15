package org.springframework.core.annotation;

import java.lang.annotation.Annotation;




































@FunctionalInterface
public interface AnnotationFilter
{
  public static final AnnotationFilter PLAIN = packages(new String[] { "java.lang", "org.springframework.lang" });




  
  public static final AnnotationFilter JAVA = packages(new String[] { "java", "javax" });




  
  public static final AnnotationFilter ALL = new AnnotationFilter()
    {
      public boolean matches(Annotation annotation) {
        return true;
      }
      
      public boolean matches(Class<?> type) {
        return true;
      }
      
      public boolean matches(String typeName) {
        return true;
      }
      
      public String toString() {
        return "All annotations filtered";
      }
    };








  
  @Deprecated
  public static final AnnotationFilter NONE = new AnnotationFilter()
    {
      public boolean matches(Annotation annotation) {
        return false;
      }
      
      public boolean matches(Class<?> type) {
        return false;
      }
      
      public boolean matches(String typeName) {
        return false;
      }
      
      public String toString() {
        return "No annotation filtering";
      }
    };






  
  default boolean matches(Annotation annotation) {
    return matches(annotation.annotationType());
  }





  
  default boolean matches(Class<?> type) {
    return matches(type.getName());
  }






  
  boolean matches(String paramString);






  
  static AnnotationFilter packages(String... packages) {
    return new PackagesAnnotationFilter(packages);
  }
}
