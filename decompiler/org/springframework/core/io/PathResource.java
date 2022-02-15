package org.springframework.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;












































public class PathResource
  extends AbstractResource
  implements WritableResource
{
  private final Path path;
  
  public PathResource(Path path) {
    Assert.notNull(path, "Path must not be null");
    this.path = path.normalize();
  }








  
  public PathResource(String path) {
    Assert.notNull(path, "Path must not be null");
    this.path = Paths.get(path, new String[0]).normalize();
  }








  
  public PathResource(URI uri) {
    Assert.notNull(uri, "URI must not be null");
    this.path = Paths.get(uri).normalize();
  }




  
  public final String getPath() {
    return this.path.toString();
  }





  
  public boolean exists() {
    return Files.exists(this.path, new java.nio.file.LinkOption[0]);
  }







  
  public boolean isReadable() {
    return (Files.isReadable(this.path) && !Files.isDirectory(this.path, new java.nio.file.LinkOption[0]));
  }





  
  public InputStream getInputStream() throws IOException {
    if (!exists()) {
      throw new FileNotFoundException(getPath() + " (no such file or directory)");
    }
    if (Files.isDirectory(this.path, new java.nio.file.LinkOption[0])) {
      throw new FileNotFoundException(getPath() + " (is a directory)");
    }
    return Files.newInputStream(this.path, new OpenOption[0]);
  }







  
  public boolean isWritable() {
    return (Files.isWritable(this.path) && !Files.isDirectory(this.path, new java.nio.file.LinkOption[0]));
  }





  
  public OutputStream getOutputStream() throws IOException {
    if (Files.isDirectory(this.path, new java.nio.file.LinkOption[0])) {
      throw new FileNotFoundException(getPath() + " (is a directory)");
    }
    return Files.newOutputStream(this.path, new OpenOption[0]);
  }






  
  public URL getURL() throws IOException {
    return this.path.toUri().toURL();
  }





  
  public URI getURI() throws IOException {
    return this.path.toUri();
  }




  
  public boolean isFile() {
    return true;
  }




  
  public File getFile() throws IOException {
    try {
      return this.path.toFile();
    }
    catch (UnsupportedOperationException ex) {

      
      throw new FileNotFoundException(this.path + " cannot be resolved to absolute file path");
    } 
  }





  
  public ReadableByteChannel readableChannel() throws IOException {
    try {
      return Files.newByteChannel(this.path, new OpenOption[] { StandardOpenOption.READ });
    }
    catch (NoSuchFileException ex) {
      throw new FileNotFoundException(ex.getMessage());
    } 
  }





  
  public WritableByteChannel writableChannel() throws IOException {
    return Files.newByteChannel(this.path, new OpenOption[] { StandardOpenOption.WRITE });
  }




  
  public long contentLength() throws IOException {
    return Files.size(this.path);
  }







  
  public long lastModified() throws IOException {
    return Files.getLastModifiedTime(this.path, new java.nio.file.LinkOption[0]).toMillis();
  }






  
  public Resource createRelative(String relativePath) {
    return new PathResource(this.path.resolve(relativePath));
  }





  
  public String getFilename() {
    return this.path.getFileName().toString();
  }

  
  public String getDescription() {
    return "path [" + this.path.toAbsolutePath() + "]";
  }





  
  public boolean equals(@Nullable Object other) {
    return (this == other || (other instanceof PathResource && this.path
      .equals(((PathResource)other).path)));
  }




  
  public int hashCode() {
    return this.path.hashCode();
  }
}
