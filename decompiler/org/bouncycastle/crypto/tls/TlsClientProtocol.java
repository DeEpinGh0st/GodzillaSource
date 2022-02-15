package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.util.Arrays;

public class TlsClientProtocol extends TlsProtocol {
  protected TlsClient tlsClient = null;
  
  TlsClientContextImpl tlsClientContext = null;
  
  protected byte[] selectedSessionID = null;
  
  protected TlsKeyExchange keyExchange = null;
  
  protected TlsAuthentication authentication = null;
  
  protected CertificateStatus certificateStatus = null;
  
  protected CertificateRequest certificateRequest = null;
  
  public TlsClientProtocol(InputStream paramInputStream, OutputStream paramOutputStream, SecureRandom paramSecureRandom) {
    super(paramInputStream, paramOutputStream, paramSecureRandom);
  }
  
  public TlsClientProtocol(SecureRandom paramSecureRandom) {
    super(paramSecureRandom);
  }
  
  public void connect(TlsClient paramTlsClient) throws IOException {
    if (paramTlsClient == null)
      throw new IllegalArgumentException("'tlsClient' cannot be null"); 
    if (this.tlsClient != null)
      throw new IllegalStateException("'connect' can only be called once"); 
    this.tlsClient = paramTlsClient;
    this.securityParameters = new SecurityParameters();
    this.securityParameters.entity = 1;
    this.tlsClientContext = new TlsClientContextImpl(this.secureRandom, this.securityParameters);
    this.securityParameters.clientRandom = createRandomBlock(paramTlsClient.shouldUseGMTUnixTime(), this.tlsClientContext.getNonceRandomGenerator());
    this.tlsClient.init(this.tlsClientContext);
    this.recordStream.init(this.tlsClientContext);
    TlsSession tlsSession = paramTlsClient.getSessionToResume();
    if (tlsSession != null && tlsSession.isResumable()) {
      SessionParameters sessionParameters = tlsSession.exportSessionParameters();
      if (sessionParameters != null) {
        this.tlsSession = tlsSession;
        this.sessionParameters = sessionParameters;
      } 
    } 
    sendClientHelloMessage();
    this.connection_state = 1;
    blockForHandshake();
  }
  
  protected void cleanupHandshake() {
    super.cleanupHandshake();
    this.selectedSessionID = null;
    this.keyExchange = null;
    this.authentication = null;
    this.certificateStatus = null;
    this.certificateRequest = null;
  }
  
  protected TlsContext getContext() {
    return this.tlsClientContext;
  }
  
  AbstractTlsContext getContextAdmin() {
    return this.tlsClientContext;
  }
  
  protected TlsPeer getPeer() {
    return this.tlsClient;
  }
  
