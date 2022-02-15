package org.springframework.cglib.util;

import java.util.Arrays;
import java.util.List;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Label;
import org.springframework.asm.Type;
import org.springframework.cglib.core.AbstractClassGenerator;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.cglib.core.KeyFactory;
import org.springframework.cglib.core.ObjectSwitchCallback;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;










public abstract class StringSwitcher
{
  private static final Type STRING_SWITCHER = TypeUtils.parseType("org.springframework.cglib.util.StringSwitcher");
  
  private static final Signature INT_VALUE = TypeUtils.parseSignature("int intValue(String)");
  
  private static final StringSwitcherKey KEY_FACTORY = (StringSwitcherKey)KeyFactory.create(StringSwitcherKey.class);






  
  public abstract int intValue(String paramString);





  
  public static StringSwitcher create(String[] strings, int[] ints, boolean fixedInput) {
    Generator gen = new Generator();
    gen.setStrings(strings);
    gen.setInts(ints);
    gen.setFixedInput(fixedInput);
    return gen.create();
  }










  
  public static class Generator
    extends AbstractClassGenerator
  {
    private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(StringSwitcher.class.getName());
    
    private String[] strings;
    private int[] ints;
    private boolean fixedInput;
    
    public Generator() {
      super(SOURCE);
    }





    
    public void setStrings(String[] strings) {
      this.strings = strings;
    }





    
    public void setInts(int[] ints) {
      this.ints = ints;
    }





    
    public void setFixedInput(boolean fixedInput) {
      this.fixedInput = fixedInput;
    }
    
    protected ClassLoader getDefaultClassLoader() {
      return getClass().getClassLoader();
    }



    
    public StringSwitcher create() {
      setNamePrefix(StringSwitcher.class.getName());
      Object key = StringSwitcher.KEY_FACTORY.newInstance(this.strings, this.ints, this.fixedInput);
      return (StringSwitcher)create(key);
    }
    
    public void generateClass(ClassVisitor v) throws Exception {
      ClassEmitter ce = new ClassEmitter(v);
      ce.begin_class(52, 1, 
          
          getClassName(), StringSwitcher
          .STRING_SWITCHER, null, "<generated>");

      
      EmitUtils.null_constructor(ce);
      final CodeEmitter e = ce.begin_method(1, StringSwitcher.INT_VALUE, null);
      e.load_arg(0);
      final List<String> stringList = Arrays.asList(this.strings);
      int style = this.fixedInput ? 2 : 1;
      EmitUtils.string_switch(e, this.strings, style, new ObjectSwitchCallback() {
            public void processCase(Object key, Label end) {
              e.push(StringSwitcher.Generator.this.ints[stringList.indexOf(key)]);
              e.return_value();
            }
            public void processDefault() {
              e.push(-1);
              e.return_value();
            }
          });
      e.end_method();
      ce.end_class();
    }
    
    protected Object firstInstance(Class type) {
      return ReflectUtils.newInstance(type);
    }
    
    protected Object nextInstance(Object instance) {
      return instance;
    }
  }
  
  static interface StringSwitcherKey {
    Object newInstance(String[] param1ArrayOfString, int[] param1ArrayOfint, boolean param1Boolean);
  }
}
