package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPTransport implements DatagramTransport {
  protected static final int MIN_IP_OVERHEAD = 20;
  
  protected static final int MAX_IP_OVERHEAD = 84;
  
  protected static final int UDP_OVERHEAD = 8;
  
  protected final DatagramSocket socket;
  
  protected final int receiveLimit;
  
  protected final int sendLimit;
  
  public UDPTransport(DatagramSocket paramDatagramSocket, int paramInt) throws IOException {
    if (!paramDatagramSocket.isBound() || !paramDatagramSocket.isConnected())
      throw new IllegalArgumentException("'socket' must be bound and connected"); 
    this.socket = paramDatagramSocket;
    this.receiveLimit = paramInt - 20 - 8;
    this.sendLimit = paramInt - 84 - 8;
  }
  
  public int getReceiveLimit() {
    return this.receiveLimit;
  }
  
  public int getSendLimit() {
    return this.sendLimit;
  }
  
  public int receive(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3) throws IOException {
    this.socket.setSoTimeout(paramInt3);
    DatagramPacket datagramPacket = new DatagramPacket(paramArrayOfbyte, paramInt1, paramInt2);
    this.socket.receive(datagramPacket);
    return datagramPacket.getLength();
  }
  
  public void send(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 > getSendLimit())
      throw new TlsFatalAlert((short)80); 
    DatagramPacket datagramPacket = new DatagramPacket(paramArrayOfbyte, paramInt1, paramInt2);
    this.socket.send(datagramPacket);
  }
  
  public void close() throws IOException {
    this.socket.close();
  }
}
