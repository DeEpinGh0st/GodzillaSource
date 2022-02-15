package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Vector;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.util.Arrays;

public class TlsServerProtocol extends TlsProtocol {
  protected TlsServer tlsServer = null;
  
  TlsServerContextImpl tlsServerContext = null;
  
  protected TlsKeyExchange keyExchange = null;
  
  protected TlsCredentials serverCredentials = null;
  
  protected CertificateRequest certificateRequest = null;
  
  protected short clientCertificateType = -1;
  
  protected TlsHandshakeHash prepareFinishHash = null;
  
  public TlsServerProtocol(InputStream paramInputStream, OutputStream paramOutputStream, SecureRandom paramSecureRandom) {
    super(paramInputStream, paramOutputStream, paramSecureRandom);
  }
  
  public TlsServerProtocol(SecureRandom paramSecureRandom) {
    super(paramSecureRandom);
  }
  
  public void accept(TlsServer paramTlsServer) throws IOException {
    if (paramTlsServer == null)
      throw new IllegalArgumentException("'tlsServer' cannot be null"); 
    if (this.tlsServer != null)
      throw new IllegalStateException("'accept' can only be called once"); 
    this.tlsServer = paramTlsServer;
    this.securityParameters = new SecurityParameters();
    this.securityParameters.entity = 0;
    this.tlsServerContext = new TlsServerContextImpl(this.secureRandom, this.securityParameters);
    this.securityParameters.serverRandom = createRandomBlock(paramTlsServer.shouldUseGMTUnixTime(), this.tlsServerContext.getNonceRandomGenerator());
    this.tlsServer.init(this.tlsServerContext);
    this.recordStream.init(this.tlsServerContext);
    this.recordStream.setRestrictReadVersion(false);
    blockForHandshake();
  }
  
  protected void cleanupHandshake() {
    super.cleanupHandshake();
    this.keyExchange = null;
    this.serverCredentials = null;
    this.certificateRequest = null;
    this.prepareFinishHash = null;
  }
  
  protected TlsContext getContext() {
    return this.tlsServerContext;
  }
  
  AbstractTlsContext getContextAdmin() {
    return this.tlsServerContext;
  }
  
  protected TlsPeer getPeer() {
    return this.tlsServer;
  }
  
