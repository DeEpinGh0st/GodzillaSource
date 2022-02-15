package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.util.Arrays;

public class DTLSClientProtocol extends DTLSProtocol {
  public DTLSClientProtocol(SecureRandom paramSecureRandom) {
    super(paramSecureRandom);
  }
  
  public DTLSTransport connect(TlsClient paramTlsClient, DatagramTransport paramDatagramTransport) throws IOException {
    if (paramTlsClient == null)
      throw new IllegalArgumentException("'client' cannot be null"); 
    if (paramDatagramTransport == null)
      throw new IllegalArgumentException("'transport' cannot be null"); 
    SecurityParameters securityParameters = new SecurityParameters();
    securityParameters.entity = 1;
    ClientHandshakeState clientHandshakeState = new ClientHandshakeState();
    clientHandshakeState.client = paramTlsClient;
    clientHandshakeState.clientContext = new TlsClientContextImpl(this.secureRandom, securityParameters);
    securityParameters.clientRandom = TlsProtocol.createRandomBlock(paramTlsClient.shouldUseGMTUnixTime(), clientHandshakeState.clientContext.getNonceRandomGenerator());
    paramTlsClient.init(clientHandshakeState.clientContext);
    DTLSRecordLayer dTLSRecordLayer = new DTLSRecordLayer(paramDatagramTransport, clientHandshakeState.clientContext, paramTlsClient, (short)22);
    TlsSession tlsSession = clientHandshakeState.client.getSessionToResume();
    if (tlsSession != null && tlsSession.isResumable()) {
      SessionParameters sessionParameters = tlsSession.exportSessionParameters();
      if (sessionParameters != null) {
        clientHandshakeState.tlsSession = tlsSession;
        clientHandshakeState.sessionParameters = sessionParameters;
      } 
    } 
    try {
      return clientHandshake(clientHandshakeState, dTLSRecordLayer);
    } catch (TlsFatalAlert tlsFatalAlert) {
      abortClientHandshake(clientHandshakeState, dTLSRecordLayer, tlsFatalAlert.getAlertDescription());
      throw tlsFatalAlert;
    } catch (IOException iOException) {
      abortClientHandshake(clientHandshakeState, dTLSRecordLayer, (short)80);
      throw iOException;
    } catch (RuntimeException runtimeException) {
      abortClientHandshake(clientHandshakeState, dTLSRecordLayer, (short)80);
      throw new TlsFatalAlert((short)80, runtimeException);
    } finally {
      securityParameters.clear();
    } 
  }
  
  protected void abortClientHandshake(ClientHandshakeState paramClientHandshakeState, DTLSRecordLayer paramDTLSRecordLayer, short paramShort) {
    paramDTLSRecordLayer.fail(paramShort);
    invalidateSession(paramClientHandshakeState);
  }
  