  protected void handleHandshakeMessage(short paramShort, ByteArrayInputStream paramByteArrayInputStream) throws IOException {
    Vector vector;
    TlsCredentials tlsCredentials;
    TlsHandshakeHash tlsHandshakeHash;
    if (this.resumedSession) {
      if (paramShort != 20 || this.connection_state != 2)
        throw new TlsFatalAlert((short)10); 
      processFinishedMessage(paramByteArrayInputStream);
      this.connection_state = 15;
      sendFinishedMessage();
      this.connection_state = 13;
      completeHandshake();
      return;
    } 
    switch (paramShort) {
      case 11:
        switch (this.connection_state) {
          case 2:
            handleSupplementalData((Vector)null);
          case 3:
            this.peerCertificate = Certificate.parse(paramByteArrayInputStream);
            assertEmpty(paramByteArrayInputStream);
            if (this.peerCertificate == null || this.peerCertificate.isEmpty())
              this.allowCertificateStatus = false; 
            this.keyExchange.processServerCertificate(this.peerCertificate);
            this.authentication = this.tlsClient.getAuthentication();
            this.authentication.notifyServerCertificate(this.peerCertificate);
            break;
          default:
            throw new TlsFatalAlert((short)10);
        } 
        this.connection_state = 4;
        return;
      case 22:
        switch (this.connection_state) {
          case 4:
            if (!this.allowCertificateStatus)
              throw new TlsFatalAlert((short)10); 
            this.certificateStatus = CertificateStatus.parse(paramByteArrayInputStream);
            assertEmpty(paramByteArrayInputStream);
            this.connection_state = 5;
            return;
        } 
        throw new TlsFatalAlert((short)10);
      case 20:
        switch (this.connection_state) {
          case 13:
            if (this.expectSessionTicket)
              throw new TlsFatalAlert((short)10); 
          case 14:
            processFinishedMessage(paramByteArrayInputStream);
            this.connection_state = 15;
            completeHandshake();
            return;
        } 
        throw new TlsFatalAlert((short)10);
      case 2:
        switch (this.connection_state) {
          case 1:
            receiveServerHelloMessage(paramByteArrayInputStream);
            this.connection_state = 2;
            this.recordStream.notifyHelloComplete();
            applyMaxFragmentLengthExtension();
            if (this.resumedSession) {
              this.securityParameters.masterSecret = Arrays.clone(this.sessionParameters.getMasterSecret());
              this.recordStream.setPendingConnectionState(getPeer().getCompression(), getPeer().getCipher());
              sendChangeCipherSpecMessage();
            } else {
              invalidateSession();
              if (this.selectedSessionID.length > 0)
                this.tlsSession = new TlsSessionImpl(this.selectedSessionID, null); 
            } 
            return;
        } 
        throw new TlsFatalAlert((short)10);
      case 23:
        switch (this.connection_state) {
          case 2:
            handleSupplementalData(readSupplementalDataMessage(paramByteArrayInputStream));
            return;
        } 
        throw new TlsFatalAlert((short)10);
      case 14:
        switch (this.connection_state) {
          case 2:
            handleSupplementalData((Vector)null);
          case 3:
            this.keyExchange.skipServerCredentials();
            this.authentication = null;
          case 4:
          case 5:
            this.keyExchange.skipServerKeyExchange();
          case 6:
          case 7:
            assertEmpty(paramByteArrayInputStream);
            this.connection_state = 8;
            this.recordStream.getHandshakeHash().sealHashAlgorithms();
            vector = this.tlsClient.getClientSupplementalData();
            if (vector != null)
              sendSupplementalDataMessage(vector); 
            this.connection_state = 9;
            tlsCredentials = null;
            if (this.certificateRequest == null) {
              this.keyExchange.skipClientCredentials();
            } else {
              tlsCredentials = this.authentication.getClientCredentials(this.certificateRequest);
              if (tlsCredentials == null) {
                this.keyExchange.skipClientCredentials();
                sendCertificateMessage(Certificate.EMPTY_CHAIN);
              } else {
                this.keyExchange.processClientCredentials(tlsCredentials);
                sendCertificateMessage(tlsCredentials.getCertificate());
              } 
            } 
            this.connection_state = 10;
            sendClientKeyExchangeMessage();
            this.connection_state = 11;
            if (TlsUtils.isSSL(getContext()))
              establishMasterSecret(getContext(), this.keyExchange); 
            tlsHandshakeHash = this.recordStream.prepareToFinish();
            this.securityParameters.sessionHash = getCurrentPRFHash(getContext(), tlsHandshakeHash, null);
            if (!TlsUtils.isSSL(getContext()))
              establishMasterSecret(getContext(), this.keyExchange); 
            this.recordStream.setPendingConnectionState(getPeer().getCompression(), getPeer().getCipher());
            if (tlsCredentials != null && tlsCredentials instanceof TlsSignerCredentials) {
              byte[] arrayOfByte1;
              TlsSignerCredentials tlsSignerCredentials = (TlsSignerCredentials)tlsCredentials;
              SignatureAndHashAlgorithm signatureAndHashAlgorithm = TlsUtils.getSignatureAndHashAlgorithm(getContext(), tlsSignerCredentials);
              if (signatureAndHashAlgorithm == null) {
                arrayOfByte1 = this.securityParameters.getSessionHash();
              } else {
                arrayOfByte1 = tlsHandshakeHash.getFinalHash(signatureAndHashAlgorithm.getHash());
              } 
              byte[] arrayOfByte2 = tlsSignerCredentials.generateCertificateSignature(arrayOfByte1);
              DigitallySigned digitallySigned = new DigitallySigned(signatureAndHashAlgorithm, arrayOfByte2);
              sendCertificateVerifyMessage(digitallySigned);
              this.connection_state = 12;
            } 
            sendChangeCipherSpecMessage();
            sendFinishedMessage();
            break;
          default:
            throw new TlsFatalAlert((short)10);
        } 
        this.connection_state = 13;
        return;
      case 12:
        switch (this.connection_state) {
          case 2:
            handleSupplementalData((Vector)null);
          case 3:
            this.keyExchange.skipServerCredentials();
            this.authentication = null;
          case 4:
          case 5:
            this.keyExchange.processServerKeyExchange(paramByteArrayInputStream);
            assertEmpty(paramByteArrayInputStream);
            break;
          default:
            throw new TlsFatalAlert((short)10);
        } 
        this.connection_state = 6;
        return;
      case 13:
        switch (this.connection_state) {
          case 4:
          case 5:
            this.keyExchange.skipServerKeyExchange();
          case 6:
            if (this.authentication == null)
              throw new TlsFatalAlert((short)40); 
            this.certificateRequest = CertificateRequest.parse(getContext(), paramByteArrayInputStream);
            assertEmpty(paramByteArrayInputStream);
            this.keyExchange.validateCertificateRequest(this.certificateRequest);
            TlsUtils.trackHashAlgorithms(this.recordStream.getHandshakeHash(), this.certificateRequest.getSupportedSignatureAlgorithms());
            break;
          default:
            throw new TlsFatalAlert((short)10);
        } 
        this.connection_state = 7;
        return;
      case 4:
        switch (this.connection_state) {
          case 13:
            if (!this.expectSessionTicket)
              throw new TlsFatalAlert((short)10); 
            invalidateSession();
            receiveNewSessionTicketMessage(paramByteArrayInputStream);
            break;
          default:
            throw new TlsFatalAlert((short)10);
        } 
        this.connection_state = 14;
        return;
      case 0:
        assertEmpty(paramByteArrayInputStream);
        if (this.connection_state == 16)
          refuseRenegotiation(); 
        return;
    } 
    throw new TlsFatalAlert((short)10);
  }
  
