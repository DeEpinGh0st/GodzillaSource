package org.mozilla.javascript;






















public class NativeJavaConstructor
  extends BaseFunction
{
  static final long serialVersionUID = -8149253217482668463L;
  MemberBox ctor;
  
  public NativeJavaConstructor(MemberBox ctor) {
    this.ctor = ctor;
  }



  
  public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    return NativeJavaClass.constructSpecific(cx, scope, args, this.ctor);
  }


  
  public String getFunctionName() {
    String sig = JavaMembers.liveConnectSignature(this.ctor.argTypes);
    return "<init>".concat(sig);
  }


  
  public String toString() {
    return "[JavaConstructor " + this.ctor.getName() + "]";
  }
}