  protected DTLSTransport clientHandshake(ClientHandshakeState paramClientHandshakeState, DTLSRecordLayer paramDTLSRecordLayer) throws IOException {
    SecurityParameters securityParameters = paramClientHandshakeState.clientContext.getSecurityParameters();
    DTLSReliableHandshake dTLSReliableHandshake = new DTLSReliableHandshake(paramClientHandshakeState.clientContext, paramDTLSRecordLayer);
    byte[] arrayOfByte1 = generateClientHello(paramClientHandshakeState, paramClientHandshakeState.client);
    paramDTLSRecordLayer.setWriteVersion(ProtocolVersion.DTLSv10);
    dTLSReliableHandshake.sendMessage((short)1, arrayOfByte1);
    DTLSReliableHandshake.Message message;
    for (message = dTLSReliableHandshake.receiveMessage(); message.getType() == 3; message = dTLSReliableHandshake.receiveMessage()) {
      ProtocolVersion protocolVersion1 = paramDTLSRecordLayer.getReadVersion();
      ProtocolVersion protocolVersion2 = paramClientHandshakeState.clientContext.getClientVersion();
      if (!protocolVersion1.isEqualOrEarlierVersionOf(protocolVersion2))
        throw new TlsFatalAlert((short)47); 
      paramDTLSRecordLayer.setReadVersion(null);
      byte[] arrayOfByte5 = processHelloVerifyRequest(paramClientHandshakeState, message.getBody());
      byte[] arrayOfByte6 = patchClientHelloWithCookie(arrayOfByte1, arrayOfByte5);
      dTLSReliableHandshake.resetHandshakeMessagesDigest();
      dTLSReliableHandshake.sendMessage((short)1, arrayOfByte6);
    } 
    if (message.getType() == 2) {
      ProtocolVersion protocolVersion = paramDTLSRecordLayer.getReadVersion();
      reportServerVersion(paramClientHandshakeState, protocolVersion);
      paramDTLSRecordLayer.setWriteVersion(protocolVersion);
      processServerHello(paramClientHandshakeState, message.getBody());
    } else {
      throw new TlsFatalAlert((short)10);
    } 
    dTLSReliableHandshake.notifyHelloComplete();
    applyMaxFragmentLengthExtension(paramDTLSRecordLayer, securityParameters.maxFragmentLength);
    if (paramClientHandshakeState.resumedSession) {
      securityParameters.masterSecret = Arrays.clone(paramClientHandshakeState.sessionParameters.getMasterSecret());
      paramDTLSRecordLayer.initPendingEpoch(paramClientHandshakeState.client.getCipher());
      byte[] arrayOfByte5 = TlsUtils.calculateVerifyData(paramClientHandshakeState.clientContext, "server finished", TlsProtocol.getCurrentPRFHash(paramClientHandshakeState.clientContext, dTLSReliableHandshake.getHandshakeHash(), null));
      processFinished(dTLSReliableHandshake.receiveMessageBody((short)20), arrayOfByte5);
      byte[] arrayOfByte6 = TlsUtils.calculateVerifyData(paramClientHandshakeState.clientContext, "client finished", TlsProtocol.getCurrentPRFHash(paramClientHandshakeState.clientContext, dTLSReliableHandshake.getHandshakeHash(), null));
      dTLSReliableHandshake.sendMessage((short)20, arrayOfByte6);
      dTLSReliableHandshake.finish();
      paramClientHandshakeState.clientContext.setResumableSession(paramClientHandshakeState.tlsSession);
      paramClientHandshakeState.client.notifyHandshakeComplete();
      return new DTLSTransport(paramDTLSRecordLayer);
    } 
    invalidateSession(paramClientHandshakeState);
    if (paramClientHandshakeState.selectedSessionID.length > 0)
      paramClientHandshakeState.tlsSession = new TlsSessionImpl(paramClientHandshakeState.selectedSessionID, null); 
    message = dTLSReliableHandshake.receiveMessage();
    if (message.getType() == 23) {
      processServerSupplementalData(paramClientHandshakeState, message.getBody());
      message = dTLSReliableHandshake.receiveMessage();
    } else {
      paramClientHandshakeState.client.processServerSupplementalData((Vector)null);
    } 
    paramClientHandshakeState.keyExchange = paramClientHandshakeState.client.getKeyExchange();
    paramClientHandshakeState.keyExchange.init(paramClientHandshakeState.clientContext);
    Certificate certificate = null;
    if (message.getType() == 11) {
      certificate = processServerCertificate(paramClientHandshakeState, message.getBody());
      message = dTLSReliableHandshake.receiveMessage();
    } else {
      paramClientHandshakeState.keyExchange.skipServerCredentials();
    } 
    if (certificate == null || certificate.isEmpty())
      paramClientHandshakeState.allowCertificateStatus = false; 
    if (message.getType() == 22) {
      processCertificateStatus(paramClientHandshakeState, message.getBody());
      message = dTLSReliableHandshake.receiveMessage();
    } 
    if (message.getType() == 12) {
      processServerKeyExchange(paramClientHandshakeState, message.getBody());
      message = dTLSReliableHandshake.receiveMessage();
    } else {
      paramClientHandshakeState.keyExchange.skipServerKeyExchange();
    } 
    if (message.getType() == 13) {
      processCertificateRequest(paramClientHandshakeState, message.getBody());
      TlsUtils.trackHashAlgorithms(dTLSReliableHandshake.getHandshakeHash(), paramClientHandshakeState.certificateRequest.getSupportedSignatureAlgorithms());
      message = dTLSReliableHandshake.receiveMessage();
    } 
    if (message.getType() == 14) {
      if ((message.getBody()).length != 0)
        throw new TlsFatalAlert((short)50); 
    } else {
      throw new TlsFatalAlert((short)10);
    } 
    dTLSReliableHandshake.getHandshakeHash().sealHashAlgorithms();
    Vector vector = paramClientHandshakeState.client.getClientSupplementalData();
    if (vector != null) {
      byte[] arrayOfByte = generateSupplementalData(vector);
      dTLSReliableHandshake.sendMessage((short)23, arrayOfByte);
    } 
    if (paramClientHandshakeState.certificateRequest != null) {
      paramClientHandshakeState.clientCredentials = paramClientHandshakeState.authentication.getClientCredentials(paramClientHandshakeState.certificateRequest);
      Certificate certificate1 = null;
      if (paramClientHandshakeState.clientCredentials != null)
        certificate1 = paramClientHandshakeState.clientCredentials.getCertificate(); 
      if (certificate1 == null)
        certificate1 = Certificate.EMPTY_CHAIN; 
      byte[] arrayOfByte = generateCertificate(certificate1);
      dTLSReliableHandshake.sendMessage((short)11, arrayOfByte);
    } 
    if (paramClientHandshakeState.clientCredentials != null) {
      paramClientHandshakeState.keyExchange.processClientCredentials(paramClientHandshakeState.clientCredentials);
    } else {
      paramClientHandshakeState.keyExchange.skipClientCredentials();
    } 
    byte[] arrayOfByte2 = generateClientKeyExchange(paramClientHandshakeState);
    dTLSReliableHandshake.sendMessage((short)16, arrayOfByte2);
    TlsHandshakeHash tlsHandshakeHash = dTLSReliableHandshake.prepareToFinish();
    securityParameters.sessionHash = TlsProtocol.getCurrentPRFHash(paramClientHandshakeState.clientContext, tlsHandshakeHash, null);
    TlsProtocol.establishMasterSecret(paramClientHandshakeState.clientContext, paramClientHandshakeState.keyExchange);
    paramDTLSRecordLayer.initPendingEpoch(paramClientHandshakeState.client.getCipher());
    if (paramClientHandshakeState.clientCredentials != null && paramClientHandshakeState.clientCredentials instanceof TlsSignerCredentials) {
      byte[] arrayOfByte5;
      TlsSignerCredentials tlsSignerCredentials = (TlsSignerCredentials)paramClientHandshakeState.clientCredentials;
      SignatureAndHashAlgorithm signatureAndHashAlgorithm = TlsUtils.getSignatureAndHashAlgorithm(paramClientHandshakeState.clientContext, tlsSignerCredentials);
      if (signatureAndHashAlgorithm == null) {
        arrayOfByte5 = securityParameters.getSessionHash();
      } else {
        arrayOfByte5 = tlsHandshakeHash.getFinalHash(signatureAndHashAlgorithm.getHash());
      } 
      byte[] arrayOfByte6 = tlsSignerCredentials.generateCertificateSignature(arrayOfByte5);
      DigitallySigned digitallySigned = new DigitallySigned(signatureAndHashAlgorithm, arrayOfByte6);
      byte[] arrayOfByte7 = generateCertificateVerify(paramClientHandshakeState, digitallySigned);
      dTLSReliableHandshake.sendMessage((short)15, arrayOfByte7);
    } 
    byte[] arrayOfByte3 = TlsUtils.calculateVerifyData(paramClientHandshakeState.clientContext, "client finished", TlsProtocol.getCurrentPRFHash(paramClientHandshakeState.clientContext, dTLSReliableHandshake.getHandshakeHash(), null));
    dTLSReliableHandshake.sendMessage((short)20, arrayOfByte3);
    if (paramClientHandshakeState.expectSessionTicket) {
      message = dTLSReliableHandshake.receiveMessage();
      if (message.getType() == 4) {
        processNewSessionTicket(paramClientHandshakeState, message.getBody());
      } else {
        throw new TlsFatalAlert((short)10);
      } 
    } 
    byte[] arrayOfByte4 = TlsUtils.calculateVerifyData(paramClientHandshakeState.clientContext, "server finished", TlsProtocol.getCurrentPRFHash(paramClientHandshakeState.clientContext, dTLSReliableHandshake.getHandshakeHash(), null));
    processFinished(dTLSReliableHandshake.receiveMessageBody((short)20), arrayOfByte4);
    dTLSReliableHandshake.finish();
    if (paramClientHandshakeState.tlsSession != null) {
      paramClientHandshakeState.sessionParameters = (new SessionParameters.Builder()).setCipherSuite(securityParameters.getCipherSuite()).setCompressionAlgorithm(securityParameters.getCompressionAlgorithm()).setMasterSecret(securityParameters.getMasterSecret()).setPeerCertificate(certificate).setPSKIdentity(securityParameters.getPSKIdentity()).setSRPIdentity(securityParameters.getSRPIdentity()).setServerExtensions(paramClientHandshakeState.serverExtensions).build();
      paramClientHandshakeState.tlsSession = TlsUtils.importSession(paramClientHandshakeState.tlsSession.getSessionID(), paramClientHandshakeState.sessionParameters);
      paramClientHandshakeState.clientContext.setResumableSession(paramClientHandshakeState.tlsSession);
    } 
    paramClientHandshakeState.client.notifyHandshakeComplete();
    return new DTLSTransport(paramDTLSRecordLayer);
  }
  
