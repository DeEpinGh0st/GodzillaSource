package org.fife.ui.rsyntaxtextarea;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.fife.io.UnicodeReader;
import org.fife.io.UnicodeWriter;


































































































public class TextEditorPane
  extends RSyntaxTextArea
  implements DocumentListener
{
  private static final long serialVersionUID = 1L;
  public static final String FULL_PATH_PROPERTY = "TextEditorPane.fileFullPath";
  public static final String DIRTY_PROPERTY = "TextEditorPane.dirty";
  public static final String READ_ONLY_PROPERTY = "TextEditorPane.readOnly";
  public static final String ENCODING_PROPERTY = "TextEditorPane.encoding";
  private FileLocation loc;
  private String charSet;
  private boolean readOnly;
  private boolean dirty;
  private long lastSaveOrLoadTime;
  public static final long LAST_MODIFIED_UNKNOWN = 0L;
  private static final String DEFAULT_FILE_NAME = "Untitled.txt";
  
  public TextEditorPane() {
    this(0);
  }







  
  public TextEditorPane(int textMode) {
    this(textMode, false);
  }









  
  public TextEditorPane(int textMode, boolean wordWrapEnabled) {
    super(textMode);
    setLineWrap(wordWrapEnabled);
    try {
      init((FileLocation)null, (String)null);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } 
  }















  
  public TextEditorPane(int textMode, boolean wordWrapEnabled, FileLocation loc) throws IOException {
    this(textMode, wordWrapEnabled, loc, (String)null);
  }



















  
  public TextEditorPane(int textMode, boolean wordWrapEnabled, FileLocation loc, String defaultEnc) throws IOException {
    super(textMode);
    setLineWrap(wordWrapEnabled);
    init(loc, defaultEnc);
  }









  
  public void changedUpdate(DocumentEvent e) {}









  
  private static String getDefaultEncoding() {
    return Charset.defaultCharset().name();
  }







  
  public String getEncoding() {
    return this.charSet;
  }






  
  public String getFileFullPath() {
    return (this.loc == null) ? null : this.loc.getFileFullPath();
  }






  
  public String getFileName() {
    return (this.loc == null) ? null : this.loc.getFileName();
  }















  
  public long getLastSaveOrLoadTime() {
    return this.lastSaveOrLoadTime;
  }

















  
  public Object getLineSeparator() {
    return getDocument().getProperty("__EndOfLine__");
  }














  
  private void init(FileLocation loc, String defaultEnc) throws IOException {
    if (loc == null) {


      
      this.loc = FileLocation.create("Untitled.txt");
      this.charSet = (defaultEnc == null) ? getDefaultEncoding() : defaultEnc;


      
      setLineSeparator(System.getProperty("line.separator"));
    } else {
      
      load(loc, defaultEnc);
    } 
    
    if (this.loc.isLocalAndExists()) {
      File file = new File(this.loc.getFileFullPath());
      this.lastSaveOrLoadTime = file.lastModified();
      setReadOnly(!file.canWrite());
    } else {
      
      this.lastSaveOrLoadTime = 0L;
      setReadOnly(false);
    } 
    
    setDirty(false);
  }








  
  public void insertUpdate(DocumentEvent e) {
    if (!this.dirty) {
      setDirty(true);
    }
  }







  
  public boolean isDirty() {
    return this.dirty;
  }






  
  public boolean isLocal() {
    return this.loc.isLocal();
  }






  
  public boolean isLocalAndExists() {
    return this.loc.isLocalAndExists();
  }














  
  public boolean isModifiedOutsideEditor() {
    return (this.loc.getActualLastModified() > getLastSaveOrLoadTime());
  }







  
  public boolean isReadOnly() {
    return this.readOnly;
  }
















  
  public void load(FileLocation loc) throws IOException {
    load(loc, (String)null);
  }

















  
  public void load(FileLocation loc, Charset defaultEnc) throws IOException {
    load(loc, (defaultEnc == null) ? null : defaultEnc.name());
  }



















  
  public void load(FileLocation loc, String defaultEnc) throws IOException {
    if (loc.isLocal() && !loc.isLocalAndExists()) {
      this.charSet = (defaultEnc != null) ? defaultEnc : getDefaultEncoding();
      this.loc = loc;
      setText(null);
      discardAllEdits();
      setDirty(false);

      
      return;
    } 

    
    UnicodeReader ur = new UnicodeReader(loc.getInputStream(), defaultEnc);

    
    Document doc = getDocument();
    doc.removeDocumentListener(this);
    try (BufferedReader r = new BufferedReader((Reader)ur)) {
      read(r, null);
    } finally {
      doc.addDocumentListener(this);
    } 

    
    this.charSet = ur.getEncoding();
    String old = getFileFullPath();
    this.loc = loc;
    setDirty(false);
    setCaretPosition(0);
    discardAllEdits();
    firePropertyChange("TextEditorPane.fileFullPath", old, getFileFullPath());
  }



















  
  public void reload() throws IOException {
    String oldEncoding = getEncoding();
    UnicodeReader ur = new UnicodeReader(this.loc.getInputStream(), oldEncoding);
    String encoding = ur.getEncoding();
    try (BufferedReader r = new BufferedReader((Reader)ur)) {
      read(r, null);
    } 
    setEncoding(encoding);
    setDirty(false);
    syncLastSaveOrLoadTimeToActualFile();
    discardAllEdits();
  }







  
  public void removeUpdate(DocumentEvent e) {
    if (!this.dirty) {
      setDirty(true);
    }
  }











  
  public void save() throws IOException {
    saveImpl(this.loc);
    setDirty(false);
    syncLastSaveOrLoadTimeToActualFile();
  }










  
  public void saveAs(FileLocation loc) throws IOException {
    saveImpl(loc);
    
    String old = getFileFullPath();
    this.loc = loc;
    setDirty(false);
    this.lastSaveOrLoadTime = loc.getActualLastModified();
    firePropertyChange("TextEditorPane.fileFullPath", old, getFileFullPath());
  }







  
  private void saveImpl(FileLocation loc) throws IOException {
    OutputStream out = loc.getOutputStream();
    try (BufferedWriter w = new BufferedWriter((Writer)new UnicodeWriter(out, 
            getEncoding()))) {
      write(w);
    } 
  }

















  
  public void setDirty(boolean dirty) {
    if (this.dirty != dirty) {
      this.dirty = dirty;
      firePropertyChange("TextEditorPane.dirty", !dirty, dirty);
    } 
  }







  
  public void setDocument(Document doc) {
    Document old = getDocument();
    if (old != null) {
      old.removeDocumentListener(this);
    }
    super.setDocument(doc);
    doc.addDocumentListener(this);
  }












  
  public void setEncoding(String encoding) {
    if (encoding == null) {
      throw new NullPointerException("encoding cannot be null");
    }
    if (!Charset.isSupported(encoding)) {
      throw new UnsupportedCharsetException(encoding);
    }
    if (this.charSet == null || !this.charSet.equals(encoding)) {
      String oldEncoding = this.charSet;
      this.charSet = encoding;
      firePropertyChange("TextEditorPane.encoding", oldEncoding, this.charSet);
      setDirty(true);
    } 
  }
















  
  public void setLineSeparator(String separator) {
    setLineSeparator(separator, true);
  }


















  
  public void setLineSeparator(String separator, boolean setDirty) {
    if (separator == null) {
      throw new NullPointerException("terminator cannot be null");
    }
    if (!"\r\n".equals(separator) && !"\n".equals(separator) && 
      !"\r".equals(separator)) {
      throw new IllegalArgumentException("Invalid line terminator");
    }
    Document doc = getDocument();
    Object old = doc.getProperty("__EndOfLine__");
    
    if (!separator.equals(old)) {
      doc.putProperty("__EndOfLine__", separator);
      
      if (setDirty) {
        setDirty(true);
      }
    } 
  }








  
  public void setReadOnly(boolean readOnly) {
    if (this.readOnly != readOnly) {
      this.readOnly = readOnly;
      firePropertyChange("TextEditorPane.readOnly", !readOnly, readOnly);
    } 
  }













  
  public void syncLastSaveOrLoadTimeToActualFile() {
    if (this.loc.isLocalAndExists())
      this.lastSaveOrLoadTime = this.loc.getActualLastModified(); 
  }
}