  protected void handleHandshakeMessage(short paramShort, ByteArrayInputStream paramByteArrayInputStream) throws IOException {
    Vector vector;
    Certificate certificate;
    byte[] arrayOfByte;
    switch (paramShort) {
      case 1:
        switch (this.connection_state) {
          case 0:
            receiveClientHelloMessage(paramByteArrayInputStream);
            this.connection_state = 1;
            sendServerHelloMessage();
            this.connection_state = 2;
            this.recordStream.notifyHelloComplete();
            vector = this.tlsServer.getServerSupplementalData();
            if (vector != null)
              sendSupplementalDataMessage(vector); 
            this.connection_state = 3;
            this.keyExchange = this.tlsServer.getKeyExchange();
            this.keyExchange.init(getContext());
            this.serverCredentials = this.tlsServer.getCredentials();
            certificate = null;
            if (this.serverCredentials == null) {
              this.keyExchange.skipServerCredentials();
            } else {
              this.keyExchange.processServerCredentials(this.serverCredentials);
              certificate = this.serverCredentials.getCertificate();
              sendCertificateMessage(certificate);
            } 
            this.connection_state = 4;
            if (certificate == null || certificate.isEmpty())
              this.allowCertificateStatus = false; 
            if (this.allowCertificateStatus) {
              CertificateStatus certificateStatus = this.tlsServer.getCertificateStatus();
              if (certificateStatus != null)
                sendCertificateStatusMessage(certificateStatus); 
            } 
            this.connection_state = 5;
            arrayOfByte = this.keyExchange.generateServerKeyExchange();
            if (arrayOfByte != null)
              sendServerKeyExchangeMessage(arrayOfByte); 
            this.connection_state = 6;
            if (this.serverCredentials != null) {
              this.certificateRequest = this.tlsServer.getCertificateRequest();
              if (this.certificateRequest != null) {
                if (TlsUtils.isTLSv12(getContext()) != ((this.certificateRequest.getSupportedSignatureAlgorithms() != null)))
                  throw new TlsFatalAlert((short)80); 
                this.keyExchange.validateCertificateRequest(this.certificateRequest);
                sendCertificateRequestMessage(this.certificateRequest);
                TlsUtils.trackHashAlgorithms(this.recordStream.getHandshakeHash(), this.certificateRequest.getSupportedSignatureAlgorithms());
              } 
            } 
            this.connection_state = 7;
            sendServerHelloDoneMessage();
            this.connection_state = 8;
            this.recordStream.getHandshakeHash().sealHashAlgorithms();
            return;
          case 16:
            refuseRenegotiation();
            return;
        } 
        throw new TlsFatalAlert((short)10);
      case 23:
        switch (this.connection_state) {
          case 8:
            this.tlsServer.processClientSupplementalData(readSupplementalDataMessage(paramByteArrayInputStream));
            this.connection_state = 9;
            return;
        } 
        throw new TlsFatalAlert((short)10);
      case 11:
        switch (this.connection_state) {
          case 8:
            this.tlsServer.processClientSupplementalData((Vector)null);
          case 9:
            if (this.certificateRequest == null)
              throw new TlsFatalAlert((short)10); 
            receiveCertificateMessage(paramByteArrayInputStream);
            this.connection_state = 10;
            return;
        } 
        throw new TlsFatalAlert((short)10);
      case 16:
        switch (this.connection_state) {
          case 8:
            this.tlsServer.processClientSupplementalData((Vector)null);
          case 9:
            if (this.certificateRequest == null) {
              this.keyExchange.skipClientCredentials();
            } else {
              if (TlsUtils.isTLSv12(getContext()))
                throw new TlsFatalAlert((short)10); 
              if (TlsUtils.isSSL(getContext())) {
                if (this.peerCertificate == null)
                  throw new TlsFatalAlert((short)10); 
              } else {
                notifyClientCertificate(Certificate.EMPTY_CHAIN);
              } 
            } 
          case 10:
            receiveClientKeyExchangeMessage(paramByteArrayInputStream);
            this.connection_state = 11;
            return;
        } 
        throw new TlsFatalAlert((short)10);
      case 15:
        switch (this.connection_state) {
          case 11:
            if (!expectCertificateVerifyMessage())
              throw new TlsFatalAlert((short)10); 
            receiveCertificateVerifyMessage(paramByteArrayInputStream);
            this.connection_state = 12;
            return;
        } 
        throw new TlsFatalAlert((short)10);
      case 20:
        switch (this.connection_state) {
          case 11:
            if (expectCertificateVerifyMessage())
              throw new TlsFatalAlert((short)10); 
          case 12:
            processFinishedMessage(paramByteArrayInputStream);
            this.connection_state = 13;
            if (this.expectSessionTicket) {
              sendNewSessionTicketMessage(this.tlsServer.getNewSessionTicket());
              sendChangeCipherSpecMessage();
            } 
            this.connection_state = 14;
            sendFinishedMessage();
            this.connection_state = 15;
            completeHandshake();
            return;
        } 
        throw new TlsFatalAlert((short)10);
    } 
    throw new TlsFatalAlert((short)10);
  }
  
  protected void handleAlertWarningMessage(short paramShort) throws IOException {
    super.handleAlertWarningMessage(paramShort);
    switch (paramShort) {
      case 41:
        if (TlsUtils.isSSL(getContext()) && this.certificateRequest != null)
          switch (this.connection_state) {
            case 8:
              this.tlsServer.processClientSupplementalData((Vector)null);
            case 9:
              notifyClientCertificate(Certificate.EMPTY_CHAIN);
              this.connection_state = 10;
              return;
          }  
        throw new TlsFatalAlert((short)10);
    } 
  }
  
