package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.util.Arrays;

public class DTLSServerProtocol extends DTLSProtocol {
  protected boolean verifyRequests = true;
  
  public DTLSServerProtocol(SecureRandom paramSecureRandom) {
    super(paramSecureRandom);
  }
  
  public boolean getVerifyRequests() {
    return this.verifyRequests;
  }
  
  public void setVerifyRequests(boolean paramBoolean) {
    this.verifyRequests = paramBoolean;
  }
  
  public DTLSTransport accept(TlsServer paramTlsServer, DatagramTransport paramDatagramTransport) throws IOException {
    if (paramTlsServer == null)
      throw new IllegalArgumentException("'server' cannot be null"); 
    if (paramDatagramTransport == null)
      throw new IllegalArgumentException("'transport' cannot be null"); 
    SecurityParameters securityParameters = new SecurityParameters();
    securityParameters.entity = 0;
    ServerHandshakeState serverHandshakeState = new ServerHandshakeState();
    serverHandshakeState.server = paramTlsServer;
    serverHandshakeState.serverContext = new TlsServerContextImpl(this.secureRandom, securityParameters);
    securityParameters.serverRandom = TlsProtocol.createRandomBlock(paramTlsServer.shouldUseGMTUnixTime(), serverHandshakeState.serverContext.getNonceRandomGenerator());
    paramTlsServer.init(serverHandshakeState.serverContext);
    DTLSRecordLayer dTLSRecordLayer = new DTLSRecordLayer(paramDatagramTransport, serverHandshakeState.serverContext, paramTlsServer, (short)22);
    try {
      return serverHandshake(serverHandshakeState, dTLSRecordLayer);
    } catch (TlsFatalAlert tlsFatalAlert) {
      abortServerHandshake(serverHandshakeState, dTLSRecordLayer, tlsFatalAlert.getAlertDescription());
      throw tlsFatalAlert;
    } catch (IOException iOException) {
      abortServerHandshake(serverHandshakeState, dTLSRecordLayer, (short)80);
      throw iOException;
    } catch (RuntimeException runtimeException) {
      abortServerHandshake(serverHandshakeState, dTLSRecordLayer, (short)80);
      throw new TlsFatalAlert((short)80, runtimeException);
    } finally {
      securityParameters.clear();
    } 
  }
  
  protected void abortServerHandshake(ServerHandshakeState paramServerHandshakeState, DTLSRecordLayer paramDTLSRecordLayer, short paramShort) {
    paramDTLSRecordLayer.fail(paramShort);
    invalidateSession(paramServerHandshakeState);
  }
  
