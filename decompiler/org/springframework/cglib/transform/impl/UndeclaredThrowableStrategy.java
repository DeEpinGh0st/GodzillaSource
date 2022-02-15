package org.springframework.cglib.transform.impl;

import org.springframework.cglib.core.ClassGenerator;
import org.springframework.cglib.core.DefaultGeneratorStrategy;
import org.springframework.cglib.core.TypeUtils;
import org.springframework.cglib.transform.ClassTransformer;
import org.springframework.cglib.transform.MethodFilter;
import org.springframework.cglib.transform.MethodFilterTransformer;
import org.springframework.cglib.transform.TransformingClassGenerator;






























public class UndeclaredThrowableStrategy
  extends DefaultGeneratorStrategy
{
  private Class wrapper;
  
  public UndeclaredThrowableStrategy(Class wrapper) {
    this.wrapper = wrapper;
  }
  
  private static final MethodFilter TRANSFORM_FILTER = new MethodFilter() {
      public boolean accept(int access, String name, String desc, String signature, String[] exceptions) {
        return (!TypeUtils.isPrivate(access) && name.indexOf('$') < 0);
      }
    };
  
  protected ClassGenerator transform(ClassGenerator cg) throws Exception {
    UndeclaredThrowableTransformer undeclaredThrowableTransformer = new UndeclaredThrowableTransformer(this.wrapper);
    MethodFilterTransformer methodFilterTransformer = new MethodFilterTransformer(TRANSFORM_FILTER, (ClassTransformer)undeclaredThrowableTransformer);
    return (ClassGenerator)new TransformingClassGenerator(cg, (ClassTransformer)methodFilterTransformer);
  }
}
