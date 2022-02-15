package org.springframework.cglib.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;















public class FastConstructor
  extends FastMember
{
  FastConstructor(FastClass fc, Constructor constructor) {
    super(fc, constructor, fc.getIndex(constructor.getParameterTypes()));
  }
  
  public Class[] getParameterTypes() {
    return ((Constructor)this.member).getParameterTypes();
  }
  
  public Class[] getExceptionTypes() {
    return ((Constructor)this.member).getExceptionTypes();
  }
  
  public Object newInstance() throws InvocationTargetException {
    return this.fc.newInstance(this.index, (Object[])null);
  }
  
  public Object newInstance(Object[] args) throws InvocationTargetException {
    return this.fc.newInstance(this.index, args);
  }
  
  public Constructor getJavaConstructor() {
    return (Constructor)this.member;
  }
}
