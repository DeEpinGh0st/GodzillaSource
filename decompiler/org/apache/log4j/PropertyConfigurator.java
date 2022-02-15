package org.apache.log4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.log4j.config.PropertySetter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RendererSupport;
import org.apache.log4j.spi.ThrowableRenderer;
import org.apache.log4j.spi.ThrowableRendererSupport;



































































public class PropertyConfigurator
  implements Configurator
{
  protected Hashtable registry = new Hashtable(11);
  private LoggerRepository repository;
  protected LoggerFactory loggerFactory = new DefaultCategoryFactory();














  
  static final String CATEGORY_PREFIX = "log4j.category.";














  
  static final String LOGGER_PREFIX = "log4j.logger.";














  
  static final String FACTORY_PREFIX = "log4j.factory";














  
  static final String ADDITIVITY_PREFIX = "log4j.additivity.";














  
  static final String ROOT_CATEGORY_PREFIX = "log4j.rootCategory";














  
  static final String ROOT_LOGGER_PREFIX = "log4j.rootLogger";














  
  static final String APPENDER_PREFIX = "log4j.appender.";














  
  static final String RENDERER_PREFIX = "log4j.renderer.";














  
  static final String THRESHOLD_PREFIX = "log4j.threshold";














  
  private static final String THROWABLE_RENDERER_PREFIX = "log4j.throwableRenderer";














  
  private static final String LOGGER_REF = "logger-ref";














  
  private static final String ROOT_REF = "root-ref";













  
  private static final String APPENDER_REF_TAG = "appender-ref";













  
  public static final String LOGGER_FACTORY_KEY = "log4j.loggerFactory";













  
  private static final String RESET_KEY = "log4j.reset";













  
  private static final String INTERNAL_ROOT_NAME = "root";














  
  public void doConfigure(String configFileName, LoggerRepository hierarchy) {
    Properties props = new Properties();
    FileInputStream istream = null;
    try {
      istream = new FileInputStream(configFileName);
      props.load(istream);
      istream.close();
    }
    catch (Exception e) {
      if (e instanceof InterruptedIOException || e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
      LogLog.error("Could not read configuration file [" + configFileName + "].", e);
      LogLog.error("Ignoring configuration file [" + configFileName + "].");
      return;
    } finally {
      if (istream != null) {
        try {
          istream.close();
        } catch (InterruptedIOException ignore) {
          Thread.currentThread().interrupt();
        } catch (Throwable ignore) {}
      }
    } 


    
    doConfigure(props, hierarchy);
  }




  
  public static void configure(String configFilename) {
    (new PropertyConfigurator()).doConfigure(configFilename, LogManager.getLoggerRepository());
  }








  
  public static void configure(URL configURL) {
    (new PropertyConfigurator()).doConfigure(configURL, LogManager.getLoggerRepository());
  }








  
  public static void configure(InputStream inputStream) {
    (new PropertyConfigurator()).doConfigure(inputStream, LogManager.getLoggerRepository());
  }









  
  public static void configure(Properties properties) {
    (new PropertyConfigurator()).doConfigure(properties, LogManager.getLoggerRepository());
  }











  
  public static void configureAndWatch(String configFilename) {
    configureAndWatch(configFilename, 60000L);
  }














  
  public static void configureAndWatch(String configFilename, long delay) {
    PropertyWatchdog pdog = new PropertyWatchdog(configFilename);
    pdog.setDelay(delay);
    pdog.start();
  }







  
  public void doConfigure(Properties properties, LoggerRepository hierarchy) {
    this.repository = hierarchy;
    String value = properties.getProperty("log4j.debug");
    if (value == null) {
      value = properties.getProperty("log4j.configDebug");
      if (value != null) {
        LogLog.warn("[log4j.configDebug] is deprecated. Use [log4j.debug] instead.");
      }
    } 
    if (value != null) {
      LogLog.setInternalDebugging(OptionConverter.toBoolean(value, true));
    }



    
    String reset = properties.getProperty("log4j.reset");
    if (reset != null && OptionConverter.toBoolean(reset, false)) {
      hierarchy.resetConfiguration();
    }
    
    String thresholdStr = OptionConverter.findAndSubst("log4j.threshold", properties);
    
    if (thresholdStr != null) {
      hierarchy.setThreshold(OptionConverter.toLevel(thresholdStr, Level.ALL));
      
      LogLog.debug("Hierarchy threshold set to [" + hierarchy.getThreshold() + "].");
    } 
    
    configureRootCategory(properties, hierarchy);
    configureLoggerFactory(properties);
    parseCatsAndRenderers(properties, hierarchy);
    
    LogLog.debug("Finished configuring.");

    
    this.registry.clear();
  }





  
  public void doConfigure(InputStream inputStream, LoggerRepository hierarchy) {
    Properties props = new Properties();
    try {
      props.load(inputStream);
    } catch (IOException e) {
      if (e instanceof InterruptedIOException) {
        Thread.currentThread().interrupt();
      }
      LogLog.error("Could not read configuration file from InputStream [" + inputStream + "].", e);
      
      LogLog.error("Ignoring configuration InputStream [" + inputStream + "].");
      return;
    } 
    doConfigure(props, hierarchy);
  }




  
  public void doConfigure(URL configURL, LoggerRepository hierarchy) {
    Properties props = new Properties();
    LogLog.debug("Reading configuration from URL " + configURL);
    InputStream istream = null;
    URLConnection uConn = null;
    try {
      uConn = configURL.openConnection();
      uConn.setUseCaches(false);
      istream = uConn.getInputStream();
      props.load(istream);
    }
    catch (Exception e) {
      if (e instanceof InterruptedIOException || e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
      LogLog.error("Could not read configuration file from URL [" + configURL + "].", e);
      
      LogLog.error("Ignoring configuration file [" + configURL + "].");
      
      return;
    } finally {
      if (istream != null) {
        
        try { istream.close(); }
        catch (InterruptedIOException ignore)
        { Thread.currentThread().interrupt(); }
        catch (IOException ignore) {  }
        catch (RuntimeException ignore) {}
      }
    } 
    
    doConfigure(props, hierarchy);
  }















  
  protected void configureLoggerFactory(Properties props) {
    String factoryClassName = OptionConverter.findAndSubst("log4j.loggerFactory", props);
    
    if (factoryClassName != null) {
      LogLog.debug("Setting category factory to [" + factoryClassName + "].");
      this.loggerFactory = (LoggerFactory)OptionConverter.instantiateByClassName(factoryClassName, LoggerFactory.class, this.loggerFactory);


      
      PropertySetter.setProperties(this.loggerFactory, props, "log4j.factory.");
    } 
  }























  
  void configureRootCategory(Properties props, LoggerRepository hierarchy) {
    String effectiveFrefix = "log4j.rootLogger";
    String value = OptionConverter.findAndSubst("log4j.rootLogger", props);
    
    if (value == null) {
      value = OptionConverter.findAndSubst("log4j.rootCategory", props);
      effectiveFrefix = "log4j.rootCategory";
    } 
    
    if (value == null) {
      LogLog.debug("Could not find root logger information. Is this OK?");
    } else {
      Logger root = hierarchy.getRootLogger();
      synchronized (root) {
        parseCategory(props, root, effectiveFrefix, "root", value);
      } 
    } 
  }





  
  protected void parseCatsAndRenderers(Properties props, LoggerRepository hierarchy) {
    Enumeration enumeration = props.propertyNames();
    while (enumeration.hasMoreElements()) {
      String key = (String)enumeration.nextElement();
      if (key.startsWith("log4j.category.") || key.startsWith("log4j.logger.")) {
        String loggerName = null;
        if (key.startsWith("log4j.category.")) {
          loggerName = key.substring("log4j.category.".length());
        } else if (key.startsWith("log4j.logger.")) {
          loggerName = key.substring("log4j.logger.".length());
        } 
        String value = OptionConverter.findAndSubst(key, props);
        Logger logger = hierarchy.getLogger(loggerName, this.loggerFactory);
        synchronized (logger) {
          parseCategory(props, logger, key, loggerName, value);
          parseAdditivityForLogger(props, logger, loggerName);
        }  continue;
      }  if (key.startsWith("log4j.renderer.")) {
        String renderedClass = key.substring("log4j.renderer.".length());
        String renderingClass = OptionConverter.findAndSubst(key, props);
        if (hierarchy instanceof RendererSupport)
          RendererMap.addRenderer((RendererSupport)hierarchy, renderedClass, renderingClass); 
        continue;
      } 
      if (key.equals("log4j.throwableRenderer") && 
        hierarchy instanceof ThrowableRendererSupport) {
        ThrowableRenderer tr = (ThrowableRenderer)OptionConverter.instantiateByKey(props, "log4j.throwableRenderer", ThrowableRenderer.class, null);



        
        if (tr == null) {
          LogLog.error("Could not instantiate throwableRenderer.");
          continue;
        } 
        PropertySetter setter = new PropertySetter(tr);
        setter.setProperties(props, "log4j.throwableRenderer.");
        ((ThrowableRendererSupport)hierarchy).setThrowableRenderer(tr);
      } 
    } 
  }







  
  void parseAdditivityForLogger(Properties props, Logger cat, String loggerName) {
    String value = OptionConverter.findAndSubst("log4j.additivity." + loggerName, props);
    
    LogLog.debug("Handling log4j.additivity." + loggerName + "=[" + value + "]");
    
    if (value != null && !value.equals("")) {
      boolean additivity = OptionConverter.toBoolean(value, true);
      LogLog.debug("Setting additivity for \"" + loggerName + "\" to " + additivity);
      
      cat.setAdditivity(additivity);
    } 
  }





  
  void parseCategory(Properties props, Logger logger, String optionKey, String loggerName, String value) {
    LogLog.debug("Parsing for [" + loggerName + "] with value=[" + value + "].");
    
    StringTokenizer st = new StringTokenizer(value, ",");



    
    if (!value.startsWith(",") && !value.equals("")) {

      
      if (!st.hasMoreTokens()) {
        return;
      }
      String levelStr = st.nextToken();
      LogLog.debug("Level token is [" + levelStr + "].");



      
      if ("inherited".equalsIgnoreCase(levelStr) || "null".equalsIgnoreCase(levelStr)) {
        
        if (loggerName.equals("root")) {
          LogLog.warn("The root logger cannot be set to null.");
        } else {
          logger.setLevel(null);
        } 
      } else {
        logger.setLevel(OptionConverter.toLevel(levelStr, Level.DEBUG));
      } 
      LogLog.debug("Category " + loggerName + " set to " + logger.getLevel());
    } 

    
    logger.removeAllAppenders();


    
    while (st.hasMoreTokens()) {
      String appenderName = st.nextToken().trim();
      if (appenderName == null || appenderName.equals(","))
        continue; 
      LogLog.debug("Parsing appender named \"" + appenderName + "\".");
      Appender appender = parseAppender(props, appenderName);
      if (appender != null) {
        logger.addAppender(appender);
      }
    } 
  }
  
  Appender parseAppender(Properties props, String appenderName) {
    Appender appender = registryGet(appenderName);
    if (appender != null) {
      LogLog.debug("Appender \"" + appenderName + "\" was already parsed.");
      return appender;
    } 
    
    String prefix = "log4j.appender." + appenderName;
    String layoutPrefix = prefix + ".layout";
    
    appender = (Appender)OptionConverter.instantiateByKey(props, prefix, Appender.class, null);

    
    if (appender == null) {
      LogLog.error("Could not instantiate appender named \"" + appenderName + "\".");
      
      return null;
    } 
    appender.setName(appenderName);
    
    if (appender instanceof org.apache.log4j.spi.OptionHandler) {
      if (appender.requiresLayout()) {
        Layout layout = (Layout)OptionConverter.instantiateByKey(props, layoutPrefix, Layout.class, null);


        
        if (layout != null) {
          appender.setLayout(layout);
          LogLog.debug("Parsing layout options for \"" + appenderName + "\".");
          
          PropertySetter.setProperties(layout, props, layoutPrefix + ".");
          LogLog.debug("End of parsing for \"" + appenderName + "\".");
        } 
      } 
      String errorHandlerPrefix = prefix + ".errorhandler";
      String errorHandlerClass = OptionConverter.findAndSubst(errorHandlerPrefix, props);
      if (errorHandlerClass != null) {
        ErrorHandler eh = (ErrorHandler)OptionConverter.instantiateByKey(props, errorHandlerPrefix, ErrorHandler.class, null);


        
        if (eh != null) {
          appender.setErrorHandler(eh);
          LogLog.debug("Parsing errorhandler options for \"" + appenderName + "\".");
          parseErrorHandler(eh, errorHandlerPrefix, props, this.repository);
          Properties edited = new Properties();
          String[] keys = { errorHandlerPrefix + "." + "root-ref", errorHandlerPrefix + "." + "logger-ref", errorHandlerPrefix + "." + "appender-ref" };



          
          for (Iterator iter = props.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry entry = iter.next();
            int i = 0;
            for (; i < keys.length && 
              !keys[i].equals(entry.getKey()); i++);
            
            if (i == keys.length) {
              edited.put(entry.getKey(), entry.getValue());
            }
          } 
          PropertySetter.setProperties(eh, edited, errorHandlerPrefix + ".");
          LogLog.debug("End of errorhandler parsing for \"" + appenderName + "\".");
        } 
      } 

      
      PropertySetter.setProperties(appender, props, prefix + ".");
      LogLog.debug("Parsed \"" + appenderName + "\" options.");
    } 
    parseAppenderFilters(props, appenderName, appender);
    registryPut(appender);
    return appender;
  }




  
  private void parseErrorHandler(ErrorHandler eh, String errorHandlerPrefix, Properties props, LoggerRepository hierarchy) {
    boolean rootRef = OptionConverter.toBoolean(OptionConverter.findAndSubst(errorHandlerPrefix + "root-ref", props), false);
    
    if (rootRef) {
      eh.setLogger(hierarchy.getRootLogger());
    }
    String loggerName = OptionConverter.findAndSubst(errorHandlerPrefix + "logger-ref", props);
    if (loggerName != null) {
      Logger logger = (this.loggerFactory == null) ? hierarchy.getLogger(loggerName) : hierarchy.getLogger(loggerName, this.loggerFactory);
      
      eh.setLogger(logger);
    } 
    String appenderName = OptionConverter.findAndSubst(errorHandlerPrefix + "appender-ref", props);
    if (appenderName != null) {
      Appender backup = parseAppender(props, appenderName);
      if (backup != null) {
        eh.setBackupAppender(backup);
      }
    } 
  }




  
  void parseAppenderFilters(Properties props, String appenderName, Appender appender) {
    String filterPrefix = "log4j.appender." + appenderName + ".filter.";
    int fIdx = filterPrefix.length();
    Hashtable filters = new Hashtable();
    Enumeration e = props.keys();
    String name = "";
    while (e.hasMoreElements()) {
      String key = e.nextElement();
      if (key.startsWith(filterPrefix)) {
        int dotIdx = key.indexOf('.', fIdx);
        String filterKey = key;
        if (dotIdx != -1) {
          filterKey = key.substring(0, dotIdx);
          name = key.substring(dotIdx + 1);
        } 
        Vector filterOpts = (Vector)filters.get(filterKey);
        if (filterOpts == null) {
          filterOpts = new Vector();
          filters.put(filterKey, filterOpts);
        } 
        if (dotIdx != -1) {
          String value = OptionConverter.findAndSubst(key, props);
          filterOpts.add(new NameValue(name, value));
        } 
      } 
    } 


    
    Enumeration g = new SortedKeyEnumeration(filters);
    while (g.hasMoreElements()) {
      String key = g.nextElement();
      String clazz = props.getProperty(key);
      if (clazz != null) {
        LogLog.debug("Filter key: [" + key + "] class: [" + props.getProperty(key) + "] props: " + filters.get(key));
        Filter filter = (Filter)OptionConverter.instantiateByClassName(clazz, Filter.class, null);
        if (filter != null) {
          PropertySetter propSetter = new PropertySetter(filter);
          Vector v = (Vector)filters.get(key);
          Enumeration filterProps = v.elements();
          while (filterProps.hasMoreElements()) {
            NameValue kv = filterProps.nextElement();
            propSetter.setProperty(kv.key, kv.value);
          } 
          propSetter.activate();
          LogLog.debug("Adding filter of type [" + filter.getClass() + "] to appender named [" + appender.getName() + "].");
          
          appender.addFilter(filter);
        }  continue;
      } 
      LogLog.warn("Missing class definition for filter: [" + key + "]");
    } 
  }


  
  void registryPut(Appender appender) {
    this.registry.put(appender.getName(), appender);
  }
  
  Appender registryGet(String name) {
    return (Appender)this.registry.get(name);
  }
}
