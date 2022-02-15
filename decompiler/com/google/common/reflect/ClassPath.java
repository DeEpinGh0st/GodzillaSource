package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.StandardSystemProperty;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;



































@Beta
public final class ClassPath
{
  private static final Logger logger = Logger.getLogger(ClassPath.class.getName());
  
  private static final Predicate<ClassInfo> IS_TOP_LEVEL = new Predicate<ClassInfo>()
    {
      public boolean apply(ClassPath.ClassInfo info)
      {
        return (info.className.indexOf('$') == -1);
      }
    };


  
  private static final Splitter CLASS_PATH_ATTRIBUTE_SEPARATOR = Splitter.on(" ").omitEmptyStrings();
  
  private static final String CLASS_FILE_NAME_EXTENSION = ".class";
  
  private final ImmutableSet<ResourceInfo> resources;
  
  private ClassPath(ImmutableSet<ResourceInfo> resources) {
    this.resources = resources;
  }
















  
  public static ClassPath from(ClassLoader classloader) throws IOException {
    DefaultScanner scanner = new DefaultScanner();
    scanner.scan(classloader);
    return new ClassPath(scanner.getResources());
  }




  
  public ImmutableSet<ResourceInfo> getResources() {
    return this.resources;
  }





  
  public ImmutableSet<ClassInfo> getAllClasses() {
    return FluentIterable.from((Iterable)this.resources).filter(ClassInfo.class).toSet();
  }

  
  public ImmutableSet<ClassInfo> getTopLevelClasses() {
    return FluentIterable.from((Iterable)this.resources).filter(ClassInfo.class).filter(IS_TOP_LEVEL).toSet();
  }

  
  public ImmutableSet<ClassInfo> getTopLevelClasses(String packageName) {
    Preconditions.checkNotNull(packageName);
    ImmutableSet.Builder<ClassInfo> builder = ImmutableSet.builder();
    for (UnmodifiableIterator<ClassInfo> unmodifiableIterator = getTopLevelClasses().iterator(); unmodifiableIterator.hasNext(); ) { ClassInfo classInfo = unmodifiableIterator.next();
      if (classInfo.getPackageName().equals(packageName)) {
        builder.add(classInfo);
      } }
    
    return builder.build();
  }




  
  public ImmutableSet<ClassInfo> getTopLevelClassesRecursive(String packageName) {
    Preconditions.checkNotNull(packageName);
    String packagePrefix = packageName + '.';
    ImmutableSet.Builder<ClassInfo> builder = ImmutableSet.builder();
    for (UnmodifiableIterator<ClassInfo> unmodifiableIterator = getTopLevelClasses().iterator(); unmodifiableIterator.hasNext(); ) { ClassInfo classInfo = unmodifiableIterator.next();
      if (classInfo.getName().startsWith(packagePrefix)) {
        builder.add(classInfo);
      } }
    
    return builder.build();
  }


  
  @Beta
  public static class ResourceInfo
  {
    private final String resourceName;

    
    final ClassLoader loader;


    
    static ResourceInfo of(String resourceName, ClassLoader loader) {
      if (resourceName.endsWith(".class")) {
        return new ClassPath.ClassInfo(resourceName, loader);
      }
      return new ResourceInfo(resourceName, loader);
    }

    
    ResourceInfo(String resourceName, ClassLoader loader) {
      this.resourceName = (String)Preconditions.checkNotNull(resourceName);
      this.loader = (ClassLoader)Preconditions.checkNotNull(loader);
    }








    
    public final URL url() {
      URL url = this.loader.getResource(this.resourceName);
      if (url == null) {
        throw new NoSuchElementException(this.resourceName);
      }
      return url;
    }







    
    public final ByteSource asByteSource() {
      return Resources.asByteSource(url());
    }








    
    public final CharSource asCharSource(Charset charset) {
      return Resources.asCharSource(url(), charset);
    }

    
    public final String getResourceName() {
      return this.resourceName;
    }

    
    public int hashCode() {
      return this.resourceName.hashCode();
    }

    
    public boolean equals(Object obj) {
      if (obj instanceof ResourceInfo) {
        ResourceInfo that = (ResourceInfo)obj;
        return (this.resourceName.equals(that.resourceName) && this.loader == that.loader);
      } 
      return false;
    }


    
    public String toString() {
      return this.resourceName;
    }
  }


  
  @Beta
  public static final class ClassInfo
    extends ResourceInfo
  {
    private final String className;

    
    ClassInfo(String resourceName, ClassLoader loader) {
      super(resourceName, loader);
      this.className = ClassPath.getClassName(resourceName);
    }






    
    public String getPackageName() {
      return Reflection.getPackageName(this.className);
    }






    
    public String getSimpleName() {
      int lastDollarSign = this.className.lastIndexOf('$');
      if (lastDollarSign != -1) {
        String innerClassName = this.className.substring(lastDollarSign + 1);

        
        return CharMatcher.digit().trimLeadingFrom(innerClassName);
      } 
      String packageName = getPackageName();
      if (packageName.isEmpty()) {
        return this.className;
      }

      
      return this.className.substring(packageName.length() + 1);
    }






    
    public String getName() {
      return this.className;
    }






    
    public Class<?> load() {
      try {
        return this.loader.loadClass(this.className);
      } catch (ClassNotFoundException e) {
        
        throw new IllegalStateException(e);
      } 
    }

    
    public String toString() {
      return this.className;
    }
  }







  
  static abstract class Scanner
  {
    private final Set<File> scannedUris = Sets.newHashSet();
    
