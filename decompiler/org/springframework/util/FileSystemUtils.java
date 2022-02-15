package org.springframework.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.util.EnumSet;
import org.springframework.lang.Nullable;







































public abstract class FileSystemUtils
{
  public static boolean deleteRecursively(@Nullable File root) {
    if (root == null) {
      return false;
    }
    
    try {
      return deleteRecursively(root.toPath());
    }
    catch (IOException ex) {
      return false;
    } 
  }









  
  public static boolean deleteRecursively(@Nullable Path root) throws IOException {
    if (root == null) {
      return false;
    }
    if (!Files.exists(root, new java.nio.file.LinkOption[0])) {
      return false;
    }
    
    Files.walkFileTree(root, new SimpleFileVisitor<Path>()
        {
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
          }
          
          public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
          }
        });
    return true;
  }







  
  public static void copyRecursively(File src, File dest) throws IOException {
    Assert.notNull(src, "Source File must not be null");
    Assert.notNull(dest, "Destination File must not be null");
    copyRecursively(src.toPath(), dest.toPath());
  }








  
  public static void copyRecursively(final Path src, final Path dest) throws IOException {
    Assert.notNull(src, "Source Path must not be null");
    Assert.notNull(dest, "Destination Path must not be null");
    BasicFileAttributes srcAttr = Files.readAttributes(src, BasicFileAttributes.class, new java.nio.file.LinkOption[0]);
    
    if (srcAttr.isDirectory()) {
      Files.walkFileTree(src, EnumSet.of(FileVisitOption.FOLLOW_LINKS), 2147483647, new SimpleFileVisitor<Path>()
          {
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
              Files.createDirectories(dest.resolve(src.relativize(dir)), (FileAttribute<?>[])new FileAttribute[0]);
              return FileVisitResult.CONTINUE;
            }
            
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
              Files.copy(file, dest.resolve(src.relativize(file)), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
              return FileVisitResult.CONTINUE;
            }
          });
    }
    else if (srcAttr.isRegularFile()) {
      Files.copy(src, dest, new CopyOption[0]);
    } else {
      
      throw new IllegalArgumentException("Source File must denote a directory or file");
    } 
  }
}
