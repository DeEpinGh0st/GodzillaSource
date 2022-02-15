package net.miginfocom.layout;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
































final class ResizeConstraint
  implements Externalizable
{
  static final Float WEIGHT_100 = Float.valueOf(100.0F);






  
  Float grow = null;


  
  int growPrio = 100;
  
  Float shrink = WEIGHT_100;
  
  int shrinkPrio = 100;


  
  public ResizeConstraint() {}

  
  ResizeConstraint(int shrinkPrio, Float shrinkWeight, int growPrio, Float growWeight) {
    this.shrinkPrio = shrinkPrio;
    this.shrink = shrinkWeight;
    this.growPrio = growPrio;
    this.grow = growWeight;
  }





  
  private Object readResolve() throws ObjectStreamException {
    return LayoutUtil.getSerializedObject(this);
  }


  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    LayoutUtil.setSerializedObject(this, LayoutUtil.readAsXML(in));
  }


  
  public void writeExternal(ObjectOutput out) throws IOException {
    if (getClass() == ResizeConstraint.class)
      LayoutUtil.writeAsXML(out, this); 
  }
}
