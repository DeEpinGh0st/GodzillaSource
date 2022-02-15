package org.fife.io;

import java.io.Reader;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Segment;






































public class DocumentReader
  extends Reader
{
  private long position;
  private long mark;
  private Document document;
  private Segment segment;
  
  public DocumentReader(Document document) {
    this.position = 0L;
    this.mark = -1L;
    this.document = document;
    this.segment = new Segment();
  }







  
  public void close() {}







  
  public void mark(int readAheadLimit) {
    this.mark = this.position;
  }






  
  public boolean markSupported() {
    return true;
  }





  
  public int read() {
    if (this.position >= this.document.getLength()) {
      return -1;
    }
    try {
      this.document.getText((int)this.position, 1, this.segment);
      this.position++;
      return this.segment.array[this.segment.offset];
    } catch (BadLocationException ble) {
      
      ble.printStackTrace();
      return -1;
    } 
  }









  
  public int read(char[] array) {
    return read(array, 0, array.length);
  }












  
  public int read(char[] cbuf, int off, int len) {
    if (this.position >= this.document.getLength()) {
      return -1;
    }
    int k = len;
    if (this.position + k >= this.document.getLength()) {
      k = this.document.getLength() - (int)this.position;
    }
    if (off + k >= cbuf.length) {
      k = cbuf.length - off;
    }
    try {
      this.document.getText((int)this.position, k, this.segment);
      this.position += k;
      System.arraycopy(this.segment.array, this.segment.offset, cbuf, off, k);

      
      return k;
    } catch (BadLocationException ble) {
      
      return -1;
    } 
  }










  
  public boolean ready() {
    return true;
  }







  
  public void reset() {
    if (this.mark == -1L) {
      this.position = 0L;
    } else {
      
      this.position = this.mark;
      this.mark = -1L;
    } 
  }








  
  public long skip(long n) {
    if (this.position + n <= this.document.getLength()) {
      this.position += n;
      return n;
    } 
    long temp = this.position;
    this.position = this.document.getLength();
    return this.document.getLength() - temp;
  }








  
  public void seek(long pos) {
    this.position = Math.min(pos, this.document.getLength());
  }
}