  protected DTLSTransport serverHandshake(ServerHandshakeState paramServerHandshakeState, DTLSRecordLayer paramDTLSRecordLayer) throws IOException {
    Certificate certificate;
    SecurityParameters securityParameters = paramServerHandshakeState.serverContext.getSecurityParameters();
    DTLSReliableHandshake dTLSReliableHandshake = new DTLSReliableHandshake(paramServerHandshakeState.serverContext, paramDTLSRecordLayer);
    DTLSReliableHandshake.Message message = dTLSReliableHandshake.receiveMessage();
    if (message.getType() == 1) {
      processClientHello(paramServerHandshakeState, message.getBody());
    } else {
      throw new TlsFatalAlert((short)10);
    } 
    byte[] arrayOfByte1 = generateServerHello(paramServerHandshakeState);
    applyMaxFragmentLengthExtension(paramDTLSRecordLayer, securityParameters.maxFragmentLength);
    ProtocolVersion protocolVersion = paramServerHandshakeState.serverContext.getServerVersion();
    paramDTLSRecordLayer.setReadVersion(protocolVersion);
    paramDTLSRecordLayer.setWriteVersion(protocolVersion);
    dTLSReliableHandshake.sendMessage((short)2, arrayOfByte1);
    dTLSReliableHandshake.notifyHelloComplete();
    Vector vector = paramServerHandshakeState.server.getServerSupplementalData();
    if (vector != null) {
      byte[] arrayOfByte = generateSupplementalData(vector);
      dTLSReliableHandshake.sendMessage((short)23, arrayOfByte);
    } 
    paramServerHandshakeState.keyExchange = paramServerHandshakeState.server.getKeyExchange();
    paramServerHandshakeState.keyExchange.init(paramServerHandshakeState.serverContext);
    paramServerHandshakeState.serverCredentials = paramServerHandshakeState.server.getCredentials();
    protocolVersion = null;
    if (paramServerHandshakeState.serverCredentials == null) {
      paramServerHandshakeState.keyExchange.skipServerCredentials();
    } else {
      paramServerHandshakeState.keyExchange.processServerCredentials(paramServerHandshakeState.serverCredentials);
      certificate = paramServerHandshakeState.serverCredentials.getCertificate();
      byte[] arrayOfByte = generateCertificate(certificate);
      dTLSReliableHandshake.sendMessage((short)11, arrayOfByte);
    } 
    if (certificate == null || certificate.isEmpty())
      paramServerHandshakeState.allowCertificateStatus = false; 
    if (paramServerHandshakeState.allowCertificateStatus) {
      CertificateStatus certificateStatus = paramServerHandshakeState.server.getCertificateStatus();
      if (certificateStatus != null) {
        byte[] arrayOfByte = generateCertificateStatus(paramServerHandshakeState, certificateStatus);
        dTLSReliableHandshake.sendMessage((short)22, arrayOfByte);
      } 
    } 
    byte[] arrayOfByte2 = paramServerHandshakeState.keyExchange.generateServerKeyExchange();
    if (arrayOfByte2 != null)
      dTLSReliableHandshake.sendMessage((short)12, arrayOfByte2); 
    if (paramServerHandshakeState.serverCredentials != null) {
      paramServerHandshakeState.certificateRequest = paramServerHandshakeState.server.getCertificateRequest();
      if (paramServerHandshakeState.certificateRequest != null) {
        if (TlsUtils.isTLSv12(paramServerHandshakeState.serverContext) != ((paramServerHandshakeState.certificateRequest.getSupportedSignatureAlgorithms() != null)))
          throw new TlsFatalAlert((short)80); 
        paramServerHandshakeState.keyExchange.validateCertificateRequest(paramServerHandshakeState.certificateRequest);
        byte[] arrayOfByte = generateCertificateRequest(paramServerHandshakeState, paramServerHandshakeState.certificateRequest);
        dTLSReliableHandshake.sendMessage((short)13, arrayOfByte);
        TlsUtils.trackHashAlgorithms(dTLSReliableHandshake.getHandshakeHash(), paramServerHandshakeState.certificateRequest.getSupportedSignatureAlgorithms());
      } 
    } 
    dTLSReliableHandshake.sendMessage((short)14, TlsUtils.EMPTY_BYTES);
    dTLSReliableHandshake.getHandshakeHash().sealHashAlgorithms();
    message = dTLSReliableHandshake.receiveMessage();
    if (message.getType() == 23) {
      processClientSupplementalData(paramServerHandshakeState, message.getBody());
      message = dTLSReliableHandshake.receiveMessage();
    } else {
      paramServerHandshakeState.server.processClientSupplementalData((Vector)null);
    } 
    if (paramServerHandshakeState.certificateRequest == null) {
      paramServerHandshakeState.keyExchange.skipClientCredentials();
    } else if (message.getType() == 11) {
      processClientCertificate(paramServerHandshakeState, message.getBody());
      message = dTLSReliableHandshake.receiveMessage();
    } else {
      if (TlsUtils.isTLSv12(paramServerHandshakeState.serverContext))
        throw new TlsFatalAlert((short)10); 
      notifyClientCertificate(paramServerHandshakeState, Certificate.EMPTY_CHAIN);
    } 
    if (message.getType() == 16) {
      processClientKeyExchange(paramServerHandshakeState, message.getBody());
    } else {
      throw new TlsFatalAlert((short)10);
    } 
    TlsHandshakeHash tlsHandshakeHash = dTLSReliableHandshake.prepareToFinish();
    securityParameters.sessionHash = TlsProtocol.getCurrentPRFHash(paramServerHandshakeState.serverContext, tlsHandshakeHash, null);
    TlsProtocol.establishMasterSecret(paramServerHandshakeState.serverContext, paramServerHandshakeState.keyExchange);
    paramDTLSRecordLayer.initPendingEpoch(paramServerHandshakeState.server.getCipher());
    if (expectCertificateVerifyMessage(paramServerHandshakeState)) {
      byte[] arrayOfByte = dTLSReliableHandshake.receiveMessageBody((short)15);
      processCertificateVerify(paramServerHandshakeState, arrayOfByte, tlsHandshakeHash);
    } 
    byte[] arrayOfByte3 = TlsUtils.calculateVerifyData(paramServerHandshakeState.serverContext, "client finished", TlsProtocol.getCurrentPRFHash(paramServerHandshakeState.serverContext, dTLSReliableHandshake.getHandshakeHash(), null));
    processFinished(dTLSReliableHandshake.receiveMessageBody((short)20), arrayOfByte3);
    if (paramServerHandshakeState.expectSessionTicket) {
      NewSessionTicket newSessionTicket = paramServerHandshakeState.server.getNewSessionTicket();
      byte[] arrayOfByte = generateNewSessionTicket(paramServerHandshakeState, newSessionTicket);
      dTLSReliableHandshake.sendMessage((short)4, arrayOfByte);
    } 
    byte[] arrayOfByte4 = TlsUtils.calculateVerifyData(paramServerHandshakeState.serverContext, "server finished", TlsProtocol.getCurrentPRFHash(paramServerHandshakeState.serverContext, dTLSReliableHandshake.getHandshakeHash(), null));
    dTLSReliableHandshake.sendMessage((short)20, arrayOfByte4);
    dTLSReliableHandshake.finish();
    paramServerHandshakeState.server.notifyHandshakeComplete();
    return new DTLSTransport(paramDTLSRecordLayer);
  }
  
