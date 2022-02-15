package javassist.tools.reflect;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.Loader;
import javassist.NotFoundException;














































































































public class Loader
  extends Loader
{
  protected Reflection reflection;
  
  public static void main(String[] args) throws Throwable {
    Loader cl = new Loader();
    cl.run(args);
  }




  
  public Loader() throws CannotCompileException, NotFoundException {
    delegateLoadingOf("javassist.tools.reflect.Loader");
    
    this.reflection = new Reflection();
    ClassPool pool = ClassPool.getDefault();
    addTranslator(pool, this.reflection);
  }




















  
  public boolean makeReflective(String clazz, String metaobject, String metaclass) throws CannotCompileException, NotFoundException {
    return this.reflection.makeReflective(clazz, metaobject, metaclass);
  }
}
