package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class NewSessionTicket {
  protected long ticketLifetimeHint;
  
  protected byte[] ticket;
  
  public NewSessionTicket(long paramLong, byte[] paramArrayOfbyte) {
    this.ticketLifetimeHint = paramLong;
    this.ticket = paramArrayOfbyte;
  }
  
  public long getTicketLifetimeHint() {
    return this.ticketLifetimeHint;
  }
  
  public byte[] getTicket() {
    return this.ticket;
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    TlsUtils.writeUint32(this.ticketLifetimeHint, paramOutputStream);
    TlsUtils.writeOpaque16(this.ticket, paramOutputStream);
  }
  
  public static NewSessionTicket parse(InputStream paramInputStream) throws IOException {
    long l = TlsUtils.readUint32(paramInputStream);
    byte[] arrayOfByte = TlsUtils.readOpaque16(paramInputStream);
    return new NewSessionTicket(l, arrayOfByte);
  }
}