  protected byte[] generateCertificateRequest(ServerHandshakeState paramServerHandshakeState, CertificateRequest paramCertificateRequest) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    paramCertificateRequest.encode(byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }
  
  protected byte[] generateCertificateStatus(ServerHandshakeState paramServerHandshakeState, CertificateStatus paramCertificateStatus) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    paramCertificateStatus.encode(byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }
  
  protected byte[] generateNewSessionTicket(ServerHandshakeState paramServerHandshakeState, NewSessionTicket paramNewSessionTicket) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    paramNewSessionTicket.encode(byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }
  
  protected byte[] generateServerHello(ServerHandshakeState paramServerHandshakeState) throws IOException {
    SecurityParameters securityParameters = paramServerHandshakeState.serverContext.getSecurityParameters();
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ProtocolVersion protocolVersion = paramServerHandshakeState.server.getServerVersion();
    if (!protocolVersion.isEqualOrEarlierVersionOf(paramServerHandshakeState.serverContext.getClientVersion()))
      throw new TlsFatalAlert((short)80); 
    paramServerHandshakeState.serverContext.setServerVersion(protocolVersion);
    TlsUtils.writeVersion(paramServerHandshakeState.serverContext.getServerVersion(), byteArrayOutputStream);
    byteArrayOutputStream.write(securityParameters.getServerRandom());
    TlsUtils.writeOpaque8(TlsUtils.EMPTY_BYTES, byteArrayOutputStream);
    int i = paramServerHandshakeState.server.getSelectedCipherSuite();
    if (!Arrays.contains(paramServerHandshakeState.offeredCipherSuites, i) || i == 0 || CipherSuite.isSCSV(i) || !TlsUtils.isValidCipherSuiteForVersion(i, paramServerHandshakeState.serverContext.getServerVersion()))
      throw new TlsFatalAlert((short)80); 
    validateSelectedCipherSuite(i, (short)80);
    securityParameters.cipherSuite = i;
    short s = paramServerHandshakeState.server.getSelectedCompressionMethod();
    if (!Arrays.contains(paramServerHandshakeState.offeredCompressionMethods, s))
      throw new TlsFatalAlert((short)80); 
    securityParameters.compressionAlgorithm = s;
    TlsUtils.writeUint16(i, byteArrayOutputStream);
    TlsUtils.writeUint8(s, byteArrayOutputStream);
    paramServerHandshakeState.serverExtensions = paramServerHandshakeState.server.getServerExtensions();
    if (paramServerHandshakeState.secure_renegotiation) {
      byte[] arrayOfByte = TlsUtils.getExtensionData(paramServerHandshakeState.serverExtensions, TlsProtocol.EXT_RenegotiationInfo);
      boolean bool = (null == arrayOfByte) ? true : false;
      if (bool) {
        paramServerHandshakeState.serverExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(paramServerHandshakeState.serverExtensions);
        paramServerHandshakeState.serverExtensions.put(TlsProtocol.EXT_RenegotiationInfo, TlsProtocol.createRenegotiationInfo(TlsUtils.EMPTY_BYTES));
      } 
    } 
    if (securityParameters.extendedMasterSecret) {
      paramServerHandshakeState.serverExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(paramServerHandshakeState.serverExtensions);
      TlsExtensionsUtils.addExtendedMasterSecretExtension(paramServerHandshakeState.serverExtensions);
    } 
    if (paramServerHandshakeState.serverExtensions != null) {
      securityParameters.encryptThenMAC = TlsExtensionsUtils.hasEncryptThenMACExtension(paramServerHandshakeState.serverExtensions);
      securityParameters.maxFragmentLength = evaluateMaxFragmentLengthExtension(paramServerHandshakeState.resumedSession, paramServerHandshakeState.clientExtensions, paramServerHandshakeState.serverExtensions, (short)80);
      securityParameters.truncatedHMac = TlsExtensionsUtils.hasTruncatedHMacExtension(paramServerHandshakeState.serverExtensions);
      paramServerHandshakeState.allowCertificateStatus = (!paramServerHandshakeState.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(paramServerHandshakeState.serverExtensions, TlsExtensionsUtils.EXT_status_request, (short)80));
      paramServerHandshakeState.expectSessionTicket = (!paramServerHandshakeState.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(paramServerHandshakeState.serverExtensions, TlsProtocol.EXT_SessionTicket, (short)80));
      TlsProtocol.writeExtensions(byteArrayOutputStream, paramServerHandshakeState.serverExtensions);
    } 
    securityParameters.prfAlgorithm = TlsProtocol.getPRFAlgorithm(paramServerHandshakeState.serverContext, securityParameters.getCipherSuite());
    securityParameters.verifyDataLength = 12;
    return byteArrayOutputStream.toByteArray();
  }
  
