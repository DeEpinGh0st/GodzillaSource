package core;
import com.formdev.flatlaf.demo.intellijthemes.IJThemeInfo;
import com.formdev.flatlaf.demo.intellijthemes.IJThemesPanel;
import com.httpProxy.server.CertUtil;
import core.annotation.CryptionAnnotation;
import core.annotation.PayloadAnnotation;
import core.annotation.PluginAnnotation;
import core.imp.Cryption;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.annotation.Annotation;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import util.Log;
import util.functions;
import util.http.Http;

public class ApplicationContext {
  public static final String VERSION = "4.01";
  private static final HashMap<String, Class<?>> payloadMap;
  public static int windowWidth = (Toolkit.getDefaultToolkit().getScreenSize()).width; private static final HashMap<String, HashMap<String, Class<?>>> cryptionMap; private static final HashMap<String, HashMap<String, Class<?>>> pluginMap; private static File[] pluginJarFiles;
  public static int windowsHeight = (Toolkit.getDefaultToolkit().getScreenSize()).height;
  
  public static ThreadLocal<Boolean> isShowHttpProgressBar = new ThreadLocal<>();
  
  public static final CoreClassLoader PLUGIN_CLASSLOADER = new CoreClassLoader(ApplicationContext.class.getClassLoader());

  
  public static boolean easterEgg = true;
  
  private static Font font;
  
  private static Map<String, String> headerMap;

  
  static {
    payloadMap = new HashMap<>();
    cryptionMap = new HashMap<>();
    pluginMap = new HashMap<>();
  }
  public static void init() {
    initFont();
    initHttpHeader();
    scanPluginJar();
    scanPayload();
    scanCryption();
    scanPlugin();
  }

  
  private static void initFont() {
    String fontName = Db.getSetingValue("font-name");
    String fontType = Db.getSetingValue("font-type");
    String fontSize = Db.getSetingValue("font-size");
    if (fontName != null && fontType != null && fontSize != null) {
      font = new Font(fontName, Integer.parseInt(fontType), Integer.parseInt(fontSize));
      InitGlobalFont(font);
    } 
  }
  private static void initHttpHeader() {
    String headerString = getGloballHttpHeader();
    if (headerString != null) {
      String[] reqLines = headerString.split("\n");


      
      headerMap = new Hashtable<>();
      for (int i = 0; i < reqLines.length; i++) {
        if (!reqLines[i].trim().isEmpty()) {
          int index = reqLines[i].indexOf(":");
          if (index > 1) {
            String keyName = reqLines[i].substring(0, index).trim();
            String keyValue = reqLines[i].substring(index + 1).trim();
            headerMap.put(keyName, keyValue);
          } 
        } 
      } 
    } 
  }
  private static void scanPayload() {
    try {
      URL url = ApplicationContext.class.getResource("/shells/payloads/");
      ArrayList<Class<?>> destList = new ArrayList<>();
      int loadNum = scanClass(url.toURI(), "shells.payloads", Payload.class, PayloadAnnotation.class, destList);
      destList.forEach(t -> {
            try {
              Annotation annotation = (Annotation)t.getAnnotation(PayloadAnnotation.class);
              String name = (String)annotation.annotationType().getMethod("Name", new Class[0]).invoke(annotation, null);
              payloadMap.put(name, t);
              cryptionMap.put(name, new HashMap<>());
              pluginMap.put(name, new HashMap<>());
            } catch (Exception e) {
              Log.error(e);
            } 
          });
      Log.log(String.format("load payload success! payloadMaxNum:%s onceLoadPayloadNum:%s", new Object[] { Integer.valueOf(payloadMap.size()), Integer.valueOf(loadNum) }), new Object[0]);
    } catch (Exception e) {
      Log.error(e);
    } 
  }
  
