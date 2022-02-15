package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

public interface TlsClient extends TlsPeer {
  void init(TlsClientContext paramTlsClientContext);
  
  TlsSession getSessionToResume();
  
  ProtocolVersion getClientHelloRecordLayerVersion();
  
  ProtocolVersion getClientVersion();
  
  boolean isFallback();
  
  int[] getCipherSuites();
  
  short[] getCompressionMethods();
  
  Hashtable getClientExtensions() throws IOException;
  
  void notifyServerVersion(ProtocolVersion paramProtocolVersion) throws IOException;
  
  void notifySessionID(byte[] paramArrayOfbyte);
  
  void notifySelectedCipherSuite(int paramInt);
  
  void notifySelectedCompressionMethod(short paramShort);
  
  void processServerExtensions(Hashtable paramHashtable) throws IOException;
  
  void processServerSupplementalData(Vector paramVector) throws IOException;
  
  TlsKeyExchange getKeyExchange() throws IOException;
  
  TlsAuthentication getAuthentication() throws IOException;
  
  Vector getClientSupplementalData() throws IOException;
  
  void notifyNewSessionTicket(NewSessionTicket paramNewSessionTicket) throws IOException;
}