  protected void invalidateSession(ServerHandshakeState paramServerHandshakeState) {
    if (paramServerHandshakeState.sessionParameters != null) {
      paramServerHandshakeState.sessionParameters.clear();
      paramServerHandshakeState.sessionParameters = null;
    } 
    if (paramServerHandshakeState.tlsSession != null) {
      paramServerHandshakeState.tlsSession.invalidate();
      paramServerHandshakeState.tlsSession = null;
    } 
  }
  
  protected void notifyClientCertificate(ServerHandshakeState paramServerHandshakeState, Certificate paramCertificate) throws IOException {
    if (paramServerHandshakeState.certificateRequest == null)
      throw new IllegalStateException(); 
    if (paramServerHandshakeState.clientCertificate != null)
      throw new TlsFatalAlert((short)10); 
    paramServerHandshakeState.clientCertificate = paramCertificate;
    if (paramCertificate.isEmpty()) {
      paramServerHandshakeState.keyExchange.skipClientCredentials();
    } else {
      paramServerHandshakeState.clientCertificateType = TlsUtils.getClientCertificateType(paramCertificate, paramServerHandshakeState.serverCredentials.getCertificate());
      paramServerHandshakeState.keyExchange.processClientCertificate(paramCertificate);
    } 
    paramServerHandshakeState.server.notifyClientCertificate(paramCertificate);
  }
  
