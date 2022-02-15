package org.mozilla.javascript.tools.shell;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.serialize.ScriptableInputStream;
import org.mozilla.javascript.serialize.ScriptableOutputStream;
import org.mozilla.javascript.tools.ToolErrorReporter;

public class Global extends ImporterTopLevel {
  static final long serialVersionUID = 4029130780977538005L;
  NativeArray history;
  private String[] prompts = new String[] { "js> ", "  > " }; boolean attemptedJLineLoad; private ShellConsole console; private InputStream inStream; private PrintStream outStream; private PrintStream errStream;
  private boolean sealedStdLib = false;
  boolean initialized;
  private QuitAction quitAction;
  private HashMap<String, String> doctestCanonicalizations;
  
  public Global() {}
  
  public Global(Context cx) {
    init(cx);
  }
  
  public boolean isInitialized() {
    return this.initialized;
  }




  
  public void initQuitAction(QuitAction quitAction) {
    if (quitAction == null)
      throw new IllegalArgumentException("quitAction is null"); 
    if (this.quitAction != null) {
      throw new IllegalArgumentException("The method is once-call.");
    }
    this.quitAction = quitAction;
  }

  
  public void init(ContextFactory factory) {
    factory.call(new ContextAction()
        {
          public Object run(Context cx) {
            Global.this.init(cx);
            return null;
          }
        });
  }



  
  public void init(Context cx) {
    initStandardObjects(cx, this.sealedStdLib);
    String[] names = { "defineClass", "deserialize", "doctest", "gc", "help", "load", "loadClass", "print", "quit", "readFile", "readUrl", "runCommand", "seal", "serialize", "spawn", "sync", "toint32", "version" };


















    
    defineFunctionProperties(names, Global.class, 2);



    
    Environment.defineClass((ScriptableObject)this);
    Environment environment = new Environment((ScriptableObject)this);
    defineProperty("environment", environment, 2);

    
    this.history = (NativeArray)cx.newArray((Scriptable)this, 0);
    defineProperty("history", this.history, 2);
    
    this.initialized = true;
  }

  
  public Require installRequire(Context cx, List<String> modulePath, boolean sandboxed) {
    RequireBuilder rb = new RequireBuilder();
    rb.setSandboxed(sandboxed);
    List<URI> uris = new ArrayList<URI>();
    if (modulePath != null) {
      for (String path : modulePath) {
        try {
          URI uri = new URI(path);
          if (!uri.isAbsolute())
          {
            uri = (new File(path)).toURI().resolve("");
          }
          if (!uri.toString().endsWith("/"))
          {
            
            uri = new URI(uri + "/");
          }
          uris.add(uri);
        } catch (URISyntaxException usx) {
          throw new RuntimeException(usx);
        } 
      } 
    }
    rb.setModuleScriptProvider((ModuleScriptProvider)new SoftCachingModuleScriptProvider((ModuleSourceProvider)new UrlModuleSourceProvider(uris, null)));

    
    Require require = rb.createRequire(cx, (Scriptable)this);
    require.install((Scriptable)this);
    return require;
  }







  
  public static void help(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
    PrintStream out = getInstance(funObj).getOut();
    out.println(ToolErrorReporter.getMessage("msg.help"));
  }


  
  public static void gc(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
    System.gc();
  }












  
  public static Object print(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
    PrintStream out = getInstance(funObj).getOut();
    for (int i = 0; i < args.length; i++) {
      if (i > 0) {
        out.print(" ");
      }
      
      String s = Context.toString(args[i]);
      
      out.print(s);
    } 
    out.println();
    return Context.getUndefinedValue();
  }








  
  public static void quit(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
    Global global = getInstance(funObj);
    if (global.quitAction != null) {
      int exitCode = (args.length == 0) ? 0 : ScriptRuntime.toInt32(args[0]);
      
      global.quitAction.quit(cx, exitCode);
    } 
  }







  
  public static double version(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
    double result = cx.getLanguageVersion();
    if (args.length > 0) {
      double d = Context.toNumber(args[0]);
      cx.setLanguageVersion((int)d);
    } 
    return result;
  }








  
  public static void load(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
    for (Object arg : args) {
      String file = Context.toString(arg);
      try {
        Main.processFile(cx, thisObj, file);
      } catch (IOException ioex) {
        String msg = ToolErrorReporter.getMessage("msg.couldnt.read.source", file, ioex.getMessage());
        
        throw Context.reportRuntimeError(msg);
      } catch (VirtualMachineError ex) {
        
        ex.printStackTrace();
        String msg = ToolErrorReporter.getMessage("msg.uncaughtJSException", ex.toString());
        
        throw Context.reportRuntimeError(msg);
      } 
    } 
  }


















  
  public static void defineClass(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws IllegalAccessException, InstantiationException, InvocationTargetException {
    Class<?> clazz = getClass(args);
    if (!Scriptable.class.isAssignableFrom(clazz)) {
      throw reportRuntimeError("msg.must.implement.Scriptable");
    }
    ScriptableObject.defineClass(thisObj, clazz);
  }

















  
  public static void loadClass(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws IllegalAccessException, InstantiationException {
    Class<?> clazz = getClass(args);
    if (!Script.class.isAssignableFrom(clazz)) {
      throw reportRuntimeError("msg.must.implement.Script");
    }
    Script script = (Script)clazz.newInstance();
    script.exec(cx, thisObj);
  }
  
  private static Class<?> getClass(Object[] args) {
    if (args.length == 0) {
      throw reportRuntimeError("msg.expected.string.arg");
    }
    Object arg0 = args[0];
    if (arg0 instanceof Wrapper) {
      Object wrapped = ((Wrapper)arg0).unwrap();
      if (wrapped instanceof Class)
        return (Class)wrapped; 
    } 
    String className = Context.toString(args[0]);
    try {
      return Class.forName(className);
    }
    catch (ClassNotFoundException cnfe) {
      throw reportRuntimeError("msg.class.not.found", className);
    } 
  }



  
  public static void serialize(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws IOException {
    if (args.length < 2) {
      throw Context.reportRuntimeError("Expected an object to serialize and a filename to write the serialization to");
    }

    
    Object obj = args[0];
    String filename = Context.toString(args[1]);
    FileOutputStream fos = new FileOutputStream(filename);
    Scriptable scope = ScriptableObject.getTopLevelScope(thisObj);
    ScriptableOutputStream out = new ScriptableOutputStream(fos, scope);
    out.writeObject(obj);
    out.close();
  }



  
  public static Object deserialize(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws IOException, ClassNotFoundException {
    if (args.length < 1) {
      throw Context.reportRuntimeError("Expected a filename to read the serialization from");
    }
    
    String filename = Context.toString(args[0]);
    FileInputStream fis = new FileInputStream(filename);
    Scriptable scope = ScriptableObject.getTopLevelScope(thisObj);
    ScriptableInputStream scriptableInputStream = new ScriptableInputStream(fis, scope);
    Object deserialized = scriptableInputStream.readObject();
    scriptableInputStream.close();
    return Context.toObject(deserialized, scope);
  }
  
  public String[] getPrompts(Context cx) {
    if (ScriptableObject.hasProperty((Scriptable)this, "prompts")) {
      Object promptsJS = ScriptableObject.getProperty((Scriptable)this, "prompts");
      
      if (promptsJS instanceof Scriptable) {
        Scriptable s = (Scriptable)promptsJS;
        if (ScriptableObject.hasProperty(s, 0) && ScriptableObject.hasProperty(s, 1)) {

          
          Object elem0 = ScriptableObject.getProperty(s, 0);
          if (elem0 instanceof Function) {
            elem0 = ((Function)elem0).call(cx, (Scriptable)this, s, new Object[0]);
          }
          
          this.prompts[0] = Context.toString(elem0);
          Object elem1 = ScriptableObject.getProperty(s, 1);
          if (elem1 instanceof Function) {
            elem1 = ((Function)elem1).call(cx, (Scriptable)this, s, new Object[0]);
          }
          
          this.prompts[1] = Context.toString(elem1);
        } 
      } 
    } 
    return this.prompts;
  }






  
  public static Object doctest(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
    if (args.length == 0) {
      return Boolean.FALSE;
    }
    String session = Context.toString(args[0]);
    Global global = getInstance(funObj);
    return new Integer(global.runDoctest(cx, (Scriptable)global, session, (String)null, 0));
  }


  
  public int runDoctest(Context cx, Scriptable scope, String session, String sourceName, int lineNumber) {
    this.doctestCanonicalizations = new HashMap<String, String>();
    String[] lines = session.split("\r\n?|\n");
    String prompt0 = this.prompts[0].trim();
    String prompt1 = this.prompts[1].trim();
    int testCount = 0;
    int i = 0;
    while (i < lines.length && !lines[i].trim().startsWith(prompt0)) {
      i++;
    }
    while (i < lines.length) {
      String inputString = lines[i].trim().substring(prompt0.length());
      inputString = inputString + "\n";
      i++;
      while (i < lines.length && lines[i].trim().startsWith(prompt1)) {
        inputString = inputString + lines[i].trim().substring(prompt1.length());
        inputString = inputString + "\n";
        i++;
      } 
      String expectedString = "";
      while (i < lines.length && !lines[i].trim().startsWith(prompt0)) {

        
        expectedString = expectedString + lines[i] + "\n";
        i++;
      } 
      PrintStream savedOut = getOut();
      PrintStream savedErr = getErr();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ByteArrayOutputStream err = new ByteArrayOutputStream();
      setOut(new PrintStream(out));
      setErr(new PrintStream(err));
      String resultString = "";
      ErrorReporter savedErrorReporter = cx.getErrorReporter();
      cx.setErrorReporter((ErrorReporter)new ToolErrorReporter(false, getErr()));
      try {
        testCount++;
        Object result = cx.evaluateString(scope, inputString, "doctest input", 1, null);
        
        if (result != Context.getUndefinedValue() && (!(result instanceof Function) || !inputString.trim().startsWith("function")))
        {

          
          resultString = Context.toString(result);
        }
      } catch (RhinoException e) {
        ToolErrorReporter.reportException(cx.getErrorReporter(), e);
      } finally {
        setOut(savedOut);
        setErr(savedErr);
        cx.setErrorReporter(savedErrorReporter);
        resultString = resultString + err.toString() + out.toString();
      } 
      if (!doctestOutputMatches(expectedString, resultString)) {
        String message = "doctest failure running:\n" + inputString + "expected: " + expectedString + "actual: " + resultString + "\n";


        
        if (sourceName != null) {
          throw Context.reportRuntimeError(message, sourceName, lineNumber + i - 1, null, 0);
        }
        
        throw Context.reportRuntimeError(message);
      } 
    } 
    return testCount;
  }











  
  private boolean doctestOutputMatches(String expected, String actual) {
    expected = expected.trim();
    actual = actual.trim().replace("\r\n", "\n");
    if (expected.equals(actual))
      return true; 
    for (Map.Entry<String, String> entry : this.doctestCanonicalizations.entrySet()) {
      expected = expected.replace(entry.getKey(), entry.getValue());
    }
    if (expected.equals(actual)) {
      return true;
    }




    
    Pattern p = Pattern.compile("@[0-9a-fA-F]+");
    Matcher expectedMatcher = p.matcher(expected);
    Matcher actualMatcher = p.matcher(actual);
    while (true) {
      if (!expectedMatcher.find())
        return false; 
      if (!actualMatcher.find())
        return false; 
      if (actualMatcher.start() != expectedMatcher.start())
        return false; 
      int start = expectedMatcher.start();
      if (!expected.substring(0, start).equals(actual.substring(0, start)))
        return false; 
      String expectedGroup = expectedMatcher.group();
      String actualGroup = actualMatcher.group();
      String mapping = this.doctestCanonicalizations.get(expectedGroup);
      if (mapping == null) {
        this.doctestCanonicalizations.put(expectedGroup, actualGroup);
        expected = expected.replace(expectedGroup, actualGroup);
      } else if (!actualGroup.equals(mapping)) {
        return false;
      } 
      if (expected.equals(actual)) {
        return true;
      }
    } 
  }












  
  public static Object spawn(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
    Runner runner;
    Scriptable scope = funObj.getParentScope();
    
    if (args.length != 0 && args[0] instanceof Function) {
      Object[] newArgs = null;
      if (args.length > 1 && args[1] instanceof Scriptable) {
        newArgs = cx.getElements((Scriptable)args[1]);
      }
      if (newArgs == null) newArgs = ScriptRuntime.emptyArgs; 
      runner = new Runner(scope, (Function)args[0], newArgs);
    } else if (args.length != 0 && args[0] instanceof Script) {
      runner = new Runner(scope, (Script)args[0]);
    } else {
      throw reportRuntimeError("msg.spawn.args");
    } 
    runner.factory = cx.getFactory();
    Thread thread = new Thread(runner);
    thread.start();
    return thread;
  }























  
  public static Object sync(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
    if (args.length >= 1 && args.length <= 2 && args[0] instanceof Function) {
      Object syncObject = null;
      if (args.length == 2 && args[1] != Undefined.instance) {
        syncObject = args[1];
      }
      return new Synchronizer((Scriptable)args[0], syncObject);
    } 
    
    throw reportRuntimeError("msg.sync.args");
  }








































  
  public static Object runCommand(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws IOException {
    int L = args.length;
    if (L == 0 || (L == 1 && args[0] instanceof Scriptable)) {
      throw reportRuntimeError("msg.runCommand.bad.args");
    }
    File wd = null;
    InputStream in = null;
    OutputStream out = null, err = null;
    ByteArrayOutputStream outBytes = null, errBytes = null;
    Object outObj = null, errObj = null;
    String[] environment = null;
    Scriptable params = null;
    Object[] addArgs = null;
    if (args[L - 1] instanceof Scriptable) {
      params = (Scriptable)args[L - 1];
      L--;
      Object envObj = ScriptableObject.getProperty(params, "env");
      if (envObj != Scriptable.NOT_FOUND) {
        if (envObj == null) {
          environment = new String[0];
        } else {
          if (!(envObj instanceof Scriptable)) {
            throw reportRuntimeError("msg.runCommand.bad.env");
          }
          Scriptable envHash = (Scriptable)envObj;
          Object[] ids = ScriptableObject.getPropertyIds(envHash);
          environment = new String[ids.length];
          for (int j = 0; j != ids.length; j++) {
            Object val; String key; Object keyObj = ids[j];
            
            if (keyObj instanceof String) {
              key = (String)keyObj;
              val = ScriptableObject.getProperty(envHash, key);
            } else {
              int ikey = ((Number)keyObj).intValue();
              key = Integer.toString(ikey);
              val = ScriptableObject.getProperty(envHash, ikey);
            } 
            if (val == ScriptableObject.NOT_FOUND) {
              val = Undefined.instance;
            }
            environment[j] = key + '=' + ScriptRuntime.toString(val);
          } 
        } 
      }
      Object wdObj = ScriptableObject.getProperty(params, "dir");
      if (wdObj != Scriptable.NOT_FOUND) {
        wd = new File(ScriptRuntime.toString(wdObj));
      }
      
      Object inObj = ScriptableObject.getProperty(params, "input");
      if (inObj != Scriptable.NOT_FOUND) {
        in = toInputStream(inObj);
      }
      outObj = ScriptableObject.getProperty(params, "output");
      if (outObj != Scriptable.NOT_FOUND) {
        out = toOutputStream(outObj);
        if (out == null) {
          outBytes = new ByteArrayOutputStream();
          out = outBytes;
        } 
      } 
      errObj = ScriptableObject.getProperty(params, "err");
      if (errObj != Scriptable.NOT_FOUND) {
        err = toOutputStream(errObj);
        if (err == null) {
          errBytes = new ByteArrayOutputStream();
          err = errBytes;
        } 
      } 
      Object addArgsObj = ScriptableObject.getProperty(params, "args");
      if (addArgsObj != Scriptable.NOT_FOUND) {
        Scriptable s = Context.toObject(addArgsObj, getTopLevelScope(thisObj));
        
        addArgs = cx.getElements(s);
      } 
    } 
    Global global = getInstance(funObj);
    if (out == null) {
      out = (global != null) ? global.getOut() : System.out;
    }
    if (err == null) {
      err = (global != null) ? global.getErr() : System.err;
    }




    
    String[] cmd = new String[(addArgs == null) ? L : (L + addArgs.length)]; int i;
    for (i = 0; i != L; i++) {
      cmd[i] = ScriptRuntime.toString(args[i]);
    }
    if (addArgs != null) {
      for (i = 0; i != addArgs.length; i++) {
        cmd[L + i] = ScriptRuntime.toString(addArgs[i]);
      }
    }
    
    int exitCode = runProcess(cmd, environment, wd, in, out, err);
    if (outBytes != null) {
      String s = ScriptRuntime.toString(outObj) + outBytes.toString();
      ScriptableObject.putProperty(params, "output", s);
    } 
    if (errBytes != null) {
      String s = ScriptRuntime.toString(errObj) + errBytes.toString();
      ScriptableObject.putProperty(params, "err", s);
    } 
    
    return new Integer(exitCode);
  }




  
  public static void seal(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
    int i;
    for (i = 0; i != args.length; i++) {
      Object arg = args[i];
      if (!(arg instanceof ScriptableObject) || arg == Undefined.instance) {
        
        if (!(arg instanceof Scriptable) || arg == Undefined.instance)
        {
          throw reportRuntimeError("msg.shell.seal.not.object");
        }
        throw reportRuntimeError("msg.shell.seal.not.scriptable");
      } 
    } 

    
    for (i = 0; i != args.length; i++) {
      Object arg = args[i];
      ((ScriptableObject)arg).sealObject();
    } 
  }
















  
  public static Object readFile(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws IOException {
    if (args.length == 0) {
      throw reportRuntimeError("msg.shell.readFile.bad.args");
    }
    String path = ScriptRuntime.toString(args[0]);
    String charCoding = null;
    if (args.length >= 2) {
      charCoding = ScriptRuntime.toString(args[1]);
    }
    
    return readUrl(path, charCoding, true);
  }

















  
  public static Object readUrl(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws IOException {
    if (args.length == 0) {
      throw reportRuntimeError("msg.shell.readUrl.bad.args");
    }
    String url = ScriptRuntime.toString(args[0]);
    String charCoding = null;
    if (args.length >= 2) {
      charCoding = ScriptRuntime.toString(args[1]);
    }
    
    return readUrl(url, charCoding, false);
  }





  
  public static Object toint32(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
    Object arg = (args.length != 0) ? args[0] : Undefined.instance;
    if (arg instanceof Integer)
      return arg; 
    return ScriptRuntime.wrapInt(ScriptRuntime.toInt32(arg));
  }
  
  private boolean loadJLine(Charset cs) {
    if (!this.attemptedJLineLoad) {
      
      this.attemptedJLineLoad = true;
      this.console = ShellConsole.getConsole((Scriptable)this, cs);
    } 
    return (this.console != null);
  }
  
  public ShellConsole getConsole(Charset cs) {
    if (!loadJLine(cs)) {
      this.console = ShellConsole.getConsole(getIn(), getErr(), cs);
    }
    return this.console;
  }
  
  public InputStream getIn() {
    if (this.inStream == null && !this.attemptedJLineLoad && 
      loadJLine(Charset.defaultCharset())) {
      this.inStream = this.console.getIn();
    }
    
    return (this.inStream == null) ? System.in : this.inStream;
  }
  
  public void setIn(InputStream in) {
    this.inStream = in;
  }
  
  public PrintStream getOut() {
    return (this.outStream == null) ? System.out : this.outStream;
  }
  
  public void setOut(PrintStream out) {
    this.outStream = out;
  }
  
  public PrintStream getErr() {
    return (this.errStream == null) ? System.err : this.errStream;
  }
  
  public void setErr(PrintStream err) {
    this.errStream = err;
  }

  
  public void setSealedStdLib(boolean value) {
    this.sealedStdLib = value;
  }

  
  private static Global getInstance(Function function) {
    Scriptable scope = function.getParentScope();
    if (!(scope instanceof Global)) {
      throw reportRuntimeError("msg.bad.shell.function.scope", String.valueOf(scope));
    }
    return (Global)scope;
  }













  
  private static int runProcess(String[] cmd, String[] environment, File wd, InputStream in, OutputStream out, OutputStream err) throws IOException {
    Process p;
    if (environment == null) {
      p = Runtime.getRuntime().exec(cmd, (String[])null, wd);
    } else {
      p = Runtime.getRuntime().exec(cmd, environment, wd);
    } 
    
    try {
      PipeThread inThread = null;
      if (in != null) {
        inThread = new PipeThread(false, in, p.getOutputStream());
        inThread.start();
      } else {
        p.getOutputStream().close();
      } 
      
      PipeThread outThread = null;
      if (out != null) {
        outThread = new PipeThread(true, p.getInputStream(), out);
        outThread.start();
      } else {
        p.getInputStream().close();
      } 
      
      PipeThread errThread = null;
      if (err != null) {
        errThread = new PipeThread(true, p.getErrorStream(), err);
        errThread.start();
      } else {
        p.getErrorStream().close();
      } 

      
      while (true) {
        try {
          p.waitFor();
          if (outThread != null) {
            outThread.join();
          }
          if (inThread != null) {
            inThread.join();
          }
          if (errThread != null) {
            errThread.join();
          }
          break;
        } catch (InterruptedException ignore) {}
      } 

      
      return p.exitValue();
    } finally {
      p.destroy();
    } 
  }


  
  static void pipe(boolean fromProcess, InputStream from, OutputStream to) throws IOException {
    try {
      int SIZE = 4096;
      byte[] buffer = new byte[4096];
      while (true) {
        int n;
        if (!fromProcess) {
          n = from.read(buffer, 0, 4096);
        } else {
          try {
            n = from.read(buffer, 0, 4096);
          } catch (IOException ex) {
            break;
          } 
        } 
        
        if (n < 0)
          break;  if (fromProcess) {
          to.write(buffer, 0, n);
          to.flush(); continue;
        } 
        try {
          to.write(buffer, 0, n);
          to.flush();
        } catch (IOException ex) {
          
          break;
        } 
      } 
    } finally {
      
      try {
        if (fromProcess) {
          from.close();
        } else {
          to.close();
        } 
      } catch (IOException ex) {}
    } 
  }





  
  private static InputStream toInputStream(Object value) throws IOException {
    InputStream is = null;
    String s = null;
    if (value instanceof Wrapper) {
      Object unwrapped = ((Wrapper)value).unwrap();
      if (unwrapped instanceof InputStream) {
        is = (InputStream)unwrapped;
      } else if (unwrapped instanceof byte[]) {
        is = new ByteArrayInputStream((byte[])unwrapped);
      } else if (unwrapped instanceof Reader) {
        s = readReader((Reader)unwrapped);
      } else if (unwrapped instanceof char[]) {
        s = new String((char[])unwrapped);
      } 
    } 
    if (is == null) {
      if (s == null) s = ScriptRuntime.toString(value); 
      is = new ByteArrayInputStream(s.getBytes());
    } 
    return is;
  }
  
  private static OutputStream toOutputStream(Object value) {
    OutputStream os = null;
    if (value instanceof Wrapper) {
      Object unwrapped = ((Wrapper)value).unwrap();
      if (unwrapped instanceof OutputStream) {
        os = (OutputStream)unwrapped;
      }
    } 
    return os;
  }




  
  private static String readUrl(String filePath, String charCoding, boolean urlIsFile) throws IOException {
    InputStream is = null; try {
      int chunkLength; Reader r;
      if (!urlIsFile) {
        URL urlObj = new URL(filePath);
        URLConnection uc = urlObj.openConnection();
        is = uc.getInputStream();
        chunkLength = uc.getContentLength();
        if (chunkLength <= 0)
          chunkLength = 1024; 
        if (charCoding == null) {
          String type = uc.getContentType();
          if (type != null) {
            charCoding = getCharCodingFromType(type);
          }
        } 
      } else {
        File f = new File(filePath);
        if (!f.exists())
          throw new FileNotFoundException("File not found: " + filePath); 
        if (!f.canRead()) {
          throw new IOException("Cannot read file: " + filePath);
        }
        long length = f.length();
        chunkLength = (int)length;
        if (chunkLength != length) {
          throw new IOException("Too big file size: " + length);
        }
        if (chunkLength == 0) return "";
        
        is = new FileInputStream(f);
      } 

      
      if (charCoding == null) {
        r = new InputStreamReader(is);
      } else {
        r = new InputStreamReader(is, charCoding);
      } 
      return readReader(r, chunkLength);
    } finally {
      
      if (is != null) {
        is.close();
      }
    } 
  }
  
  private static String getCharCodingFromType(String type) {
    int i = type.indexOf(';');
    if (i >= 0) {
      int end = type.length();
      i++;
      while (i != end && type.charAt(i) <= ' ') {
        i++;
      }
      String charset = "charset";
      if (charset.regionMatches(true, 0, type, i, charset.length())) {
        
        i += charset.length();
        while (i != end && type.charAt(i) <= ' ') {
          i++;
        }
        if (i != end && type.charAt(i) == '=') {
          i++;
          while (i != end && type.charAt(i) <= ' ') {
            i++;
          }
          if (i != end) {

            
            while (type.charAt(end - 1) <= ' ') {
              end--;
            }
            return type.substring(i, end);
          } 
        } 
      } 
    } 
    return null;
  }


  
  private static String readReader(Reader reader) throws IOException {
    return readReader(reader, 4096);
  }


  
  private static String readReader(Reader reader, int initialBufferSize) throws IOException {
    char[] buffer = new char[initialBufferSize];
    int offset = 0;
    while (true) {
      int n = reader.read(buffer, offset, buffer.length - offset);
      if (n < 0)
        break;  offset += n;
      if (offset == buffer.length) {
        char[] tmp = new char[buffer.length * 2];
        System.arraycopy(buffer, 0, tmp, 0, offset);
        buffer = tmp;
      } 
    } 
    return new String(buffer, 0, offset);
  }
  
  static RuntimeException reportRuntimeError(String msgId) {
    String message = ToolErrorReporter.getMessage(msgId);
    return (RuntimeException)Context.reportRuntimeError(message);
  }

  
  static RuntimeException reportRuntimeError(String msgId, String msgArg) {
    String message = ToolErrorReporter.getMessage(msgId, msgArg);
    return (RuntimeException)Context.reportRuntimeError(message);
  }
}
