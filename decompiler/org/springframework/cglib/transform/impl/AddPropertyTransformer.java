package org.springframework.cglib.transform.impl;

import java.util.Map;
import org.springframework.asm.Type;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.cglib.core.TypeUtils;
import org.springframework.cglib.transform.ClassEmitterTransformer;












public class AddPropertyTransformer
  extends ClassEmitterTransformer
{
  private final String[] names;
  private final Type[] types;
  
  public AddPropertyTransformer(Map props) {
    int size = props.size();
    this.names = (String[])props.keySet().toArray((Object[])new String[size]);
    this.types = new Type[size];
    for (int i = 0; i < size; i++) {
      this.types[i] = (Type)props.get(this.names[i]);
    }
  }
  
  public AddPropertyTransformer(String[] names, Type[] types) {
    this.names = names;
    this.types = types;
  }
  
  public void end_class() {
    if (!TypeUtils.isAbstract(getAccess())) {
      EmitUtils.add_properties((ClassEmitter)this, this.names, this.types);
    }
    super.end_class();
  }
}