  protected void notifyClientCertificate(Certificate paramCertificate) throws IOException {
    if (this.certificateRequest == null)
      throw new IllegalStateException(); 
    if (this.peerCertificate != null)
      throw new TlsFatalAlert((short)10); 
    this.peerCertificate = paramCertificate;
    if (paramCertificate.isEmpty()) {
      this.keyExchange.skipClientCredentials();
    } else {
      this.clientCertificateType = TlsUtils.getClientCertificateType(paramCertificate, this.serverCredentials.getCertificate());
      this.keyExchange.processClientCertificate(paramCertificate);
    } 
    this.tlsServer.notifyClientCertificate(paramCertificate);
  }
  
  protected void receiveCertificateMessage(ByteArrayInputStream paramByteArrayInputStream) throws IOException {
    Certificate certificate = Certificate.parse(paramByteArrayInputStream);
    assertEmpty(paramByteArrayInputStream);
    notifyClientCertificate(certificate);
  }
  
  protected void receiveCertificateVerifyMessage(ByteArrayInputStream paramByteArrayInputStream) throws IOException {
    if (this.certificateRequest == null)
      throw new IllegalStateException(); 
    DigitallySigned digitallySigned = DigitallySigned.parse(getContext(), paramByteArrayInputStream);
    assertEmpty(paramByteArrayInputStream);
    try {
      byte[] arrayOfByte;
      SignatureAndHashAlgorithm signatureAndHashAlgorithm = digitallySigned.getAlgorithm();
      if (TlsUtils.isTLSv12(getContext())) {
        TlsUtils.verifySupportedSignatureAlgorithm(this.certificateRequest.getSupportedSignatureAlgorithms(), signatureAndHashAlgorithm);
        arrayOfByte = this.prepareFinishHash.getFinalHash(signatureAndHashAlgorithm.getHash());
      } else {
        arrayOfByte = this.securityParameters.getSessionHash();
      } 
      Certificate certificate = this.peerCertificate.getCertificateAt(0);
      SubjectPublicKeyInfo subjectPublicKeyInfo = certificate.getSubjectPublicKeyInfo();
      AsymmetricKeyParameter asymmetricKeyParameter = PublicKeyFactory.createKey(subjectPublicKeyInfo);
      TlsSigner tlsSigner = TlsUtils.createTlsSigner(this.clientCertificateType);
      tlsSigner.init(getContext());
      if (!tlsSigner.verifyRawSignature(signatureAndHashAlgorithm, digitallySigned.getSignature(), asymmetricKeyParameter, arrayOfByte))
        throw new TlsFatalAlert((short)51); 
    } catch (TlsFatalAlert tlsFatalAlert) {
      throw tlsFatalAlert;
    } catch (Exception exception) {
      throw new TlsFatalAlert((short)51, exception);
    } 
  }
  