  protected void handleSupplementalData(Vector paramVector) throws IOException {
    this.tlsClient.processServerSupplementalData(paramVector);
    this.connection_state = 3;
    this.keyExchange = this.tlsClient.getKeyExchange();
    this.keyExchange.init(getContext());
  }
  
  protected void receiveNewSessionTicketMessage(ByteArrayInputStream paramByteArrayInputStream) throws IOException {
    NewSessionTicket newSessionTicket = NewSessionTicket.parse(paramByteArrayInputStream);
    assertEmpty(paramByteArrayInputStream);
    this.tlsClient.notifyNewSessionTicket(newSessionTicket);
  }
  
  protected void receiveServerHelloMessage(ByteArrayInputStream paramByteArrayInputStream) throws IOException {
    ProtocolVersion protocolVersion1 = TlsUtils.readVersion(paramByteArrayInputStream);
    if (protocolVersion1.isDTLS())
      throw new TlsFatalAlert((short)47); 
    if (!protocolVersion1.equals(this.recordStream.getReadVersion()))
      throw new TlsFatalAlert((short)47); 
    ProtocolVersion protocolVersion2 = getContext().getClientVersion();
    if (!protocolVersion1.isEqualOrEarlierVersionOf(protocolVersion2))
      throw new TlsFatalAlert((short)47); 
    this.recordStream.setWriteVersion(protocolVersion1);
    getContextAdmin().setServerVersion(protocolVersion1);
    this.tlsClient.notifyServerVersion(protocolVersion1);
    this.securityParameters.serverRandom = TlsUtils.readFully(32, paramByteArrayInputStream);
    this.selectedSessionID = TlsUtils.readOpaque8(paramByteArrayInputStream);
    if (this.selectedSessionID.length > 32)
      throw new TlsFatalAlert((short)47); 
    this.tlsClient.notifySessionID(this.selectedSessionID);
    this.resumedSession = (this.selectedSessionID.length > 0 && this.tlsSession != null && Arrays.areEqual(this.selectedSessionID, this.tlsSession.getSessionID()));
    int i = TlsUtils.readUint16(paramByteArrayInputStream);
    if (!Arrays.contains(this.offeredCipherSuites, i) || i == 0 || CipherSuite.isSCSV(i) || !TlsUtils.isValidCipherSuiteForVersion(i, getContext().getServerVersion()))
      throw new TlsFatalAlert((short)47); 
    this.tlsClient.notifySelectedCipherSuite(i);
    short s = TlsUtils.readUint8(paramByteArrayInputStream);
    if (!Arrays.contains(this.offeredCompressionMethods, s))
      throw new TlsFatalAlert((short)47); 
    this.tlsClient.notifySelectedCompressionMethod(s);
    this.serverExtensions = readExtensions(paramByteArrayInputStream);
    if (this.serverExtensions != null) {
      Enumeration<Integer> enumeration = this.serverExtensions.keys();
      while (enumeration.hasMoreElements()) {
        Integer integer = enumeration.nextElement();
        if (integer.equals(EXT_RenegotiationInfo))
          continue; 
        if (null == TlsUtils.getExtensionData(this.clientExtensions, integer))
          throw new TlsFatalAlert((short)110); 
        if (this.resumedSession);
      } 
    } 
    byte[] arrayOfByte = TlsUtils.getExtensionData(this.serverExtensions, EXT_RenegotiationInfo);
    if (arrayOfByte != null) {
      this.secure_renegotiation = true;
      if (!Arrays.constantTimeAreEqual(arrayOfByte, createRenegotiationInfo(TlsUtils.EMPTY_BYTES)))
        throw new TlsFatalAlert((short)40); 
    } 
    this.tlsClient.notifySecureRenegotiation(this.secure_renegotiation);
    Hashtable hashtable1 = this.clientExtensions;
    Hashtable hashtable2 = this.serverExtensions;
    if (this.resumedSession) {
      if (i != this.sessionParameters.getCipherSuite() || s != this.sessionParameters.getCompressionAlgorithm())
        throw new TlsFatalAlert((short)47); 
      hashtable1 = null;
      hashtable2 = this.sessionParameters.readServerExtensions();
    } 
    this.securityParameters.cipherSuite = i;
    this.securityParameters.compressionAlgorithm = s;
    if (hashtable2 != null) {
      boolean bool = TlsExtensionsUtils.hasEncryptThenMACExtension(hashtable2);
      if (bool && !TlsUtils.isBlockCipherSuite(i))
        throw new TlsFatalAlert((short)47); 
      this.securityParameters.encryptThenMAC = bool;
      this.securityParameters.extendedMasterSecret = TlsExtensionsUtils.hasExtendedMasterSecretExtension(hashtable2);
      this.securityParameters.maxFragmentLength = processMaxFragmentLengthExtension(hashtable1, hashtable2, (short)47);
      this.securityParameters.truncatedHMac = TlsExtensionsUtils.hasTruncatedHMacExtension(hashtable2);
      this.allowCertificateStatus = (!this.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(hashtable2, TlsExtensionsUtils.EXT_status_request, (short)47));
      this.expectSessionTicket = (!this.resumedSession && TlsUtils.hasExpectedEmptyExtensionData(hashtable2, TlsProtocol.EXT_SessionTicket, (short)47));
    } 
    if (hashtable1 != null)
      this.tlsClient.processServerExtensions(hashtable2); 
    this.securityParameters.prfAlgorithm = getPRFAlgorithm(getContext(), this.securityParameters.getCipherSuite());
    this.securityParameters.verifyDataLength = 12;
  }
  
