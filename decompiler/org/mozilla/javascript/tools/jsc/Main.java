package org.mozilla.javascript.tools.jsc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.optimizer.ClassCompiler;
import org.mozilla.javascript.tools.SourceReader;
import org.mozilla.javascript.tools.ToolErrorReporter;

public class Main
{
  private boolean printHelp;
  private ToolErrorReporter reporter;
  private CompilerEnvirons compilerEnv;
  private ClassCompiler compiler;
  private String targetName;
  private String targetPackage;
  private String destinationDir;
  private String characterEncoding;
  
  public static void main(String[] args) {
    Main main = new Main();
    args = main.processOptions(args);
    if (args == null) {
      if (main.printHelp) {
        System.out.println(ToolErrorReporter.getMessage("msg.jsc.usage", Main.class.getName()));
        
        System.exit(0);
      } 
      System.exit(1);
    } 
    if (!main.reporter.hasReportedError()) {
      main.processSource(args);
    }
  }

  
  public Main() {
    this.reporter = new ToolErrorReporter(true);
    this.compilerEnv = new CompilerEnvirons();
    this.compilerEnv.setErrorReporter((ErrorReporter)this.reporter);
    this.compiler = new ClassCompiler(this.compilerEnv);
  }





  
  public String[] processOptions(String[] args) {
    this.targetPackage = "";
    this.compilerEnv.setGenerateDebugInfo(false);
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      if (!arg.startsWith("-")) {
        int tail = args.length - i;
        if (this.targetName != null && tail > 1) {
          addError("msg.multiple.js.to.file", this.targetName);
          return null;
        } 
        String[] result = new String[tail];
        for (int j = 0; j != tail; j++) {
          result[j] = args[i + j];
        }
        return result;
      } 
      if (arg.equals("-help") || arg.equals("-h") || arg.equals("--help")) {

        
        this.printHelp = true;
        return null;
      } 

      
      try { if (arg.equals("-version") && ++i < args.length)
        { int version = Integer.parseInt(args[i]);
          this.compilerEnv.setLanguageVersion(version);
           }
        
        else if ((arg.equals("-opt") || arg.equals("-O")) && ++i < args.length)
        
        { 
          int optLevel = Integer.parseInt(args[i]);
          this.compilerEnv.setOptimizationLevel(optLevel);




          
           }
        
        else if (arg.equals("-nosource"))
        { this.compilerEnv.setGeneratingSource(false);
           }
        
        else if (arg.equals("-debug") || arg.equals("-g"))
        { this.compilerEnv.setGenerateDebugInfo(true);
           }
        
        else if (arg.equals("-main-method-class") && ++i < args.length)
        { this.compiler.setMainMethodClass(args[i]);
           }
        
        else if (arg.equals("-encoding") && ++i < args.length)
        { this.characterEncoding = args[i];
           }
        
        else if (arg.equals("-o") && ++i < args.length)
        { String name = args[i];
          int end = name.length();
          if (end == 0 || !Character.isJavaIdentifierStart(name.charAt(0))) {

            
            addError("msg.invalid.classfile.name", name);
          } else {
            String str;
            for (int j = 1; j < end; j++) {
              char c = name.charAt(j);
              if (!Character.isJavaIdentifierPart(c)) {
                if (c == '.')
                {
                  if (j == end - 6 && name.endsWith(".class")) {
                    str = name.substring(0, j);
                    break;
                  } 
                }
                addError("msg.invalid.classfile.name", str);
                break;
              } 
            } 
            this.targetName = str;
          }  }
        else
        { if (arg.equals("-observe-instruction-count")) {
            this.compilerEnv.setGenerateObserverCount(true);
          }
          if (arg.equals("-package") && ++i < args.length)
          { String pkg = args[i];
            int end = pkg.length();
            for (int j = 0; j != end; ) {
              char c = pkg.charAt(j);
              if (Character.isJavaIdentifierStart(c)) {
                for (; ++j != end; j++) {
                  c = pkg.charAt(j);
                  if (!Character.isJavaIdentifierPart(c)) {
                    break;
                  }
                } 
                if (j == end) {
                  break;
                }
                if (c == '.' && j != end - 1) {
                  j++; continue;
                } 
              } 
              addError("msg.package.name", this.targetPackage);
              return null;
            } 
            this.targetPackage = pkg;
             }
          
          else if (arg.equals("-extends") && ++i < args.length)
          { Class<?> superClass; String targetExtends = args[i];
            
            try {
              superClass = Class.forName(targetExtends);
            } catch (ClassNotFoundException e) {
              throw new Error(e.toString());
            } 
            this.compiler.setTargetExtends(superClass);
             }
          
          else if (arg.equals("-implements") && ++i < args.length)
          
          { String targetImplements = args[i];
            StringTokenizer st = new StringTokenizer(targetImplements, ",");
            
            List<Class<?>> list = new ArrayList<Class<?>>();
            while (st.hasMoreTokens()) {
              String className = st.nextToken();
              try {
                list.add(Class.forName(className));
              } catch (ClassNotFoundException e) {
                throw new Error(e.toString());
              } 
            } 
            Class<?>[] implementsClasses = (Class[])list.<Class<?>[]>toArray((Class<?>[][])new Class[list.size()]);
            this.compiler.setTargetImplements(implementsClasses);
             }
          
          else if (arg.equals("-d") && ++i < args.length)
          { this.destinationDir = args[i]; }
          else
          
          { badUsage(arg);
            return null; }  }  }
      catch (NumberFormatException numberFormatException) { badUsage(args[i]); return null; }
    
    }  p(ToolErrorReporter.getMessage("msg.no.file"));
    return null;
  }


  
  private static void badUsage(String s) {
    System.err.println(ToolErrorReporter.getMessage("msg.jsc.bad.usage", Main.class.getName(), s));
  }






  
  public void processSource(String[] filenames) {
    for (int i = 0; i != filenames.length; i++) {
      String filename = filenames[i];
      if (!filename.endsWith(".js")) {
        addError("msg.extension.not.js", filename);
        return;
      } 
      File f = new File(filename);
      String source = readSource(f);
      if (source == null)
        return; 
      String mainClassName = this.targetName;
      if (mainClassName == null) {
        String name = f.getName();
        String nojs = name.substring(0, name.length() - 3);
        mainClassName = getClassName(nojs);
      } 
      if (this.targetPackage.length() != 0) {
        mainClassName = this.targetPackage + "." + mainClassName;
      }
      
      Object[] compiled = this.compiler.compileToClassFiles(source, filename, 1, mainClassName);

      
      if (compiled == null || compiled.length == 0) {
        return;
      }
      
      File targetTopDir = null;
      if (this.destinationDir != null) {
        targetTopDir = new File(this.destinationDir);
      } else {
        String parent = f.getParent();
        if (parent != null) {
          targetTopDir = new File(parent);
        }
      } 
      for (int j = 0; j != compiled.length; j += 2) {
        String className = (String)compiled[j];
        byte[] bytes = (byte[])compiled[j + 1];
        File outfile = getOutputFile(targetTopDir, className);
        try {
          FileOutputStream os = new FileOutputStream(outfile);



        
        }
        catch (IOException ioe) {
          addFormatedError(ioe.toString());
        } 
      } 
    } 
  }

  
  private String readSource(File f) {
    String absPath = f.getAbsolutePath();
    if (!f.isFile()) {
      addError("msg.jsfile.not.found", absPath);
      return null;
    } 
    try {
      return (String)SourceReader.readFileOrUrl(absPath, true, this.characterEncoding);
    }
    catch (FileNotFoundException ex) {
      addError("msg.couldnt.open", absPath);
    } catch (IOException ioe) {
      addFormatedError(ioe.toString());
    } 
    return null;
  }

  
  private File getOutputFile(File parentDir, String className) {
    String path = className.replace('.', File.separatorChar);
    path = path.concat(".class");
    File f = new File(parentDir, path);
    String dirPath = f.getParent();
    if (dirPath != null) {
      File dir = new File(dirPath);
      if (!dir.exists()) {
        dir.mkdirs();
      }
    } 
    return f;
  }






  
  String getClassName(String name) {
    char[] s = new char[name.length() + 1];
    
    int j = 0;
    
    if (!Character.isJavaIdentifierStart(name.charAt(0))) {
      s[j++] = '_';
    }
    for (int i = 0; i < name.length(); i++, j++) {
      char c = name.charAt(i);
      if (Character.isJavaIdentifierPart(c)) {
        s[j] = c;
      } else {
        s[j] = '_';
      } 
    } 
    return (new String(s)).trim();
  }
  
  private static void p(String s) {
    System.out.println(s);
  }

  
  private void addError(String messageId, String arg) {
    String msg;
    if (arg == null) {
      msg = ToolErrorReporter.getMessage(messageId);
    } else {
      msg = ToolErrorReporter.getMessage(messageId, arg);
    } 
    addFormatedError(msg);
  }

  
  private void addFormatedError(String message) {
    this.reporter.error(message, null, -1, null, -1);
  }
}