  protected void receiveClientHelloMessage(ByteArrayInputStream paramByteArrayInputStream) throws IOException {
    ProtocolVersion protocolVersion = TlsUtils.readVersion(paramByteArrayInputStream);
    this.recordStream.setWriteVersion(protocolVersion);
    if (protocolVersion.isDTLS())
      throw new TlsFatalAlert((short)47); 
    byte[] arrayOfByte1 = TlsUtils.readFully(32, paramByteArrayInputStream);
    byte[] arrayOfByte2 = TlsUtils.readOpaque8(paramByteArrayInputStream);
    if (arrayOfByte2.length > 32)
      throw new TlsFatalAlert((short)47); 
    int i = TlsUtils.readUint16(paramByteArrayInputStream);
    if (i < 2 || (i & 0x1) != 0)
      throw new TlsFatalAlert((short)50); 
    this.offeredCipherSuites = TlsUtils.readUint16Array(i / 2, paramByteArrayInputStream);
    short s = TlsUtils.readUint8(paramByteArrayInputStream);
    if (s < 1)
      throw new TlsFatalAlert((short)47); 
    this.offeredCompressionMethods = TlsUtils.readUint8Array(s, paramByteArrayInputStream);
    this.clientExtensions = readExtensions(paramByteArrayInputStream);
    this.securityParameters.extendedMasterSecret = TlsExtensionsUtils.hasExtendedMasterSecretExtension(this.clientExtensions);
    getContextAdmin().setClientVersion(protocolVersion);
    this.tlsServer.notifyClientVersion(protocolVersion);
    this.tlsServer.notifyFallback(Arrays.contains(this.offeredCipherSuites, 22016));
    this.securityParameters.clientRandom = arrayOfByte1;
    this.tlsServer.notifyOfferedCipherSuites(this.offeredCipherSuites);
    this.tlsServer.notifyOfferedCompressionMethods(this.offeredCompressionMethods);
    if (Arrays.contains(this.offeredCipherSuites, 255))
      this.secure_renegotiation = true; 
    byte[] arrayOfByte3 = TlsUtils.getExtensionData(this.clientExtensions, EXT_RenegotiationInfo);
    if (arrayOfByte3 != null) {
      this.secure_renegotiation = true;
      if (!Arrays.constantTimeAreEqual(arrayOfByte3, createRenegotiationInfo(TlsUtils.EMPTY_BYTES)))
        throw new TlsFatalAlert((short)40); 
    } 
    this.tlsServer.notifySecureRenegotiation(this.secure_renegotiation);
    if (this.clientExtensions != null) {
      TlsExtensionsUtils.getPaddingExtension(this.clientExtensions);
      this.tlsServer.processClientExtensions(this.clientExtensions);
    } 
  }
  
  protected void receiveClientKeyExchangeMessage(ByteArrayInputStream paramByteArrayInputStream) throws IOException {
    this.keyExchange.processClientKeyExchange(paramByteArrayInputStream);
    assertEmpty(paramByteArrayInputStream);
    if (TlsUtils.isSSL(getContext()))
      establishMasterSecret(getContext(), this.keyExchange); 
    this.prepareFinishHash = this.recordStream.prepareToFinish();
    this.securityParameters.sessionHash = getCurrentPRFHash(getContext(), this.prepareFinishHash, null);
    if (!TlsUtils.isSSL(getContext()))
      establishMasterSecret(getContext(), this.keyExchange); 
    this.recordStream.setPendingConnectionState(getPeer().getCompression(), getPeer().getCipher());
    if (!this.expectSessionTicket)
      sendChangeCipherSpecMessage(); 
  }
  
  protected void sendCertificateRequestMessage(CertificateRequest paramCertificateRequest) throws IOException {
    TlsProtocol.HandshakeMessage handshakeMessage = new TlsProtocol.HandshakeMessage(this, (short)13);
    paramCertificateRequest.encode(handshakeMessage);
    handshakeMessage.writeToRecordStream();
  }
  
  protected void sendCertificateStatusMessage(CertificateStatus paramCertificateStatus) throws IOException {
    TlsProtocol.HandshakeMessage handshakeMessage = new TlsProtocol.HandshakeMessage(this, (short)22);
    paramCertificateStatus.encode(handshakeMessage);
    handshakeMessage.writeToRecordStream();
  }
  
  protected void sendNewSessionTicketMessage(NewSessionTicket paramNewSessionTicket) throws IOException {
    if (paramNewSessionTicket == null)
      throw new TlsFatalAlert((short)80); 
    TlsProtocol.HandshakeMessage handshakeMessage = new TlsProtocol.HandshakeMessage(this, (short)4);
    paramNewSessionTicket.encode(handshakeMessage);
    handshakeMessage.writeToRecordStream();
  }
  