  protected void sendCertificateVerifyMessage(DigitallySigned paramDigitallySigned) throws IOException {
    TlsProtocol.HandshakeMessage handshakeMessage = new TlsProtocol.HandshakeMessage(this, (short)15);
    paramDigitallySigned.encode(handshakeMessage);
    handshakeMessage.writeToRecordStream();
  }
  
  protected void sendClientHelloMessage() throws IOException {
    this.recordStream.setWriteVersion(this.tlsClient.getClientHelloRecordLayerVersion());
    ProtocolVersion protocolVersion = this.tlsClient.getClientVersion();
    if (protocolVersion.isDTLS())
      throw new TlsFatalAlert((short)80); 
    getContextAdmin().setClientVersion(protocolVersion);
    byte[] arrayOfByte1 = TlsUtils.EMPTY_BYTES;
    if (this.tlsSession != null) {
      arrayOfByte1 = this.tlsSession.getSessionID();
      if (arrayOfByte1 == null || arrayOfByte1.length > 32)
        arrayOfByte1 = TlsUtils.EMPTY_BYTES; 
    } 
    boolean bool = this.tlsClient.isFallback();
    this.offeredCipherSuites = this.tlsClient.getCipherSuites();
    this.offeredCompressionMethods = this.tlsClient.getCompressionMethods();
    if (arrayOfByte1.length > 0 && this.sessionParameters != null && (!Arrays.contains(this.offeredCipherSuites, this.sessionParameters.getCipherSuite()) || !Arrays.contains(this.offeredCompressionMethods, this.sessionParameters.getCompressionAlgorithm())))
      arrayOfByte1 = TlsUtils.EMPTY_BYTES; 
    this.clientExtensions = this.tlsClient.getClientExtensions();
    TlsProtocol.HandshakeMessage handshakeMessage = new TlsProtocol.HandshakeMessage(this, (short)1);
    TlsUtils.writeVersion(protocolVersion, handshakeMessage);
    handshakeMessage.write(this.securityParameters.getClientRandom());
    TlsUtils.writeOpaque8(arrayOfByte1, handshakeMessage);
    byte[] arrayOfByte2 = TlsUtils.getExtensionData(this.clientExtensions, EXT_RenegotiationInfo);
    boolean bool1 = (null == arrayOfByte2) ? true : false;
    boolean bool2 = !Arrays.contains(this.offeredCipherSuites, 255) ? true : false;
    if (bool1 && bool2)
      this.offeredCipherSuites = Arrays.append(this.offeredCipherSuites, 255); 
    if (bool && !Arrays.contains(this.offeredCipherSuites, 22016))
      this.offeredCipherSuites = Arrays.append(this.offeredCipherSuites, 22016); 
    TlsUtils.writeUint16ArrayWithUint16Length(this.offeredCipherSuites, handshakeMessage);
    TlsUtils.writeUint8ArrayWithUint8Length(this.offeredCompressionMethods, handshakeMessage);
    if (this.clientExtensions != null)
      writeExtensions(handshakeMessage, this.clientExtensions); 
    handshakeMessage.writeToRecordStream();
  }
  
  protected void sendClientKeyExchangeMessage() throws IOException {
    TlsProtocol.HandshakeMessage handshakeMessage = new TlsProtocol.HandshakeMessage(this, (short)16);
    this.keyExchange.generateClientKeyExchange(handshakeMessage);
    handshakeMessage.writeToRecordStream();
  }
}
