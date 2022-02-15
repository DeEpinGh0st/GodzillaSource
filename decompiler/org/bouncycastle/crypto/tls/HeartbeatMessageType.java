package org.bouncycastle.crypto.tls;

public class HeartbeatMessageType {
  public static final short heartbeat_request = 1;
  
  public static final short heartbeat_response = 2;
  
  public static boolean isValid(short paramShort) {
    return (paramShort >= 1 && paramShort <= 2);
  }
}