  private static void scanCryption() {
    try {
      URL url = ApplicationContext.class.getResource("/shells/cryptions/");
      ArrayList<Class<?>> destList = new ArrayList<>();
      int loadNum = scanClass(url.toURI(), "shells.cryptions", Cryption.class, CryptionAnnotation.class, destList);
      int pluginMaxNum = 0;
      destList.forEach(t -> {
            try {
              Annotation annotation = (Annotation)t.getAnnotation(CryptionAnnotation.class);
              String name = (String)annotation.annotationType().getMethod("Name", new Class[0]).invoke(annotation, null);
              String payloadName = (String)annotation.annotationType().getMethod("payloadName", new Class[0]).invoke(annotation, null);
              HashMap<String, Class<?>> destMap = cryptionMap.get(payloadName);
              if (destMap == null) {
                cryptionMap.put(payloadName, new HashMap<>());
                destMap = cryptionMap.get(payloadName);
              } 
              destMap.put(name, t);
            } catch (Exception e) {
              e.printStackTrace();
              Log.error(e);
            } 
          });
      Iterator<String> iterator = cryptionMap.keySet().iterator();
      while (iterator.hasNext()) {
        String keyString = iterator.next();
        HashMap<String, Class<?>> map = cryptionMap.get(keyString);
        if (map != null) {
          pluginMaxNum += map.size();
        }
      } 
      Log.log(String.format("load cryption success! cryptionMaxNum:%s onceLoadCryptionNum:%s", new Object[] { Integer.valueOf(pluginMaxNum), Integer.valueOf(loadNum) }), new Object[0]);
    } catch (Exception e) {
      Log.error(e);
    } 
  }
  private static void scanPlugin() {
    try {
      URL url = ApplicationContext.class.getResource("/shells/plugins/");
      ArrayList<Class<?>> destList = new ArrayList<>();
      int loadNum = scanClass(url.toURI(), "shells.plugins", Plugin.class, PluginAnnotation.class, destList);
      int pluginMaxNum = 0;
      destList.forEach(t -> {
            try {
              Annotation annotation = (Annotation)t.getAnnotation(PluginAnnotation.class);
              String name = (String)annotation.annotationType().getMethod("Name", new Class[0]).invoke(annotation, null);
              String payloadName = (String)annotation.annotationType().getMethod("payloadName", new Class[0]).invoke(annotation, null);
              HashMap<String, Class<?>> destMap = pluginMap.get(payloadName);
              if (destMap == null) {
                pluginMap.put(payloadName, new HashMap<>());
                destMap = pluginMap.get(payloadName);
              } 
              destMap.put(name, t);
            } catch (Exception e) {
              Log.error(e);
            } 
          });
      Iterator<String> iterator = pluginMap.keySet().iterator();
      while (iterator.hasNext()) {
        String keyString = iterator.next();
        HashMap<String, Class<?>> map = pluginMap.get(keyString);
        if (map != null) {
          pluginMaxNum += map.size();
        }
      } 
      Log.log(String.format("load plugin success! pluginMaxNum:%s onceLoadPluginNum:%s", new Object[] { Integer.valueOf(pluginMaxNum), Integer.valueOf(loadNum) }), new Object[0]);
    } catch (Exception e) {
      Log.error(e);
    } 
  }
  private static void scanPluginJar() {
    String[] pluginJars = Db.getAllPlugin();
    ArrayList<File> list = new ArrayList();
    
    for (int i = 0; i < pluginJars.length; i++) {
      File jarFile = new File(pluginJars[i]);
      if (jarFile.exists() && jarFile.isFile()) {
        addJar(jarFile);
        list.add(jarFile);
      } else {
        Log.error(String.format("PluginJarFile : %s no found", new Object[] { pluginJars[i] }));
      } 
    } 
    pluginJarFiles = list.<File>toArray(new File[0]);
    Log.log(String.format("load pluginJar success! pluginJarNum:%s LoadPluginJarSuccessNum:%s", new Object[] { Integer.valueOf(pluginJars.length), Integer.valueOf(pluginJars.length) }), new Object[0]);
  }
  private static int scanClass(URI uri, String packageName, Class<?> parentClass, Class<?> annotationClass, ArrayList<Class<?>> destList) {
    int num = scanClassX(uri, packageName, parentClass, annotationClass, destList);
    
    for (int i = 0; i < pluginJarFiles.length; i++) {
      File jarFile = pluginJarFiles[i];
      num += scanClassByJar(jarFile, packageName, parentClass, annotationClass, destList);
    } 
    
    return num;
  }
  private static int scanClassX(URI uri, String packageName, Class<?> parentClass, Class<?> annotationClass, ArrayList<Class<?>> destList) {
    String jarFileString;
    if ((jarFileString = functions.getJarFileByClass(ApplicationContext.class)) != null) {
      return scanClassByJar(new File(jarFileString), packageName, parentClass, annotationClass, destList);
    }
    int addNum = 0;
    try {
      File file = new File(uri);
      File[] file2 = file.listFiles();






      
      for (int i = 0; i < file2.length; i++) {
        File objectFile = file2[i];
        if (objectFile.isDirectory()) {
          File[] objectFiles = objectFile.listFiles();
          for (int j = 0; j < objectFiles.length; j++) {
            File objectClassFile = objectFiles[j];
            if (objectClassFile.getPath().endsWith(".class")) {
              try {
                String objectClassName = String.format("%s.%s.%s", new Object[] { packageName, objectFile.getName(), objectClassFile.getName().substring(0, objectClassFile.getName().length() - ".class".length()) });
                Class<?> objectClass = Class.forName(objectClassName, true, PLUGIN_CLASSLOADER);
                if (parentClass.isAssignableFrom(objectClass) && objectClass.isAnnotationPresent((Class)annotationClass)) {
                  destList.add(objectClass);
                  addNum++;
                } 
              } catch (Exception e) {
                Log.error(e);
              } 
            }
          } 
        } 
      } 
    } catch (Exception e) {
      Log.error(e);
    } 
    return addNum;
  }
  private static int scanClassByJar(File srcJarFile, String packageName, Class<?> parentClass, Class<?> annotationClass, ArrayList<Class<?>> destList) {
    int addNum = 0;
    try {
      JarFile jarFile = new JarFile(srcJarFile);
      Enumeration<JarEntry> jarFiles = jarFile.entries();




      
      packageName = packageName.replace(".", "/");
      while (jarFiles.hasMoreElements()) {
        JarEntry jarEntry = jarFiles.nextElement();
        String name = jarEntry.getName();
        if (name.startsWith(packageName) && name.endsWith(".class")) {
          name = name.replace("/", ".");
          name = name.substring(0, name.length() - 6);
          try {
            String objectClassName = name;
            Class<?> objectClass = Class.forName(objectClassName, true, PLUGIN_CLASSLOADER);
            if (parentClass.isAssignableFrom(objectClass) && objectClass.isAnnotationPresent((Class)annotationClass)) {
              destList.add(objectClass);
              addNum++;
            } 
          } catch (Exception e) {
            Log.error(e);
          } 
        } 
      } 
      
      jarFile.close();
    } catch (Exception e) {
      Log.error(e);
    } 
    return addNum;
  }
  public static String[] getAllPayload() {
    Set<String> keys = payloadMap.keySet();
    return keys.<String>toArray(new String[0]);
  }
  public static Payload getPayload(String payloadName) {
    Class<?> payloadClass = payloadMap.get(payloadName);
    Payload payload = null;
    if (payloadClass != null) {
      try {
        payload = (Payload)payloadClass.newInstance();
      } catch (Exception e) {
        Log.error(e);
      } 
    }
    return payload;
  }
  public static Plugin[] getAllPlugin(String payloadName) {
    HashMap<String, Class<?>> pluginSrcMap = pluginMap.get(payloadName);
    ArrayList<Plugin> list = new ArrayList<>();
    Class<?> payloadClass = payloadMap.get(payloadName);
    while (payloadClass != null && (payloadClass = payloadClass.getSuperclass()) != null) {
      if (payloadClass != null && payloadClass.isAnnotationPresent((Class)PayloadAnnotation.class)) {
        list.addAll(new CopyOnWriteArrayList<>(getAllPlugin(payloadClass)));
      }
    } 
    if (pluginSrcMap != null) {
      Iterator<String> keys = pluginSrcMap.keySet().iterator();

      
      while (keys.hasNext()) {
        String cryptionName = keys.next();
        Class<?> pluginClass = pluginSrcMap.get(cryptionName);
        if (pluginClass != null) {
          PluginAnnotation pluginAnnotation = pluginClass.<PluginAnnotation>getAnnotation(PluginAnnotation.class);
          if (pluginAnnotation.payloadName().equals(payloadName)) {
            try {
              Plugin plugin = (Plugin)pluginClass.newInstance();
              list.add(plugin);
            } catch (Exception e) {
              Log.error(e);
            } 
          }
        } 
      } 
    } 

    
    return list.<Plugin>toArray(new Plugin[0]);
  }
  public static Plugin[] getAllPlugin(Class payloadClass) {
    Annotation annotation = (Annotation)payloadClass.getAnnotation(PayloadAnnotation.class);
    if (annotation != null) {
      PayloadAnnotation payloadAnnotation = (PayloadAnnotation)annotation;
      return getAllPlugin(payloadAnnotation.Name());
    } 
    return new Plugin[0];
  }
  public static String[] getAllCryption(String payloadName) {
    HashMap<String, Class<?>> cryptionSrcMap = cryptionMap.get(payloadName);
    ArrayList<String> list = new ArrayList<>();
    if (cryptionSrcMap != null) {
      Iterator<String> keys = cryptionSrcMap.keySet().iterator();
      
      while (keys.hasNext()) {
        String cryptionName = keys.next();
        Class<?> cryptionClass = cryptionSrcMap.get(cryptionName);
        if (cryptionClass != null) {
          CryptionAnnotation cryptionAnnotation = cryptionClass.<CryptionAnnotation>getAnnotation(CryptionAnnotation.class);
          if (cryptionAnnotation.payloadName().equals(payloadName)) {
            list.add(cryptionName);
          }
        } 
      } 
    } 
    
    return list.<String>toArray(new String[0]);
  }
  public static Cryption getCryption(String payloadName, String crytionName) {
    HashMap<String, Class<?>> cryptionSrcMap = cryptionMap.get(payloadName);
    if (cryptionSrcMap != null) {
      Class<?> cryptionClass = cryptionSrcMap.get(crytionName);
      if (cryptionMap != null) {
        CryptionAnnotation cryptionAnnotation = cryptionClass.<CryptionAnnotation>getAnnotation(CryptionAnnotation.class);
        if (cryptionAnnotation.payloadName().equals(payloadName)) {
          Cryption cryption = null;
          try {
            cryption = (Cryption)cryptionClass.newInstance();
            return cryption;
          } catch (Exception e) {
            Log.error(e);
            return null;
          } 
        } 
      } 
    } 
    return null;
  }
  
