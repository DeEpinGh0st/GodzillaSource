package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.TreeTraverser;
import com.google.common.graph.SuccessorsFunction;
import com.google.common.graph.Traverser;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;











































@GwtIncompatible
public final class Files
{
  private static final int TEMP_DIR_ATTEMPTS = 10000;
  
  @Beta
  public static BufferedReader newReader(File file, Charset charset) throws FileNotFoundException {
    Preconditions.checkNotNull(file);
    Preconditions.checkNotNull(charset);
    return new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
  }












  
  @Beta
  public static BufferedWriter newWriter(File file, Charset charset) throws FileNotFoundException {
    Preconditions.checkNotNull(file);
    Preconditions.checkNotNull(charset);
    return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
  }





  
  public static ByteSource asByteSource(File file) {
    return new FileByteSource(file);
  }
  
  private static final class FileByteSource
    extends ByteSource {
    private final File file;
    
    private FileByteSource(File file) {
      this.file = (File)Preconditions.checkNotNull(file);
    }

    
    public FileInputStream openStream() throws IOException {
      return new FileInputStream(this.file);
    }

    
    public Optional<Long> sizeIfKnown() {
      if (this.file.isFile()) {
        return Optional.of(Long.valueOf(this.file.length()));
      }
      return Optional.absent();
    }


    
    public long size() throws IOException {
      if (!this.file.isFile()) {
        throw new FileNotFoundException(this.file.toString());
      }
      return this.file.length();
    }

    
    public byte[] read() throws IOException {
      Closer closer = Closer.create();
      try {
        FileInputStream in = closer.<FileInputStream>register(openStream());
        return ByteStreams.toByteArray(in, in.getChannel().size());
      } catch (Throwable e) {
        throw closer.rethrow(e);
      } finally {
        closer.close();
      } 
    }

    
    public String toString() {
      return "Files.asByteSource(" + this.file + ")";
    }
  }








  
  public static ByteSink asByteSink(File file, FileWriteMode... modes) {
    return new FileByteSink(file, modes);
  }
  
