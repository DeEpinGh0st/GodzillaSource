package org.bouncycastle.crypto.tls;

import java.io.IOException;

public class DTLSTransport implements DatagramTransport {
  private final DTLSRecordLayer recordLayer;
  
  DTLSTransport(DTLSRecordLayer paramDTLSRecordLayer) {
    this.recordLayer = paramDTLSRecordLayer;
  }
  
  public int getReceiveLimit() throws IOException {
    return this.recordLayer.getReceiveLimit();
  }
  
  public int getSendLimit() throws IOException {
    return this.recordLayer.getSendLimit();
  }
  
  public int receive(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3) throws IOException {
    try {
      return this.recordLayer.receive(paramArrayOfbyte, paramInt1, paramInt2, paramInt3);
    } catch (TlsFatalAlert tlsFatalAlert) {
      this.recordLayer.fail(tlsFatalAlert.getAlertDescription());
      throw tlsFatalAlert;
    } catch (IOException iOException) {
      this.recordLayer.fail((short)80);
      throw iOException;
    } catch (RuntimeException runtimeException) {
      this.recordLayer.fail((short)80);
      throw new TlsFatalAlert((short)80, runtimeException);
    } 
  }
  
  public void send(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    try {
      this.recordLayer.send(paramArrayOfbyte, paramInt1, paramInt2);
    } catch (TlsFatalAlert tlsFatalAlert) {
      this.recordLayer.fail(tlsFatalAlert.getAlertDescription());
      throw tlsFatalAlert;
    } catch (IOException iOException) {
      this.recordLayer.fail((short)80);
      throw iOException;
    } catch (RuntimeException runtimeException) {
      this.recordLayer.fail((short)80);
      throw new TlsFatalAlert((short)80, runtimeException);
    } 
  }
  
  public void close() throws IOException {
    this.recordLayer.close();
  }
}