  protected void sendServerHelloMessage() throws IOException {
    TlsProtocol.HandshakeMessage handshakeMessage = new TlsProtocol.HandshakeMessage(this, (short)2);
    ProtocolVersion protocolVersion = this.tlsServer.getServerVersion();
    if (!protocolVersion.isEqualOrEarlierVersionOf(getContext().getClientVersion()))
      throw new TlsFatalAlert((short)80); 
    this.recordStream.setReadVersion(protocolVersion);
    this.recordStream.setWriteVersion(protocolVersion);
    this.recordStream.setRestrictReadVersion(true);
    getContextAdmin().setServerVersion(protocolVersion);
    TlsUtils.writeVersion(protocolVersion, handshakeMessage);
    handshakeMessage.write(this.securityParameters.serverRandom);
    TlsUtils.writeOpaque8(TlsUtils.EMPTY_BYTES, handshakeMessage);
    int i = this.tlsServer.getSelectedCipherSuite();
    if (!Arrays.contains(this.offeredCipherSuites, i) || i == 0 || CipherSuite.isSCSV(i) || !TlsUtils.isValidCipherSuiteForVersion(i, getContext().getServerVersion()))
      throw new TlsFatalAlert((short)80); 
    this.securityParameters.cipherSuite = i;
    short s = this.tlsServer.getSelectedCompressionMethod();
    if (!Arrays.contains(this.offeredCompressionMethods, s))
      throw new TlsFatalAlert((short)80); 
    this.securityParameters.compressionAlgorithm = s;
    TlsUtils.writeUint16(i, handshakeMessage);
    TlsUtils.writeUint8(s, handshakeMessage);
    this.serverExtensions = this.tlsServer.getServerExtensions();
    if (this.secure_renegotiation) {
      byte[] arrayOfByte = TlsUtils.getExtensionData(this.serverExtensions, EXT_RenegotiationInfo);
      boolean bool = (null == arrayOfByte) ? true : false;
      if (bool) {
        this.serverExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(this.serverExtensions);
        this.serverExtensions.put(EXT_RenegotiationInfo, createRenegotiationInfo(TlsUtils.EMPTY_BYTES));
      } 
    } 
    if (this.securityParameters.extendedMasterSecret) {
      this.serverExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(this.serverExtensions);
      TlsExtensionsUtils.addExtendedMasterSecretExtension(this.serverExtensions);
    } 
    if (this.serverExtensions != null) {
      this.securityParameters.encryptThenMAC = TlsExtensionsUtils.hasEncryptThenMACExtension(this.serverExtensions);
      this.securityParameters.maxFragmentLength = processMaxFragmentLengthExtension(this.clientExtensions, this.serverExtensions, (short)80);
      this.securityParameters.truncatedHMac = TlsExtensionsUtils.hasTruncatedHMacExtension(this.serverExtensions);
      this.allowCertificateStatus = (!this.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(this.serverExtensions, TlsExtensionsUtils.EXT_status_request, (short)80));
      this.expectSessionTicket = (!this.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(this.serverExtensions, TlsProtocol.EXT_SessionTicket, (short)80));
      writeExtensions(handshakeMessage, this.serverExtensions);
    } 
    this.securityParameters.prfAlgorithm = getPRFAlgorithm(getContext(), this.securityParameters.getCipherSuite());
    this.securityParameters.verifyDataLength = 12;
    applyMaxFragmentLengthExtension();
    handshakeMessage.writeToRecordStream();
  }
  
  protected void sendServerHelloDoneMessage() throws IOException {
    byte[] arrayOfByte = new byte[4];
    TlsUtils.writeUint8((short)14, arrayOfByte, 0);
    TlsUtils.writeUint24(0, arrayOfByte, 1);
    writeHandshakeMessage(arrayOfByte, 0, arrayOfByte.length);
  }
  
  protected void sendServerKeyExchangeMessage(byte[] paramArrayOfbyte) throws IOException {
    TlsProtocol.HandshakeMessage handshakeMessage = new TlsProtocol.HandshakeMessage(this, (short)12, paramArrayOfbyte.length);
    handshakeMessage.write(paramArrayOfbyte);
    handshakeMessage.writeToRecordStream();
  }
  
  protected boolean expectCertificateVerifyMessage() {
    return (this.clientCertificateType >= 0 && TlsUtils.hasSigningCapability(this.clientCertificateType));
  }
}
