package javassist.tools.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.Translator;































public class Webserver
{
  private ServerSocket socket;
  private ClassPool classPool;
  protected Translator translator;
  private static final byte[] endofline = new byte[] { 13, 10 };

  
  private static final int typeHtml = 1;

  
  private static final int typeClass = 2;
  
  private static final int typeGif = 3;
  
  private static final int typeJpeg = 4;
  
  private static final int typeText = 5;
  
  public String debugDir = null;














  
  public String htmlfileBase = null;




  
  public static void main(String[] args) throws IOException {
    if (args.length == 1) {
      Webserver web = new Webserver(args[0]);
      web.run();
    } else {
      
      System.err.println("Usage: java javassist.tools.web.Webserver <port number>");
    } 
  }





  
  public Webserver(String port) throws IOException {
    this(Integer.parseInt(port));
  }





  
  public Webserver(int port) throws IOException {
    this.socket = new ServerSocket(port);
    this.classPool = null;
    this.translator = null;
  }




  
  public void setClassPool(ClassPool loader) {
    this.classPool = loader;
  }










  
  public void addTranslator(ClassPool cp, Translator t) throws NotFoundException, CannotCompileException {
    this.classPool = cp;
    this.translator = t;
    t.start(this.classPool);
  }



  
  public void end() throws IOException {
    this.socket.close();
  }



  
  public void logging(String msg) {
    System.out.println(msg);
  }



  
  public void logging(String msg1, String msg2) {
    System.out.print(msg1);
    System.out.print(" ");
    System.out.println(msg2);
  }



  
  public void logging(String msg1, String msg2, String msg3) {
    System.out.print(msg1);
    System.out.print(" ");
    System.out.print(msg2);
    System.out.print(" ");
    System.out.println(msg3);
  }



  
  public void logging2(String msg) {
    System.out.print("    ");
    System.out.println(msg);
  }



  
  public void run() {
    System.err.println("ready to service..."); while (true) {
      try {
        while (true) {
          ServiceThread th = new ServiceThread(this, this.socket.accept());
          th.start();
        }  break;
      } catch (IOException e) {
        logging(e.toString());
      } 
    } 
  }
  final void process(Socket clnt) throws IOException {
    InputStream in = new BufferedInputStream(clnt.getInputStream());
    String cmd = readLine(in);
    logging(clnt.getInetAddress().getHostName(), (new Date())
        .toString(), cmd);
    while (skipLine(in) > 0);

    
    OutputStream out = new BufferedOutputStream(clnt.getOutputStream());
    try {
      doReply(in, out, cmd);
    }
    catch (BadHttpRequest e) {
      replyError(out, e);
    } 
    
    out.flush();
    in.close();
    out.close();
    clnt.close();
  }
  
  private String readLine(InputStream in) throws IOException {
    StringBuffer buf = new StringBuffer();
    int c;
    while ((c = in.read()) >= 0 && c != 13) {
      buf.append((char)c);
    }
    in.read();
    return buf.toString();
  }

  
  private int skipLine(InputStream in) throws IOException {
    int len = 0; int c;
    while ((c = in.read()) >= 0 && c != 13) {
      len++;
    }
    in.read();
    return len;
  }










  
  public void doReply(InputStream in, OutputStream out, String cmd) throws IOException, BadHttpRequest {
    int fileType;
    String filename, urlName;
    if (cmd.startsWith("GET /")) {
      filename = urlName = cmd.substring(5, cmd.indexOf(' ', 5));
    } else {
      throw new BadHttpRequest();
    } 
    if (filename.endsWith(".class")) {
      fileType = 2;
    } else if (filename.endsWith(".html") || filename.endsWith(".htm")) {
      fileType = 1;
    } else if (filename.endsWith(".gif")) {
      fileType = 3;
    } else if (filename.endsWith(".jpg")) {
      fileType = 4;
    } else {
      fileType = 5;
    } 
    int len = filename.length();
    if (fileType == 2 && 
      letUsersSendClassfile(out, filename, len)) {
      return;
    }
    checkFilename(filename, len);
    if (this.htmlfileBase != null) {
      filename = this.htmlfileBase + filename;
    }
    if (File.separatorChar != '/') {
      filename = filename.replace('/', File.separatorChar);
    }
    File file = new File(filename);
    if (file.canRead()) {
      sendHeader(out, file.length(), fileType);
      FileInputStream fin = new FileInputStream(file);
      byte[] filebuffer = new byte[4096];
      while (true) {
        len = fin.read(filebuffer);
        if (len <= 0)
          break; 
        out.write(filebuffer, 0, len);
      } 
      
      fin.close();

      
      return;
    } 

    
    if (fileType == 2) {
      
      InputStream fin = getClass().getResourceAsStream("/" + urlName);
      if (fin != null) {
        ByteArrayOutputStream barray = new ByteArrayOutputStream();
        byte[] filebuffer = new byte[4096];
        while (true) {
          len = fin.read(filebuffer);
          if (len <= 0)
            break; 
          barray.write(filebuffer, 0, len);
        } 
        
        byte[] classfile = barray.toByteArray();
        sendHeader(out, classfile.length, 2);
        out.write(classfile);
        fin.close();
        
        return;
      } 
    } 
    throw new BadHttpRequest();
  }


  
  private void checkFilename(String filename, int len) throws BadHttpRequest {
    for (int i = 0; i < len; i++) {
      char c = filename.charAt(i);
      if (!Character.isJavaIdentifierPart(c) && c != '.' && c != '/') {
        throw new BadHttpRequest();
      }
    } 
    if (filename.indexOf("..") >= 0) {
      throw new BadHttpRequest();
    }
  }

  
  private boolean letUsersSendClassfile(OutputStream out, String filename, int length) throws IOException, BadHttpRequest {
    byte[] classfile;
    if (this.classPool == null) {
      return false;
    }

    
    String classname = filename.substring(0, length - 6).replace('/', '.');
    try {
      if (this.translator != null) {
        this.translator.onLoad(this.classPool, classname);
      }
      CtClass c = this.classPool.get(classname);
      classfile = c.toBytecode();
      if (this.debugDir != null) {
        c.writeFile(this.debugDir);
      }
    } catch (Exception e) {
      throw new BadHttpRequest(e);
    } 
    
    sendHeader(out, classfile.length, 2);
    out.write(classfile);
    return true;
  }


  
  private void sendHeader(OutputStream out, long dataLength, int filetype) throws IOException {
    out.write("HTTP/1.0 200 OK".getBytes());
    out.write(endofline);
    out.write("Content-Length: ".getBytes());
    out.write(Long.toString(dataLength).getBytes());
    out.write(endofline);
    if (filetype == 2) {
      out.write("Content-Type: application/octet-stream".getBytes());
    } else if (filetype == 1) {
      out.write("Content-Type: text/html".getBytes());
    } else if (filetype == 3) {
      out.write("Content-Type: image/gif".getBytes());
    } else if (filetype == 4) {
      out.write("Content-Type: image/jpg".getBytes());
    } else if (filetype == 5) {
      out.write("Content-Type: text/plain".getBytes());
    } 
    out.write(endofline);
    out.write(endofline);
  }


  
  private void replyError(OutputStream out, BadHttpRequest e) throws IOException {
    logging2("bad request: " + e.toString());
    out.write("HTTP/1.0 400 Bad Request".getBytes());
    out.write(endofline);
    out.write(endofline);
    out.write("<H1>Bad Request</H1>".getBytes());
  }
}