  private static final class FileByteSink
    extends ByteSink {
    private final File file;
    private final ImmutableSet<FileWriteMode> modes;
    
    private FileByteSink(File file, FileWriteMode... modes) {
      this.file = (File)Preconditions.checkNotNull(file);
      this.modes = ImmutableSet.copyOf((Object[])modes);
    }

    
    public FileOutputStream openStream() throws IOException {
      return new FileOutputStream(this.file, this.modes.contains(FileWriteMode.APPEND));
    }

    
    public String toString() {
      return "Files.asByteSink(" + this.file + ", " + this.modes + ")";
    }
  }






  
  public static CharSource asCharSource(File file, Charset charset) {
    return asByteSource(file).asCharSource(charset);
  }








  
  public static CharSink asCharSink(File file, Charset charset, FileWriteMode... modes) {
    return asByteSink(file, modes).asCharSink(charset);
  }











  
  @Beta
  public static byte[] toByteArray(File file) throws IOException {
    return asByteSource(file).read();
  }











  
  @Deprecated
  @Beta
  public static String toString(File file, Charset charset) throws IOException {
    return asCharSource(file, charset).read();
  }










  
  @Beta
  public static void write(byte[] from, File to) throws IOException {
    asByteSink(to, new FileWriteMode[0]).write(from);
  }











  
  @Deprecated
  @Beta
  public static void write(CharSequence from, File to, Charset charset) throws IOException {
    asCharSink(to, charset, new FileWriteMode[0]).write(from);
  }










  
  @Beta
  public static void copy(File from, OutputStream to) throws IOException {
    asByteSource(from).copyTo(to);
  }



















  
  @Beta
  public static void copy(File from, File to) throws IOException {
    Preconditions.checkArgument(!from.equals(to), "Source %s and destination %s must be different", from, to);
    asByteSource(from).copyTo(asByteSink(to, new FileWriteMode[0]));
  }












  
  @Deprecated
  @Beta
  public static void copy(File from, Charset charset, Appendable to) throws IOException {
    asCharSource(from, charset).copyTo(to);
  }












  
  @Deprecated
  @Beta
  public static void append(CharSequence from, File to, Charset charset) throws IOException {
    asCharSink(to, charset, new FileWriteMode[] { FileWriteMode.APPEND }).write(from);
  }





  
  @Beta
  public static boolean equal(File file1, File file2) throws IOException {
    Preconditions.checkNotNull(file1);
    Preconditions.checkNotNull(file2);
    if (file1 == file2 || file1.equals(file2)) {
      return true;
    }





    
    long len1 = file1.length();
    long len2 = file2.length();
    if (len1 != 0L && len2 != 0L && len1 != len2) {
      return false;
    }
    return asByteSource(file1).contentEquals(asByteSource(file2));
  }



















  
  @Beta
  public static File createTempDir() {
    File baseDir = new File(System.getProperty("java.io.tmpdir"));
    
    String baseName = System.currentTimeMillis() + "-";
    
    for (int counter = 0; counter < 10000; counter++) {
      File tempDir = new File(baseDir, baseName + counter);
      if (tempDir.mkdir()) {
        return tempDir;
      }
    } 
    throw new IllegalStateException("Failed to create directory within 10000 attempts (tried " + baseName + "0 to " + baseName + '✏' + ')');
  }
















  
  @Beta
  public static void touch(File file) throws IOException {
    Preconditions.checkNotNull(file);
    if (!file.createNewFile() && !file.setLastModified(System.currentTimeMillis())) {
      throw new IOException("Unable to update modification time of " + file);
    }
  }









  
  @Beta
  public static void createParentDirs(File file) throws IOException {
    Preconditions.checkNotNull(file);
    File parent = file.getCanonicalFile().getParentFile();
    if (parent == null) {
      return;
    }





    
    parent.mkdirs();
    if (!parent.isDirectory()) {
      throw new IOException("Unable to create parent directories of " + file);
    }
  }












  
  @Beta
  public static void move(File from, File to) throws IOException {
    Preconditions.checkNotNull(from);
    Preconditions.checkNotNull(to);
    Preconditions.checkArgument(!from.equals(to), "Source %s and destination %s must be different", from, to);
    
    if (!from.renameTo(to)) {
      copy(from, to);
      if (!from.delete()) {
        if (!to.delete()) {
          throw new IOException("Unable to delete " + to);
        }
        throw new IOException("Unable to delete " + from);
      } 
    } 
  }













  
  @Deprecated
  @Beta
  public static String readFirstLine(File file, Charset charset) throws IOException {
    return asCharSource(file, charset).readFirstLine();
  }


















  
  @Beta
  public static List<String> readLines(File file, Charset charset) throws IOException {
    return asCharSource(file, charset)
      .<List<String>>readLines(new LineProcessor<List<String>>()
        {
          final List<String> result = Lists.newArrayList();

          
          public boolean processLine(String line) {
            this.result.add(line);
            return true;
          }

          
          public List<String> getResult() {
            return this.result;
          }
        });
  }














  
  @Deprecated
  @Beta
  @CanIgnoreReturnValue
  public static <T> T readLines(File file, Charset charset, LineProcessor<T> callback) throws IOException {
    return asCharSource(file, charset).readLines(callback);
  }













  
  @Deprecated
  @Beta
  @CanIgnoreReturnValue
  public static <T> T readBytes(File file, ByteProcessor<T> processor) throws IOException {
    return asByteSource(file).read(processor);
  }












  
  @Deprecated
  @Beta
  public static HashCode hash(File file, HashFunction hashFunction) throws IOException {
    return asByteSource(file).hash(hashFunction);
  }















  
  @Beta
  public static MappedByteBuffer map(File file) throws IOException {
    Preconditions.checkNotNull(file);
    return map(file, FileChannel.MapMode.READ_ONLY);
  }

















  
  @Beta
  public static MappedByteBuffer map(File file, FileChannel.MapMode mode) throws IOException {
    return mapInternal(file, mode, -1L);
  }



















  
  @Beta
  public static MappedByteBuffer map(File file, FileChannel.MapMode mode, long size) throws IOException {
    Preconditions.checkArgument((size >= 0L), "size (%s) may not be negative", size);
    return mapInternal(file, mode, size);
  }

  
  private static MappedByteBuffer mapInternal(File file, FileChannel.MapMode mode, long size) throws IOException {
    Preconditions.checkNotNull(file);
    Preconditions.checkNotNull(mode);
    
    Closer closer = Closer.create();
    
    try {
      RandomAccessFile raf = closer.<RandomAccessFile>register(new RandomAccessFile(file, (mode == FileChannel.MapMode.READ_ONLY) ? "r" : "rw"));
      FileChannel channel = closer.<FileChannel>register(raf.getChannel());
      return channel.map(mode, 0L, (size == -1L) ? channel.size() : size);
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    } 
  }




















  
  @Beta
  public static String simplifyPath(String pathname) {
    Preconditions.checkNotNull(pathname);
    if (pathname.length() == 0) {
      return ".";
    }

    
    Iterable<String> components = Splitter.on('/').omitEmptyStrings().split(pathname);
    List<String> path = new ArrayList<>();

    
    for (String component : components) {
      switch (component) {
        case ".":
          continue;
        case "..":
          if (path.size() > 0 && !((String)path.get(path.size() - 1)).equals("..")) {
            path.remove(path.size() - 1); continue;
          } 
          path.add("..");
          continue;
      } 
      
      path.add(component);
    } 



    
    String result = Joiner.on('/').join(path);
    if (pathname.charAt(0) == '/') {
      result = "/" + result;
    }
    
    while (result.startsWith("/../")) {
      result = result.substring(3);
    }
    if (result.equals("/..")) {
      result = "/";
    } else if ("".equals(result)) {
      result = ".";
    } 
    
    return result;
  }














  
  @Beta
  public static String getFileExtension(String fullName) {
    Preconditions.checkNotNull(fullName);
    String fileName = (new File(fullName)).getName();
    int dotIndex = fileName.lastIndexOf('.');
    return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
  }










  
  @Beta
  public static String getNameWithoutExtension(String file) {
    Preconditions.checkNotNull(file);
    String fileName = (new File(file)).getName();
    int dotIndex = fileName.lastIndexOf('.');
    return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
  }












  
  @Deprecated
  static TreeTraverser<File> fileTreeTraverser() {
    return FILE_TREE_TRAVERSER;
  }
  
