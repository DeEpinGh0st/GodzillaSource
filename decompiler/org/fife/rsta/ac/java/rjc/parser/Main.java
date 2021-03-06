package org.fife.rsta.ac.java.rjc.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
























public class Main
{
  public static final String PROPERTY_NO_OUTPUT = "no.output";
  private static final boolean LOG = !"true".equals(
      System.getProperty("no.output"));

  
  private static void log(Object text) {
    if (LOG) {
      System.out.println(text);
    }
  }








  
  public static void main(String[] args) throws IOException {
    PrintStream oldOut = System.out;
    PrintStream oldErr = System.err;



    
    ASTFactory fact = new ASTFactory();

    
    List<File> toDo = new ArrayList<>();
    
    if (args.length > 0) {
      toDo.add(new File(args[0]));
    }
    else {
      
      File rootDir = new File("C:/java/32/jdk1.6.0_16/src/");

      
      File[] files = rootDir.listFiles();
      Collections.addAll(toDo, files);
    } 
    
    int count = 0;
    int typeParamCount = 0;
    int annotationTypeDecCount = 0;
    long entireStart = System.currentTimeMillis();
    
    for (int i = 0; i < toDo.size(); i++) {
      
      File file = toDo.get(i);
      
      if (file.isDirectory()) {
        File[] contents = file.listFiles();
        Collections.addAll(toDo, contents);
      
      }
      else if (file.getName().endsWith(".java")) {


        
        BufferedReader r = new BufferedReader(new FileReader(file));
        Scanner scanner = new Scanner(r);
        long start = System.currentTimeMillis();
        try {
          fact.getCompilationUnit(file.getName(), scanner);
          long time = System.currentTimeMillis() - start;
          
          log(file.getAbsolutePath() + " (" + file.length() + "): " + time + " ms");
        } catch (InternalError ie) {
          System.err.println(file.getAbsolutePath());
          ie.printStackTrace();
          System.exit(1);
        } 
        count++;
        r.close();
      } 
    } 
    
    long entireTime = System.currentTimeMillis() - entireStart;
    log(count + " files parsed");
    log("TypeParameter errors: " + typeParamCount);
    log("AnnotationTypeDeclaration errors: " + annotationTypeDecCount);
    log(entireTime + " ms");
    System.setOut(oldOut);
    System.setErr(oldErr);
  }
}
