package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

public interface TlsServer extends TlsPeer {
  void init(TlsServerContext paramTlsServerContext);
  
  void notifyClientVersion(ProtocolVersion paramProtocolVersion) throws IOException;
  
  void notifyFallback(boolean paramBoolean) throws IOException;
  
  void notifyOfferedCipherSuites(int[] paramArrayOfint) throws IOException;
  
  void notifyOfferedCompressionMethods(short[] paramArrayOfshort) throws IOException;
  
  void processClientExtensions(Hashtable paramHashtable) throws IOException;
  
  ProtocolVersion getServerVersion() throws IOException;
  
  int getSelectedCipherSuite() throws IOException;
  
  short getSelectedCompressionMethod() throws IOException;
  
  Hashtable getServerExtensions() throws IOException;
  
  Vector getServerSupplementalData() throws IOException;
  
  TlsCredentials getCredentials() throws IOException;
  
  CertificateStatus getCertificateStatus() throws IOException;
  
  TlsKeyExchange getKeyExchange() throws IOException;
  
  CertificateRequest getCertificateRequest() throws IOException;
  
  void processClientSupplementalData(Vector paramVector) throws IOException;
  
  void notifyClientCertificate(Certificate paramCertificate) throws IOException;
  
  NewSessionTicket getNewSessionTicket() throws IOException;
}
