package org.springframework.core.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import org.springframework.lang.Nullable;







































public class FileUrlResource
  extends UrlResource
  implements WritableResource
{
  @Nullable
  private volatile File file;
  
  public FileUrlResource(URL url) {
    super(url);
  }









  
  public FileUrlResource(String location) throws MalformedURLException {
    super("file", location);
  }


  
  public File getFile() throws IOException {
    File file = this.file;
    if (file != null) {
      return file;
    }
    file = super.getFile();
    this.file = file;
    return file;
  }

  
  public boolean isWritable() {
    try {
      File file = getFile();
      return (file.canWrite() && !file.isDirectory());
    }
    catch (IOException ex) {
      return false;
    } 
  }

  
  public OutputStream getOutputStream() throws IOException {
    return Files.newOutputStream(getFile().toPath(), new OpenOption[0]);
  }

  
  public WritableByteChannel writableChannel() throws IOException {
    return FileChannel.open(getFile().toPath(), new OpenOption[] { StandardOpenOption.WRITE });
  }

  
  public Resource createRelative(String relativePath) throws MalformedURLException {
    return new FileUrlResource(createRelativeURL(relativePath));
  }
}
