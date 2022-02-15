package com.kitfox.svg.xml.cpx;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;









































public class CPXTest
{
  public CPXTest() {
    writeTest();
    readTest();
  }


  
  public void writeTest() {
    try {
      InputStream is = CPXTest.class.getResourceAsStream("/data/readme.txt");

      
      FileOutputStream fout = new FileOutputStream("C:\\tmp\\cpxFile.cpx");
      CPXOutputStream cout = new CPXOutputStream(fout);
      
      byte[] buffer = new byte[1024];
      int numBytes;
      while ((numBytes = is.read(buffer)) != -1)
      {
        cout.write(buffer, 0, numBytes);
      }
      cout.close();
    }
    catch (Exception e) {
      
      Logger.getLogger("svgSalamandeLogger").log(Level.WARNING, (String)null, e);
    } 
  }




  
  public void readTest() {
    try {
      FileInputStream is = new FileInputStream("C:\\tmp\\cpxFile.cpx");
      CPXInputStream cin = new CPXInputStream(is);
      
      BufferedReader br = new BufferedReader(new InputStreamReader(cin));
      String line;
      while ((line = br.readLine()) != null)
      {
        System.err.println(line);
      }
    }
    catch (Exception e) {
      
      Logger.getLogger("svgSalamandeLogger").log(Level.WARNING, (String)null, e);
    } 
  }




  
  public static void main(String[] args) {
    new CPXTest();
  }
}
