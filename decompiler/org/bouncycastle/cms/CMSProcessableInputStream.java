package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.util.io.Streams;

class CMSProcessableInputStream implements CMSProcessable, CMSReadable {
  private InputStream input;
  
  private boolean used = false;
  
  public CMSProcessableInputStream(InputStream paramInputStream) {
    this.input = paramInputStream;
  }
  
  public InputStream getInputStream() {
    checkSingleUsage();
    return this.input;
  }
  
  public void write(OutputStream paramOutputStream) throws IOException, CMSException {
    checkSingleUsage();
    Streams.pipeAll(this.input, paramOutputStream);
    this.input.close();
  }
  
  public Object getContent() {
    return getInputStream();
  }
  
  private synchronized void checkSingleUsage() {
    if (this.used)
      throw new IllegalStateException("CMSProcessableInputStream can only be used once"); 
    this.used = true;
  }
}
