package org.bouncycastle.util.io.pem;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Base64;

public class PemWriter extends BufferedWriter {
  private static final int LINE_LENGTH = 64;
  
  private final int nlLength;
  
  private char[] buf = new char[64];
  
  public PemWriter(Writer paramWriter) {
    super(paramWriter);
    String str = Strings.lineSeparator();
    if (str != null) {
      this.nlLength = str.length();
    } else {
      this.nlLength = 2;
    } 
  }
  
  public int getOutputSize(PemObject paramPemObject) {
    int i = 2 * (paramPemObject.getType().length() + 10 + this.nlLength) + 6 + 4;
    if (!paramPemObject.getHeaders().isEmpty()) {
      for (PemHeader pemHeader : paramPemObject.getHeaders())
        i += pemHeader.getName().length() + ": ".length() + pemHeader.getValue().length() + this.nlLength; 
      i += this.nlLength;
    } 
    int j = ((paramPemObject.getContent()).length + 2) / 3 * 4;
    i += j + (j + 64 - 1) / 64 * this.nlLength;
    return i;
  }
  
  public void writeObject(PemObjectGenerator paramPemObjectGenerator) throws IOException {
    PemObject pemObject = paramPemObjectGenerator.generate();
    writePreEncapsulationBoundary(pemObject.getType());
    if (!pemObject.getHeaders().isEmpty()) {
      for (PemHeader pemHeader : pemObject.getHeaders()) {
        write(pemHeader.getName());
        write(": ");
        write(pemHeader.getValue());
        newLine();
      } 
      newLine();
    } 
    writeEncoded(pemObject.getContent());
    writePostEncapsulationBoundary(pemObject.getType());
  }
  
  private void writeEncoded(byte[] paramArrayOfbyte) throws IOException {
    paramArrayOfbyte = Base64.encode(paramArrayOfbyte);
    for (int i = 0; i < paramArrayOfbyte.length; i += this.buf.length) {
      byte b;
      for (b = 0; b != this.buf.length && i + b < paramArrayOfbyte.length; b++)
        this.buf[b] = (char)paramArrayOfbyte[i + b]; 
      write(this.buf, 0, b);
      newLine();
    } 
  }
  
  private void writePreEncapsulationBoundary(String paramString) throws IOException {
    write("-----BEGIN " + paramString + "-----");
    newLine();
  }
  
  private void writePostEncapsulationBoundary(String paramString) throws IOException {
    write("-----END " + paramString + "-----");
    newLine();
  }
}
