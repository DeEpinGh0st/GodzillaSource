package org.springframework.cglib.proxy;

import java.util.Iterator;
import java.util.List;
import org.springframework.asm.Type;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.MethodInfo;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;









class FixedValueGenerator
  implements CallbackGenerator
{
  public static final FixedValueGenerator INSTANCE = new FixedValueGenerator();
  
  private static final Type FIXED_VALUE = TypeUtils.parseType("org.springframework.cglib.proxy.FixedValue");
  
  private static final Signature LOAD_OBJECT = TypeUtils.parseSignature("Object loadObject()");
  
  public void generate(ClassEmitter ce, CallbackGenerator.Context context, List methods) {
    for (Iterator<MethodInfo> it = methods.iterator(); it.hasNext(); ) {
      MethodInfo method = it.next();
      CodeEmitter e = context.beginMethod(ce, method);
      context.emitCallback(e, context.getIndex(method));
      e.invoke_interface(FIXED_VALUE, LOAD_OBJECT);
      e.unbox_or_zero(e.getReturnType());
      e.return_value();
      e.end_method();
    } 
  }
  
  public void generateStatic(CodeEmitter e, CallbackGenerator.Context context, List methods) {}
}