    public final void scan(ClassLoader classloader) throws IOException {
      for (UnmodifiableIterator<Map.Entry<File, ClassLoader>> unmodifiableIterator = getClassPathEntries(classloader).entrySet().iterator(); unmodifiableIterator.hasNext(); ) { Map.Entry<File, ClassLoader> entry = unmodifiableIterator.next();
        scan(entry.getKey(), entry.getValue()); }
    
    }
    
    @VisibleForTesting
    final void scan(File file, ClassLoader classloader) throws IOException {
      if (this.scannedUris.add(file.getCanonicalFile())) {
        scanFrom(file, classloader);
      }
    }

    
    protected abstract void scanDirectory(ClassLoader param1ClassLoader, File param1File) throws IOException;

    
    protected abstract void scanJarFile(ClassLoader param1ClassLoader, JarFile param1JarFile) throws IOException;
    
    private void scanFrom(File file, ClassLoader classloader) throws IOException {
      try {
        if (!file.exists()) {
          return;
        }
      } catch (SecurityException e) {
        ClassPath.logger.warning("Cannot access " + file + ": " + e);
        
        return;
      } 
      if (file.isDirectory()) {
        scanDirectory(classloader, file);
      } else {
        scanJar(file, classloader);
      } 
    }
    
    private void scanJar(File file, ClassLoader classloader) throws IOException {
      JarFile jarFile;
      try {
        jarFile = new JarFile(file);
      } catch (IOException e) {
        return;
      } 
      
      try {
        for (UnmodifiableIterator<File> unmodifiableIterator = getClassPathFromManifest(file, jarFile.getManifest()).iterator(); unmodifiableIterator.hasNext(); ) { File path = unmodifiableIterator.next();
          scan(path, classloader); }
        
        scanJarFile(classloader, jarFile);
      } finally {
        try {
          jarFile.close();
        } catch (IOException iOException) {}
      } 
    }








    
    @VisibleForTesting
    static ImmutableSet<File> getClassPathFromManifest(File jarFile, Manifest manifest) {
      if (manifest == null) {
        return ImmutableSet.of();
      }
      ImmutableSet.Builder<File> builder = ImmutableSet.builder();
      
      String classpathAttribute = manifest.getMainAttributes().getValue(Attributes.Name.CLASS_PATH.toString());
      if (classpathAttribute != null) {
        for (String path : ClassPath.CLASS_PATH_ATTRIBUTE_SEPARATOR.split(classpathAttribute)) {
          URL url;
          try {
            url = getClassPathEntry(jarFile, path);
          } catch (MalformedURLException e) {
            
            ClassPath.logger.warning("Invalid Class-Path entry: " + path);
            continue;
          } 
          if (url.getProtocol().equals("file")) {
            builder.add(ClassPath.toFile(url));
          }
        } 
      }
      return builder.build();
    }
    
