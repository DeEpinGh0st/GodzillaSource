package org.springframework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import org.springframework.lang.Nullable;














































public abstract class FileCopyUtils
{
  public static final int BUFFER_SIZE = 4096;
  
  public static int copy(File in, File out) throws IOException {
    Assert.notNull(in, "No input File specified");
    Assert.notNull(out, "No output File specified");
    return copy(Files.newInputStream(in.toPath(), new java.nio.file.OpenOption[0]), Files.newOutputStream(out.toPath(), new java.nio.file.OpenOption[0]));
  }






  
  public static void copy(byte[] in, File out) throws IOException {
    Assert.notNull(in, "No input byte array specified");
    Assert.notNull(out, "No output File specified");
    copy(new ByteArrayInputStream(in), Files.newOutputStream(out.toPath(), new java.nio.file.OpenOption[0]));
  }






  
  public static byte[] copyToByteArray(File in) throws IOException {
    Assert.notNull(in, "No input File specified");
    return copyToByteArray(Files.newInputStream(in.toPath(), new java.nio.file.OpenOption[0]));
  }













  
  public static int copy(InputStream in, OutputStream out) throws IOException {
    Assert.notNull(in, "No InputStream specified");
    Assert.notNull(out, "No OutputStream specified");
    
    try {
      return StreamUtils.copy(in, out);
    } finally {
      
      close(in);
      close(out);
    } 
  }







  
  public static void copy(byte[] in, OutputStream out) throws IOException {
    Assert.notNull(in, "No input byte array specified");
    Assert.notNull(out, "No OutputStream specified");
    
    try {
      out.write(in);
    } finally {
      
      close(out);
    } 
  }







  
  public static byte[] copyToByteArray(@Nullable InputStream in) throws IOException {
    if (in == null) {
      return new byte[0];
    }
    
    ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
    copy(in, out);
    return out.toByteArray();
  }













  
  public static int copy(Reader in, Writer out) throws IOException {
    Assert.notNull(in, "No Reader specified");
    Assert.notNull(out, "No Writer specified");
    
    try {
      int charCount = 0;
      char[] buffer = new char[4096];
      int charsRead;
      while ((charsRead = in.read(buffer)) != -1) {
        out.write(buffer, 0, charsRead);
        charCount += charsRead;
      } 
      out.flush();
      return charCount;
    } finally {
      
      close(in);
      close(out);
    } 
  }







  
  public static void copy(String in, Writer out) throws IOException {
    Assert.notNull(in, "No input String specified");
    Assert.notNull(out, "No Writer specified");
    
    try {
      out.write(in);
    } finally {
      
      close(out);
    } 
  }







  
  public static String copyToString(@Nullable Reader in) throws IOException {
    if (in == null) {
      return "";
    }
    
    StringWriter out = new StringWriter(4096);
    copy(in, out);
    return out.toString();
  }





  
  private static void close(Closeable closeable) {
    try {
      closeable.close();
    }
    catch (IOException iOException) {}
  }
}