  protected byte[] generateCertificateVerify(ClientHandshakeState paramClientHandshakeState, DigitallySigned paramDigitallySigned) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    paramDigitallySigned.encode(byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }
  
  protected byte[] generateClientHello(ClientHandshakeState paramClientHandshakeState, TlsClient paramTlsClient) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ProtocolVersion protocolVersion = paramTlsClient.getClientVersion();
    if (!protocolVersion.isDTLS())
      throw new TlsFatalAlert((short)80); 
    TlsClientContextImpl tlsClientContextImpl = paramClientHandshakeState.clientContext;
    tlsClientContextImpl.setClientVersion(protocolVersion);
    TlsUtils.writeVersion(protocolVersion, byteArrayOutputStream);
    SecurityParameters securityParameters = tlsClientContextImpl.getSecurityParameters();
    byteArrayOutputStream.write(securityParameters.getClientRandom());
    byte[] arrayOfByte1 = TlsUtils.EMPTY_BYTES;
    if (paramClientHandshakeState.tlsSession != null) {
      arrayOfByte1 = paramClientHandshakeState.tlsSession.getSessionID();
      if (arrayOfByte1 == null || arrayOfByte1.length > 32)
        arrayOfByte1 = TlsUtils.EMPTY_BYTES; 
    } 
    TlsUtils.writeOpaque8(arrayOfByte1, byteArrayOutputStream);
    TlsUtils.writeOpaque8(TlsUtils.EMPTY_BYTES, byteArrayOutputStream);
    boolean bool = paramTlsClient.isFallback();
    paramClientHandshakeState.offeredCipherSuites = paramTlsClient.getCipherSuites();
    paramClientHandshakeState.clientExtensions = paramTlsClient.getClientExtensions();
    byte[] arrayOfByte2 = TlsUtils.getExtensionData(paramClientHandshakeState.clientExtensions, TlsProtocol.EXT_RenegotiationInfo);
    boolean bool1 = (null == arrayOfByte2) ? true : false;
    boolean bool2 = !Arrays.contains(paramClientHandshakeState.offeredCipherSuites, 255) ? true : false;
    if (bool1 && bool2)
      paramClientHandshakeState.offeredCipherSuites = Arrays.append(paramClientHandshakeState.offeredCipherSuites, 255); 
    if (bool && !Arrays.contains(paramClientHandshakeState.offeredCipherSuites, 22016))
      paramClientHandshakeState.offeredCipherSuites = Arrays.append(paramClientHandshakeState.offeredCipherSuites, 22016); 
    TlsUtils.writeUint16ArrayWithUint16Length(paramClientHandshakeState.offeredCipherSuites, byteArrayOutputStream);
    paramClientHandshakeState.offeredCompressionMethods = new short[] { 0 };
    TlsUtils.writeUint8ArrayWithUint8Length(paramClientHandshakeState.offeredCompressionMethods, byteArrayOutputStream);
    if (paramClientHandshakeState.clientExtensions != null)
      TlsProtocol.writeExtensions(byteArrayOutputStream, paramClientHandshakeState.clientExtensions); 
    return byteArrayOutputStream.toByteArray();
  }
  
  protected byte[] generateClientKeyExchange(ClientHandshakeState paramClientHandshakeState) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    paramClientHandshakeState.keyExchange.generateClientKeyExchange(byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }
  
  protected void invalidateSession(ClientHandshakeState paramClientHandshakeState) {
    if (paramClientHandshakeState.sessionParameters != null) {
      paramClientHandshakeState.sessionParameters.clear();
      paramClientHandshakeState.sessionParameters = null;
    } 
    if (paramClientHandshakeState.tlsSession != null) {
      paramClientHandshakeState.tlsSession.invalidate();
      paramClientHandshakeState.tlsSession = null;
    } 
  }
  
  protected void processCertificateRequest(ClientHandshakeState paramClientHandshakeState, byte[] paramArrayOfbyte) throws IOException {
    if (paramClientHandshakeState.authentication == null)
      throw new TlsFatalAlert((short)40); 
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
    paramClientHandshakeState.certificateRequest = CertificateRequest.parse(paramClientHandshakeState.clientContext, byteArrayInputStream);
    TlsProtocol.assertEmpty(byteArrayInputStream);
    paramClientHandshakeState.keyExchange.validateCertificateRequest(paramClientHandshakeState.certificateRequest);
  }
  
  protected void processCertificateStatus(ClientHandshakeState paramClientHandshakeState, byte[] paramArrayOfbyte) throws IOException {
    if (!paramClientHandshakeState.allowCertificateStatus)
      throw new TlsFatalAlert((short)10); 
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
    paramClientHandshakeState.certificateStatus = CertificateStatus.parse(byteArrayInputStream);
    TlsProtocol.assertEmpty(byteArrayInputStream);
  }
  
  protected byte[] processHelloVerifyRequest(ClientHandshakeState paramClientHandshakeState, byte[] paramArrayOfbyte) throws IOException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
    ProtocolVersion protocolVersion = TlsUtils.readVersion(byteArrayInputStream);
    byte[] arrayOfByte = TlsUtils.readOpaque8(byteArrayInputStream);
    TlsProtocol.assertEmpty(byteArrayInputStream);
    if (!protocolVersion.isEqualOrEarlierVersionOf(paramClientHandshakeState.clientContext.getClientVersion()))
      throw new TlsFatalAlert((short)47); 
    if (!ProtocolVersion.DTLSv12.isEqualOrEarlierVersionOf(protocolVersion) && arrayOfByte.length > 32)
      throw new TlsFatalAlert((short)47); 
    return arrayOfByte;
  }
  
  protected void processNewSessionTicket(ClientHandshakeState paramClientHandshakeState, byte[] paramArrayOfbyte) throws IOException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
    NewSessionTicket newSessionTicket = NewSessionTicket.parse(byteArrayInputStream);
    TlsProtocol.assertEmpty(byteArrayInputStream);
    paramClientHandshakeState.client.notifyNewSessionTicket(newSessionTicket);
  }
  
  protected Certificate processServerCertificate(ClientHandshakeState paramClientHandshakeState, byte[] paramArrayOfbyte) throws IOException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
    Certificate certificate = Certificate.parse(byteArrayInputStream);
    TlsProtocol.assertEmpty(byteArrayInputStream);
    paramClientHandshakeState.keyExchange.processServerCertificate(certificate);
    paramClientHandshakeState.authentication = paramClientHandshakeState.client.getAuthentication();
    paramClientHandshakeState.authentication.notifyServerCertificate(certificate);
    return certificate;
  }
  
  protected void processServerHello(ClientHandshakeState paramClientHandshakeState, byte[] paramArrayOfbyte) throws IOException {
    SecurityParameters securityParameters = paramClientHandshakeState.clientContext.getSecurityParameters();
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
    ProtocolVersion protocolVersion = TlsUtils.readVersion(byteArrayInputStream);
    reportServerVersion(paramClientHandshakeState, protocolVersion);
    securityParameters.serverRandom = TlsUtils.readFully(32, byteArrayInputStream);
    paramClientHandshakeState.selectedSessionID = TlsUtils.readOpaque8(byteArrayInputStream);
    if (paramClientHandshakeState.selectedSessionID.length > 32)
      throw new TlsFatalAlert((short)47); 
    paramClientHandshakeState.client.notifySessionID(paramClientHandshakeState.selectedSessionID);
    paramClientHandshakeState.resumedSession = (paramClientHandshakeState.selectedSessionID.length > 0 && paramClientHandshakeState.tlsSession != null && Arrays.areEqual(paramClientHandshakeState.selectedSessionID, paramClientHandshakeState.tlsSession.getSessionID()));
    int i = TlsUtils.readUint16(byteArrayInputStream);
    if (!Arrays.contains(paramClientHandshakeState.offeredCipherSuites, i) || i == 0 || CipherSuite.isSCSV(i) || !TlsUtils.isValidCipherSuiteForVersion(i, paramClientHandshakeState.clientContext.getServerVersion()))
      throw new TlsFatalAlert((short)47); 
    validateSelectedCipherSuite(i, (short)47);
    paramClientHandshakeState.client.notifySelectedCipherSuite(i);
    short s = TlsUtils.readUint8(byteArrayInputStream);
    if (!Arrays.contains(paramClientHandshakeState.offeredCompressionMethods, s))
      throw new TlsFatalAlert((short)47); 
    paramClientHandshakeState.client.notifySelectedCompressionMethod(s);
    paramClientHandshakeState.serverExtensions = TlsProtocol.readExtensions(byteArrayInputStream);
    if (paramClientHandshakeState.serverExtensions != null) {
      Enumeration<Integer> enumeration = paramClientHandshakeState.serverExtensions.keys();
      while (enumeration.hasMoreElements()) {
        Integer integer = enumeration.nextElement();
        if (integer.equals(TlsProtocol.EXT_RenegotiationInfo))
          continue; 
        if (null == TlsUtils.getExtensionData(paramClientHandshakeState.clientExtensions, integer))
          throw new TlsFatalAlert((short)110); 
        if (paramClientHandshakeState.resumedSession);
      } 
    } 
    byte[] arrayOfByte = TlsUtils.getExtensionData(paramClientHandshakeState.serverExtensions, TlsProtocol.EXT_RenegotiationInfo);
    if (arrayOfByte != null) {
      paramClientHandshakeState.secure_renegotiation = true;
      if (!Arrays.constantTimeAreEqual(arrayOfByte, TlsProtocol.createRenegotiationInfo(TlsUtils.EMPTY_BYTES)))
        throw new TlsFatalAlert((short)40); 
    } 
    paramClientHandshakeState.client.notifySecureRenegotiation(paramClientHandshakeState.secure_renegotiation);
    Hashtable hashtable1 = paramClientHandshakeState.clientExtensions;
    Hashtable hashtable2 = paramClientHandshakeState.serverExtensions;
    if (paramClientHandshakeState.resumedSession) {
      if (i != paramClientHandshakeState.sessionParameters.getCipherSuite() || s != paramClientHandshakeState.sessionParameters.getCompressionAlgorithm())
        throw new TlsFatalAlert((short)47); 
      hashtable1 = null;
      hashtable2 = paramClientHandshakeState.sessionParameters.readServerExtensions();
    } 
    securityParameters.cipherSuite = i;
    securityParameters.compressionAlgorithm = s;
    if (hashtable2 != null) {
      boolean bool = TlsExtensionsUtils.hasEncryptThenMACExtension(hashtable2);
      if (bool && !TlsUtils.isBlockCipherSuite(securityParameters.getCipherSuite()))
        throw new TlsFatalAlert((short)47); 
      securityParameters.encryptThenMAC = bool;
      securityParameters.extendedMasterSecret = TlsExtensionsUtils.hasExtendedMasterSecretExtension(hashtable2);
      securityParameters.maxFragmentLength = evaluateMaxFragmentLengthExtension(paramClientHandshakeState.resumedSession, hashtable1, hashtable2, (short)47);
      securityParameters.truncatedHMac = TlsExtensionsUtils.hasTruncatedHMacExtension(hashtable2);
      paramClientHandshakeState.allowCertificateStatus = (!paramClientHandshakeState.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(hashtable2, TlsExtensionsUtils.EXT_status_request, (short)47));
      paramClientHandshakeState.expectSessionTicket = (!paramClientHandshakeState.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(hashtable2, TlsProtocol.EXT_SessionTicket, (short)47));
    } 
    if (hashtable1 != null)
      paramClientHandshakeState.client.processServerExtensions(hashtable2); 
    securityParameters.prfAlgorithm = TlsProtocol.getPRFAlgorithm(paramClientHandshakeState.clientContext, securityParameters.getCipherSuite());
    securityParameters.verifyDataLength = 12;
  }
  
  protected void processServerKeyExchange(ClientHandshakeState paramClientHandshakeState, byte[] paramArrayOfbyte) throws IOException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
    paramClientHandshakeState.keyExchange.processServerKeyExchange(byteArrayInputStream);
    TlsProtocol.assertEmpty(byteArrayInputStream);
  }
  
  protected void processServerSupplementalData(ClientHandshakeState paramClientHandshakeState, byte[] paramArrayOfbyte) throws IOException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
    Vector vector = TlsProtocol.readSupplementalDataMessage(byteArrayInputStream);
    paramClientHandshakeState.client.processServerSupplementalData(vector);
  }
  
  protected void reportServerVersion(ClientHandshakeState paramClientHandshakeState, ProtocolVersion paramProtocolVersion) throws IOException {
    TlsClientContextImpl tlsClientContextImpl = paramClientHandshakeState.clientContext;
    ProtocolVersion protocolVersion = tlsClientContextImpl.getServerVersion();
    if (null == protocolVersion) {
      tlsClientContextImpl.setServerVersion(paramProtocolVersion);
      paramClientHandshakeState.client.notifyServerVersion(paramProtocolVersion);
    } else if (!protocolVersion.equals(paramProtocolVersion)) {
      throw new TlsFatalAlert((short)47);
    } 
  }
  
  protected static byte[] patchClientHelloWithCookie(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws IOException {
    byte b = 34;
    short s = TlsUtils.readUint8(paramArrayOfbyte1, b);
    int i = b + 1 + s;
    int j = i + 1;
    byte[] arrayOfByte = new byte[paramArrayOfbyte1.length + paramArrayOfbyte2.length];
    System.arraycopy(paramArrayOfbyte1, 0, arrayOfByte, 0, i);
    TlsUtils.checkUint8(paramArrayOfbyte2.length);
    TlsUtils.writeUint8(paramArrayOfbyte2.length, arrayOfByte, i);
    System.arraycopy(paramArrayOfbyte2, 0, arrayOfByte, j, paramArrayOfbyte2.length);
    System.arraycopy(paramArrayOfbyte1, j, arrayOfByte, j + paramArrayOfbyte2.length, paramArrayOfbyte1.length - j);
    return arrayOfByte;
  }
  
  protected static class ClientHandshakeState {
    TlsClient client = null;
    
    TlsClientContextImpl clientContext = null;
    
    TlsSession tlsSession = null;
    
    SessionParameters sessionParameters = null;
    
    SessionParameters.Builder sessionParametersBuilder = null;
    
    int[] offeredCipherSuites = null;
    
    short[] offeredCompressionMethods = null;
    
    Hashtable clientExtensions = null;
    
    Hashtable serverExtensions = null;
    
    byte[] selectedSessionID = null;
    
    boolean resumedSession = false;
    
    boolean secure_renegotiation = false;
    
    boolean allowCertificateStatus = false;
    
    boolean expectSessionTicket = false;
    
    TlsKeyExchange keyExchange = null;
    
    TlsAuthentication authentication = null;
    
    CertificateStatus certificateStatus = null;
    
    CertificateRequest certificateRequest = null;
    
    TlsCredentials clientCredentials = null;
  }
}
