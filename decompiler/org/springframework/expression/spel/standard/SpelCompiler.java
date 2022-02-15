package org.springframework.expression.spel.standard;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.asm.ClassWriter;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.CompiledExpression;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
















































public final class SpelCompiler
  implements Opcodes
{
  private static final int CLASSES_DEFINED_LIMIT = 100;
  private static final Log logger = LogFactory.getLog(SpelCompiler.class);


  
  private static final Map<ClassLoader, SpelCompiler> compilers = (Map<ClassLoader, SpelCompiler>)new ConcurrentReferenceHashMap();


  
  private volatile ChildClassLoader childClassLoader;

  
  private final AtomicInteger suffixId = new AtomicInteger(1);

  
  private SpelCompiler(@Nullable ClassLoader classloader) {
    this.childClassLoader = new ChildClassLoader(classloader);
  }










  
  @Nullable
  public CompiledExpression compile(SpelNodeImpl expression) {
    if (expression.isCompilable()) {
      if (logger.isDebugEnabled()) {
        logger.debug("SpEL: compiling " + expression.toStringAST());
      }
      Class<? extends CompiledExpression> clazz = createExpressionClass(expression);
      if (clazz != null) {
        try {
          return ReflectionUtils.accessibleConstructor(clazz, new Class[0]).newInstance(new Object[0]);
        }
        catch (Throwable ex) {
          throw new IllegalStateException("Failed to instantiate CompiledExpression", ex);
        } 
      }
    } 
    
    if (logger.isDebugEnabled()) {
      logger.debug("SpEL: unable to compile " + expression.toStringAST());
    }
    return null;
  }
  
  private int getNextSuffix() {
    return this.suffixId.incrementAndGet();
  }








  
  @Nullable
  private Class<? extends CompiledExpression> createExpressionClass(SpelNodeImpl expressionToCompile) {
    String className = "spel/Ex" + getNextSuffix();
    String evaluationContextClass = "org/springframework/expression/EvaluationContext";
    ClassWriter cw = new ExpressionClassWriter();
    cw.visit(52, 1, className, null, "org/springframework/expression/spel/CompiledExpression", null);

    
    MethodVisitor mv = cw.visitMethod(1, "<init>", "()V", null, null);
    mv.visitCode();
    mv.visitVarInsn(25, 0);
    mv.visitMethodInsn(183, "org/springframework/expression/spel/CompiledExpression", "<init>", "()V", false);
    
    mv.visitInsn(177);
    mv.visitMaxs(1, 1);
    mv.visitEnd();

    
    mv = cw.visitMethod(1, "getValue", "(Ljava/lang/Object;L" + evaluationContextClass + ";)Ljava/lang/Object;", null, new String[] { "org/springframework/expression/EvaluationException" });

    
    mv.visitCode();
    
    CodeFlow cf = new CodeFlow(className, cw);

    
    try {
      expressionToCompile.generateCode(mv, cf);
    }
    catch (IllegalStateException ex) {
      if (logger.isDebugEnabled()) {
        logger.debug(expressionToCompile.getClass().getSimpleName() + ".generateCode opted out of compilation: " + ex
            .getMessage());
      }
      return null;
    } 
    
    CodeFlow.insertBoxIfNecessary(mv, cf.lastDescriptor());
    if ("V".equals(cf.lastDescriptor())) {
      mv.visitInsn(1);
    }
    mv.visitInsn(176);
    
    mv.visitMaxs(0, 0);
    mv.visitEnd();
    cw.visitEnd();
    
    cf.finish();
    
    byte[] data = cw.toByteArray();

    
    return loadClass(StringUtils.replace(className, "/", "."), data);
  }










  
  private Class<? extends CompiledExpression> loadClass(String name, byte[] bytes) {
    ChildClassLoader ccl = this.childClassLoader;
    if (ccl.getClassesDefinedCount() >= 100) {
      synchronized (this) {
        ChildClassLoader currentCcl = this.childClassLoader;
        if (ccl == currentCcl) {
          
          ccl = new ChildClassLoader(ccl.getParent());
          this.childClassLoader = ccl;
        }
        else {
          
          ccl = currentCcl;
        } 
      } 
    }
    return (Class)ccl.defineClass(name, bytes);
  }








  
  public static SpelCompiler getCompiler(@Nullable ClassLoader classLoader) {
    ClassLoader clToUse = (classLoader != null) ? classLoader : ClassUtils.getDefaultClassLoader();
    
    SpelCompiler compiler = compilers.get(clToUse);
    if (compiler == null)
    {
      synchronized (compilers) {
        compiler = compilers.get(clToUse);
        if (compiler == null) {
          compiler = new SpelCompiler(clToUse);
          compilers.put(clToUse, compiler);
        } 
      } 
    }
    return compiler;
  }








  
  public static boolean compile(Expression expression) {
    return (expression instanceof SpelExpression && ((SpelExpression)expression).compileExpression());
  }





  
  public static void revertToInterpreted(Expression expression) {
    if (expression instanceof SpelExpression) {
      ((SpelExpression)expression).revertToInterpreted();
    }
  }



  
  private static class ChildClassLoader
    extends URLClassLoader
  {
    private static final URL[] NO_URLS = new URL[0];
    
    private final AtomicInteger classesDefinedCount = new AtomicInteger(0);
    
    public ChildClassLoader(@Nullable ClassLoader classLoader) {
      super(NO_URLS, classLoader);
    }
    
    public Class<?> defineClass(String name, byte[] bytes) {
      Class<?> clazz = defineClass(name, bytes, 0, bytes.length);
      this.classesDefinedCount.incrementAndGet();
      return clazz;
    }
    
    public int getClassesDefinedCount() {
      return this.classesDefinedCount.get();
    }
  }



  
  private class ExpressionClassWriter
    extends ClassWriter
  {
    public ExpressionClassWriter() {
      super(3);
    }

    
    protected ClassLoader getClassLoader() {
      return SpelCompiler.this.childClassLoader;
    }
  }
}