    @VisibleForTesting
    static ImmutableMap<File, ClassLoader> getClassPathEntries(ClassLoader classloader) {
      LinkedHashMap<File, ClassLoader> entries = Maps.newLinkedHashMap();
      
      ClassLoader parent = classloader.getParent();
      if (parent != null) {
        entries.putAll((Map<? extends File, ? extends ClassLoader>)getClassPathEntries(parent));
      }
      for (UnmodifiableIterator<URL> unmodifiableIterator = getClassLoaderUrls(classloader).iterator(); unmodifiableIterator.hasNext(); ) { URL url = unmodifiableIterator.next();
        if (url.getProtocol().equals("file")) {
          File file = ClassPath.toFile(url);
          if (!entries.containsKey(file)) {
            entries.put(file, classloader);
          }
        }  }
      
      return ImmutableMap.copyOf(entries);
    }
    
    private static ImmutableList<URL> getClassLoaderUrls(ClassLoader classloader) {
      if (classloader instanceof URLClassLoader) {
        return ImmutableList.copyOf((Object[])((URLClassLoader)classloader).getURLs());
      }
      if (classloader.equals(ClassLoader.getSystemClassLoader())) {
        return parseJavaClassPath();
      }
      return ImmutableList.of();
    }




    
    @VisibleForTesting
    static ImmutableList<URL> parseJavaClassPath() {
      ImmutableList.Builder<URL> urls = ImmutableList.builder();
      for (String entry : Splitter.on(StandardSystemProperty.PATH_SEPARATOR.value()).split(StandardSystemProperty.JAVA_CLASS_PATH.value())) {
        try {
          try {
            urls.add((new File(entry)).toURI().toURL());
          } catch (SecurityException e) {
            urls.add(new URL("file", null, (new File(entry)).getAbsolutePath()));
          } 
        } catch (MalformedURLException e) {
          ClassPath.logger.log(Level.WARNING, "malformed classpath entry: " + entry, e);
        } 
      } 
      return urls.build();
    }






    
    @VisibleForTesting
    static URL getClassPathEntry(File jarFile, String path) throws MalformedURLException {
      return new URL(jarFile.toURI().toURL(), path);
    }
  }
  
  @VisibleForTesting
  static final class DefaultScanner
    extends Scanner {
    private final SetMultimap<ClassLoader, String> resources = MultimapBuilder.hashKeys().linkedHashSetValues().build();
    
    ImmutableSet<ClassPath.ResourceInfo> getResources() {
      ImmutableSet.Builder<ClassPath.ResourceInfo> builder = ImmutableSet.builder();
      for (Map.Entry<ClassLoader, String> entry : (Iterable<Map.Entry<ClassLoader, String>>)this.resources.entries()) {
        builder.add(ClassPath.ResourceInfo.of(entry.getValue(), entry.getKey()));
      }
      return builder.build();
    }

    
    protected void scanJarFile(ClassLoader classloader, JarFile file) {
      Enumeration<JarEntry> entries = file.entries();
      while (entries.hasMoreElements()) {
        JarEntry entry = entries.nextElement();
        if (entry.isDirectory() || entry.getName().equals("META-INF/MANIFEST.MF")) {
          continue;
        }
        this.resources.get(classloader).add(entry.getName());
      } 
    }

    
    protected void scanDirectory(ClassLoader classloader, File directory) throws IOException {
      Set<File> currentPath = new HashSet<>();
      currentPath.add(directory.getCanonicalFile());
      scanDirectory(directory, classloader, "", currentPath);
    }














    
    private void scanDirectory(File directory, ClassLoader classloader, String packagePrefix, Set<File> currentPath) throws IOException {
      File[] files = directory.listFiles();
      if (files == null) {
        ClassPath.logger.warning("Cannot read directory " + directory);
        
        return;
      } 
      for (File f : files) {
        String name = f.getName();
        if (f.isDirectory()) {
          File deref = f.getCanonicalFile();
          if (currentPath.add(deref)) {
            scanDirectory(deref, classloader, packagePrefix + name + "/", currentPath);
            currentPath.remove(deref);
          } 
        } else {
          String resourceName = packagePrefix + name;
          if (!resourceName.equals("META-INF/MANIFEST.MF")) {
            this.resources.get(classloader).add(resourceName);
          }
        } 
      } 
    }
  }
  
  @VisibleForTesting
  static String getClassName(String filename) {
    int classNameEnd = filename.length() - ".class".length();
    return filename.substring(0, classNameEnd).replace('/', '.');
  }

  
  @VisibleForTesting
  static File toFile(URL url) {
    Preconditions.checkArgument(url.getProtocol().equals("file"));
    try {
      return new File(url.toURI());
    } catch (URISyntaxException e) {
      return new File(url.getPath());
    } 
  }
}