  private static void addJar(File jarPath) {
    try {
      PLUGIN_CLASSLOADER.addJar(jarPath.toURI().toURL());
    } catch (Exception e) {
      Log.error(e);
    } 
  }
  private static void InitGlobalFont(Font font) {
    FontUIResource fontRes = new FontUIResource(font);
    Enumeration<Object> keys = UIManager.getDefaults().keys();
    while (keys.hasMoreElements()) {
      Object key = keys.nextElement();
      Object value = UIManager.get(key);
      if (value instanceof FontUIResource)
        UIManager.put(key, fontRes); 
    } 
  }
  
  public static Proxy getProxy(ShellEntity shellContext) {
    return ProxyT.getProxy(shellContext);
  }
  public static String[] getAllProxy() {
    return ProxyT.getAllProxyType();
  }
  public static String[] getAllEncodingTypes() {
    return Encoding.getAllEncodingTypes();
  }
  public static Http getHttp(ShellEntity shellEntity) {
    Http httpx = new Http(shellEntity);
    return httpx;
  }
  public static Font getFont() {
    return font;
  }
  public static void setFont(Font font) {
    Db.updateSetingKV("font-name", font.getName());
    Db.updateSetingKV("font-type", Integer.toString(font.getStyle()));
    Db.updateSetingKV("font-size", Integer.toString(font.getSize()));
    ApplicationContext.font = font;
  }
  public static void resetFont() {
    Db.removeSetingK("font-name");
    Db.removeSetingK("font-type");
    Db.removeSetingK("font-size");
  }
  public static String getGloballHttpHeader() {
    return Db.getSetingValue("globallHttpHeader");
  }
  public static Map<String, String> getGloballHttpHeaderX() {
    return headerMap;
  }
  public static boolean updateGloballHttpHeader(String header) {
    boolean state = Db.updateSetingKV("globallHttpHeader", header);
    initHttpHeader();
    return state;
  }
  public static boolean isGodMode() {
    return Boolean.valueOf(Db.getSetingValue("godMode")).booleanValue();
  }
  public static boolean setGodMode(boolean state) {
    return Db.updateSetingKV("godMode", String.valueOf(state));
  }
  public static boolean isOpenC(String k) {
    return Boolean.valueOf(Db.getSetingValue(k)).booleanValue();
  }
  public static boolean setOpenC(String k, boolean state) {
    return Db.updateSetingKV(k, String.valueOf(state));
  }
  public static boolean isOpenCache() {
    return Db.getSetingBooleanValue("shellOpenCache", true);
  }
  public static boolean setOpenCache(boolean state) {
    return setOpenC("shellOpenCache", state);
  }
  public static void initUi() {
    if (SystemInfo.isMacOS && System.getProperty("apple.laf.useScreenMenuBar") == null) {
      System.setProperty("apple.laf.useScreenMenuBar", "true");
    }
    UIManager.put("Table.showHorizontalLines", Boolean.valueOf(true));
    UIManager.put("Table.showVerticalLines", Boolean.valueOf(true));
    JFrame.setDefaultLookAndFeelDecorated(true);
    JDialog.setDefaultLookAndFeelDecorated(true);
    
    String resourceNameString = Db.getSetingValue("ui-resourceName");
    String lafClassNameString = Db.getSetingValue("ui-lafClassName");
    if (resourceNameString == null && lafClassNameString == null) {
      Db.updateSetingKV("ui-lafClassName", "com.formdev.flatlaf.FlatIntelliJLaf");
    }
    lafClassNameString = Db.getSetingValue("ui-lafClassName");
    IJThemesPanel.setTheme(new IJThemeInfo(resourceNameString, lafClassNameString));
  }
  