  private static final TreeTraverser<File> FILE_TREE_TRAVERSER = new TreeTraverser<File>()
    {
      public Iterable<File> children(File file)
      {
        return Files.fileTreeChildren(file);
      }

      
      public String toString() {
        return "Files.fileTreeTraverser()";
      }
    };






















  
  @Beta
  public static Traverser<File> fileTraverser() {
    return Traverser.forTree(FILE_TREE);
  }
  
  private static final SuccessorsFunction<File> FILE_TREE = new SuccessorsFunction<File>()
    {
      public Iterable<File> successors(File file)
      {
        return Files.fileTreeChildren(file);
      }
    };

  
  private static Iterable<File> fileTreeChildren(File file) {
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      if (files != null) {
        return Collections.unmodifiableList(Arrays.asList(files));
      }
    } 
    
    return Collections.emptyList();
  }





  
  @Beta
  public static Predicate<File> isDirectory() {
    return FilePredicate.IS_DIRECTORY;
  }





  
  @Beta
  public static Predicate<File> isFile() {
    return FilePredicate.IS_FILE;
  }
  
  private enum FilePredicate implements Predicate<File> {
    IS_DIRECTORY
    {
      public boolean apply(File file) {
        return file.isDirectory();
      }

      
      public String toString() {
        return "Files.isDirectory()";
      }
    },
    
    IS_FILE
    {
      public boolean apply(File file) {
        return file.isFile();
      }

      
      public String toString() {
        return "Files.isFile()";
      }
    };
  }
}
