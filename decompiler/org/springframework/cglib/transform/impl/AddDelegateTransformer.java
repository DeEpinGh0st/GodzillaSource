package org.springframework.cglib.transform.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.springframework.asm.Type;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.CodeGenerationException;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;
import org.springframework.cglib.transform.ClassEmitterTransformer;















public class AddDelegateTransformer
  extends ClassEmitterTransformer
{
  private static final String DELEGATE = "$CGLIB_DELEGATE";
  private static final Signature CSTRUCT_OBJECT = TypeUtils.parseSignature("void <init>(Object)");
  
  private Class[] delegateIf;
  
  private Class delegateImpl;
  private Type delegateType;
  
  public AddDelegateTransformer(Class[] delegateIf, Class delegateImpl) {
    try {
      delegateImpl.getConstructor(new Class[] { Object.class });
      this.delegateIf = delegateIf;
      this.delegateImpl = delegateImpl;
      this.delegateType = Type.getType(delegateImpl);
    } catch (NoSuchMethodException e) {
      throw new CodeGenerationException(e);
    } 
  }

  
  public void begin_class(int version, int access, String className, Type superType, Type[] interfaces, String sourceFile) {
    if (!TypeUtils.isInterface(access)) {
      
      Type[] all = TypeUtils.add(interfaces, TypeUtils.getTypes(this.delegateIf));
      super.begin_class(version, access, className, superType, all, sourceFile);
      
      declare_field(130, "$CGLIB_DELEGATE", this.delegateType, null);


      
      for (int i = 0; i < this.delegateIf.length; i++) {
        Method[] methods = this.delegateIf[i].getMethods();
        for (int j = 0; j < methods.length; j++) {
          if (Modifier.isAbstract(methods[j].getModifiers())) {
            addDelegate(methods[j]);
          }
        } 
      } 
    } else {
      super.begin_class(version, access, className, superType, interfaces, sourceFile);
    } 
  }
  
  public CodeEmitter begin_method(int access, Signature sig, Type[] exceptions) {
    CodeEmitter e = super.begin_method(access, sig, exceptions);
    if (sig.getName().equals("<init>"))
      return new CodeEmitter(e)
        {
          public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            if (this.transformInit && opcode == 183) {
              load_this();
              new_instance(AddDelegateTransformer.this.delegateType);
              dup();
              load_this();
              invoke_constructor(AddDelegateTransformer.this.delegateType, AddDelegateTransformer.CSTRUCT_OBJECT);
              putfield("$CGLIB_DELEGATE");
              this.transformInit = false;
            } 
          }
          private boolean transformInit = true;
        }; 
    return e;
  }

  
  private void addDelegate(Method m) {
    try {
      Method delegate = this.delegateImpl.getMethod(m.getName(), m.getParameterTypes());
      if (!delegate.getReturnType().getName().equals(m.getReturnType().getName())) {
        throw new IllegalArgumentException("Invalid delegate signature " + delegate);
      }
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new CodeGenerationException(noSuchMethodException);
    } 
    
    Signature sig = ReflectUtils.getSignature(m);
    Type[] exceptions = TypeUtils.getTypes(m.getExceptionTypes());
    CodeEmitter e = super.begin_method(1, sig, exceptions);
    e.load_this();
    e.getfield("$CGLIB_DELEGATE");
    e.load_args();
    e.invoke_virtual(this.delegateType, sig);
    e.return_value();
    e.end_method();
  }
}
