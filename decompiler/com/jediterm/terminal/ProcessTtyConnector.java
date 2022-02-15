package com.jediterm.terminal;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



public abstract class ProcessTtyConnector
  implements TtyConnector
{
  protected final InputStream myInputStream;
  protected final OutputStream myOutputStream;
  protected final InputStreamReader myReader;
  protected final Charset myCharset;
  private Dimension myPendingTermSize;
  private final Process myProcess;
  
  public ProcessTtyConnector(@NotNull Process process, @NotNull Charset charset) {
    this.myOutputStream = process.getOutputStream();
    this.myCharset = charset;
    this.myInputStream = process.getInputStream();
    this.myReader = new InputStreamReader(this.myInputStream, charset);
    this.myProcess = process;
  }
  
  @NotNull
  public Process getProcess() {
    if (this.myProcess == null) $$$reportNull$$$0(2);  return this.myProcess;
  }

  
  public void resize(@NotNull Dimension termWinSize) {
    if (termWinSize == null) $$$reportNull$$$0(3);  setPendingTermSize(termWinSize);
    if (isConnected()) {
      resizeImmediately();
      setPendingTermSize(null);
    } 
  }


  
  @Deprecated
  protected void resizeImmediately() {}

  
  public abstract String getName();

  
  public int read(char[] buf, int offset, int length) throws IOException {
    return this.myReader.read(buf, offset, length);
  }
  
  public void write(byte[] bytes) throws IOException {
    this.myOutputStream.write(bytes);
    this.myOutputStream.flush();
  }

  
  public abstract boolean isConnected();

  
  public void write(String string) throws IOException {
    write(string.getBytes(this.myCharset));
  }



  
  @Deprecated
  protected void setPendingTermSize(@Nullable Dimension pendingTermSize) {
    this.myPendingTermSize = pendingTermSize;
  }


  
  @Deprecated
  @Nullable
  protected Dimension getPendingTermSize() {
    return this.myPendingTermSize;
  }



  
  @Deprecated
  protected Dimension getPendingPixelSize() {
    return new Dimension(0, 0);
  }

  
  public boolean init(Questioner q) {
    return isConnected();
  }

  
  public void close() {
    this.myProcess.destroy();
    try {
      this.myOutputStream.close();
    }
    catch (IOException iOException) {}
    try {
      this.myInputStream.close();
    }
    catch (IOException iOException) {}
  }

  
  public int waitFor() throws InterruptedException {
    return this.myProcess.waitFor();
  }
}