  public static boolean saveUi(IJThemeInfo themeInfo) {
    try {
      String resourceNameString = themeInfo.getResourceName();
      String lafClassNameString = themeInfo.getLafClassName();
      if (resourceNameString != null && lafClassNameString == null) {
        Db.updateSetingKV("ui-resourceName", resourceNameString);
        Db.removeSetingK("ui-lafClassName");
      } 
      if (lafClassNameString != null && resourceNameString == null) {
        Db.updateSetingKV("ui-lafClassName", lafClassNameString);
        Db.removeSetingK("ui-resourceName");
      } 
      
      if (lafClassNameString == null && resourceNameString == null) {
        return false;
      }
    }
    catch (Exception e) {
      Log.error(e);
      return false;
    } 
    return true;
  }
  public static void genHttpsConfig() {
    try {
      KeyPair keyPair = CertUtil.genKeyPair();
      String base64HttpsCert = functions.base64EncodeToString(CertUtil.genCACert("C=CN, ST=GD, L=SZ, O=lee, OU=study, CN=HttpsProxy", new Date(), new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3650L)), keyPair).getEncoded());
      String base64HttpsPrivateKey = functions.base64EncodeToString((new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded())).getEncoded());
      Db.addSetingKV("HttpsPrivateKey", base64HttpsPrivateKey);
      Db.addSetingKV("HttpsCert", base64HttpsCert);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  public static PrivateKey getHttpsPrivateKey() {
    try {
      String base64String = Db.getSetingValue("HttpsPrivateKey");
      if (base64String == null) {
        genHttpsConfig();
      }
      return CertUtil.loadPriKey(functions.base64Decode(Db.getSetingValue("HttpsPrivateKey")));
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  public static X509Certificate getHttpsCert() {
    try {
      String base64String = Db.getSetingValue("HttpsCert");
      if (base64String == null) {
        genHttpsConfig();
      }
      return CertUtil.loadCert(new ByteArrayInputStream(functions.base64Decode(Db.getSetingValue("HttpsCert"))));
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
}