  protected void processClientCertificate(ServerHandshakeState paramServerHandshakeState, byte[] paramArrayOfbyte) throws IOException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
    Certificate certificate = Certificate.parse(byteArrayInputStream);
    TlsProtocol.assertEmpty(byteArrayInputStream);
    notifyClientCertificate(paramServerHandshakeState, certificate);
  }
  
  protected void processCertificateVerify(ServerHandshakeState paramServerHandshakeState, byte[] paramArrayOfbyte, TlsHandshakeHash paramTlsHandshakeHash) throws IOException {
    if (paramServerHandshakeState.certificateRequest == null)
      throw new IllegalStateException(); 
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
    TlsServerContextImpl tlsServerContextImpl = paramServerHandshakeState.serverContext;
    DigitallySigned digitallySigned = DigitallySigned.parse(tlsServerContextImpl, byteArrayInputStream);
    TlsProtocol.assertEmpty(byteArrayInputStream);
    try {
      byte[] arrayOfByte;
      SignatureAndHashAlgorithm signatureAndHashAlgorithm = digitallySigned.getAlgorithm();
      if (TlsUtils.isTLSv12(tlsServerContextImpl)) {
        TlsUtils.verifySupportedSignatureAlgorithm(paramServerHandshakeState.certificateRequest.getSupportedSignatureAlgorithms(), signatureAndHashAlgorithm);
        arrayOfByte = paramTlsHandshakeHash.getFinalHash(signatureAndHashAlgorithm.getHash());
      } else {
        arrayOfByte = tlsServerContextImpl.getSecurityParameters().getSessionHash();
      } 
      Certificate certificate = paramServerHandshakeState.clientCertificate.getCertificateAt(0);
      SubjectPublicKeyInfo subjectPublicKeyInfo = certificate.getSubjectPublicKeyInfo();
      AsymmetricKeyParameter asymmetricKeyParameter = PublicKeyFactory.createKey(subjectPublicKeyInfo);
      TlsSigner tlsSigner = TlsUtils.createTlsSigner(paramServerHandshakeState.clientCertificateType);
      tlsSigner.init(tlsServerContextImpl);
      if (!tlsSigner.verifyRawSignature(signatureAndHashAlgorithm, digitallySigned.getSignature(), asymmetricKeyParameter, arrayOfByte))
        throw new TlsFatalAlert((short)51); 
    } catch (TlsFatalAlert tlsFatalAlert) {
      throw tlsFatalAlert;
    } catch (Exception exception) {
      throw new TlsFatalAlert((short)51, exception);
    } 
  }
  
  protected void processClientHello(ServerHandshakeState paramServerHandshakeState, byte[] paramArrayOfbyte) throws IOException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
    ProtocolVersion protocolVersion = TlsUtils.readVersion(byteArrayInputStream);
    if (!protocolVersion.isDTLS())
      throw new TlsFatalAlert((short)47); 
    byte[] arrayOfByte1 = TlsUtils.readFully(32, byteArrayInputStream);
    byte[] arrayOfByte2 = TlsUtils.readOpaque8(byteArrayInputStream);
    if (arrayOfByte2.length > 32)
      throw new TlsFatalAlert((short)47); 
    byte[] arrayOfByte3 = TlsUtils.readOpaque8(byteArrayInputStream);
    int i = TlsUtils.readUint16(byteArrayInputStream);
    if (i < 2 || (i & 0x1) != 0)
      throw new TlsFatalAlert((short)50); 
    paramServerHandshakeState.offeredCipherSuites = TlsUtils.readUint16Array(i / 2, byteArrayInputStream);
    short s = TlsUtils.readUint8(byteArrayInputStream);
    if (s < 1)
      throw new TlsFatalAlert((short)47); 
    paramServerHandshakeState.offeredCompressionMethods = TlsUtils.readUint8Array(s, byteArrayInputStream);
    paramServerHandshakeState.clientExtensions = TlsProtocol.readExtensions(byteArrayInputStream);
    TlsServerContextImpl tlsServerContextImpl = paramServerHandshakeState.serverContext;
    SecurityParameters securityParameters = tlsServerContextImpl.getSecurityParameters();
    securityParameters.extendedMasterSecret = TlsExtensionsUtils.hasExtendedMasterSecretExtension(paramServerHandshakeState.clientExtensions);
    tlsServerContextImpl.setClientVersion(protocolVersion);
    paramServerHandshakeState.server.notifyClientVersion(protocolVersion);
    paramServerHandshakeState.server.notifyFallback(Arrays.contains(paramServerHandshakeState.offeredCipherSuites, 22016));
    securityParameters.clientRandom = arrayOfByte1;
    paramServerHandshakeState.server.notifyOfferedCipherSuites(paramServerHandshakeState.offeredCipherSuites);
    paramServerHandshakeState.server.notifyOfferedCompressionMethods(paramServerHandshakeState.offeredCompressionMethods);
    if (Arrays.contains(paramServerHandshakeState.offeredCipherSuites, 255))
      paramServerHandshakeState.secure_renegotiation = true; 
    byte[] arrayOfByte4 = TlsUtils.getExtensionData(paramServerHandshakeState.clientExtensions, TlsProtocol.EXT_RenegotiationInfo);
    if (arrayOfByte4 != null) {
      paramServerHandshakeState.secure_renegotiation = true;
      if (!Arrays.constantTimeAreEqual(arrayOfByte4, TlsProtocol.createRenegotiationInfo(TlsUtils.EMPTY_BYTES)))
        throw new TlsFatalAlert((short)40); 
    } 
    paramServerHandshakeState.server.notifySecureRenegotiation(paramServerHandshakeState.secure_renegotiation);
    if (paramServerHandshakeState.clientExtensions != null) {
      TlsExtensionsUtils.getPaddingExtension(paramServerHandshakeState.clientExtensions);
      paramServerHandshakeState.server.processClientExtensions(paramServerHandshakeState.clientExtensions);
    } 
  }
  
  protected void processClientKeyExchange(ServerHandshakeState paramServerHandshakeState, byte[] paramArrayOfbyte) throws IOException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
    paramServerHandshakeState.keyExchange.processClientKeyExchange(byteArrayInputStream);
    TlsProtocol.assertEmpty(byteArrayInputStream);
  }
  
  protected void processClientSupplementalData(ServerHandshakeState paramServerHandshakeState, byte[] paramArrayOfbyte) throws IOException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
    Vector vector = TlsProtocol.readSupplementalDataMessage(byteArrayInputStream);
    paramServerHandshakeState.server.processClientSupplementalData(vector);
  }
  
  protected boolean expectCertificateVerifyMessage(ServerHandshakeState paramServerHandshakeState) {
    return (paramServerHandshakeState.clientCertificateType >= 0 && TlsUtils.hasSigningCapability(paramServerHandshakeState.clientCertificateType));
  }
  
  protected static class ServerHandshakeState {
    TlsServer server = null;
    
    TlsServerContextImpl serverContext = null;
    
    TlsSession tlsSession = null;
    
    SessionParameters sessionParameters = null;
    
    SessionParameters.Builder sessionParametersBuilder = null;
    
    int[] offeredCipherSuites = null;
    
    short[] offeredCompressionMethods = null;
    
    Hashtable clientExtensions = null;
    
    Hashtable serverExtensions = null;
    
    boolean resumedSession = false;
    
    boolean secure_renegotiation = false;
    
    boolean allowCertificateStatus = false;
    
    boolean expectSessionTicket = false;
    
    TlsKeyExchange keyExchange = null;
    
    TlsCredentials serverCredentials = null;
    
    CertificateRequest certificateRequest = null;
    
    short clientCertificateType = -1;
    
    Certificate clientCertificate = null;
  }
}
