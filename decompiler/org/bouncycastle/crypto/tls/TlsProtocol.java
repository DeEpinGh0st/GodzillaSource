package org.bouncycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.prng.RandomGenerator;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;

public abstract class TlsProtocol {
  protected static final Integer EXT_RenegotiationInfo = Integers.valueOf(65281);
  
  protected static final Integer EXT_SessionTicket = Integers.valueOf(35);
  
  protected static final short CS_START = 0;
  
  protected static final short CS_CLIENT_HELLO = 1;
  
  protected static final short CS_SERVER_HELLO = 2;
  
  protected static final short CS_SERVER_SUPPLEMENTAL_DATA = 3;
  
  protected static final short CS_SERVER_CERTIFICATE = 4;
  
  protected static final short CS_CERTIFICATE_STATUS = 5;
  
  protected static final short CS_SERVER_KEY_EXCHANGE = 6;
  
  protected static final short CS_CERTIFICATE_REQUEST = 7;
  
  protected static final short CS_SERVER_HELLO_DONE = 8;
  
  protected static final short CS_CLIENT_SUPPLEMENTAL_DATA = 9;
  
  protected static final short CS_CLIENT_CERTIFICATE = 10;
  
  protected static final short CS_CLIENT_KEY_EXCHANGE = 11;
  
  protected static final short CS_CERTIFICATE_VERIFY = 12;
  
  protected static final short CS_CLIENT_FINISHED = 13;
  
  protected static final short CS_SERVER_SESSION_TICKET = 14;
  
  protected static final short CS_SERVER_FINISHED = 15;
  
  protected static final short CS_END = 16;
  
  protected static final short ADS_MODE_1_Nsub1 = 0;
  
  protected static final short ADS_MODE_0_N = 1;
  
  protected static final short ADS_MODE_0_N_FIRSTONLY = 2;
  
  private ByteQueue applicationDataQueue = new ByteQueue(0);
  
  private ByteQueue alertQueue = new ByteQueue(2);
  
  private ByteQueue handshakeQueue = new ByteQueue(0);
  
  RecordStream recordStream;
  
  protected SecureRandom secureRandom;
  
  private TlsInputStream tlsInputStream = null;
  
  private TlsOutputStream tlsOutputStream = null;
  
  private volatile boolean closed = false;
  
  private volatile boolean failedWithError = false;
  
  private volatile boolean appDataReady = false;
  
  private volatile boolean appDataSplitEnabled = true;
  
  private volatile int appDataSplitMode = 0;
  
  private byte[] expected_verify_data = null;
  
  protected TlsSession tlsSession = null;
  
  protected SessionParameters sessionParameters = null;
  
  protected SecurityParameters securityParameters = null;
  
  protected Certificate peerCertificate = null;
  
  protected int[] offeredCipherSuites = null;
  
  protected short[] offeredCompressionMethods = null;
  
  protected Hashtable clientExtensions = null;
  
  protected Hashtable serverExtensions = null;
  
  protected short connection_state = 0;
  
  protected boolean resumedSession = false;
  
  protected boolean receivedChangeCipherSpec = false;
  
  protected boolean secure_renegotiation = false;
  
  protected boolean allowCertificateStatus = false;
  
  protected boolean expectSessionTicket = false;
  
  protected boolean blocking = true;
  
  protected ByteQueueInputStream inputBuffers;
  
  protected ByteQueueOutputStream outputBuffer;
  
  public TlsProtocol(InputStream paramInputStream, OutputStream paramOutputStream, SecureRandom paramSecureRandom) {
    this.recordStream = new RecordStream(this, paramInputStream, paramOutputStream);
    this.secureRandom = paramSecureRandom;
  }
  
  public TlsProtocol(SecureRandom paramSecureRandom) {
    this.inputBuffers = new ByteQueueInputStream();
    this.outputBuffer = new ByteQueueOutputStream();
    this.recordStream = new RecordStream(this, this.inputBuffers, this.outputBuffer);
    this.secureRandom = paramSecureRandom;
  }
  
  protected abstract TlsContext getContext();
  
  abstract AbstractTlsContext getContextAdmin();
  
  protected abstract TlsPeer getPeer();
  
  protected void handleAlertMessage(short paramShort1, short paramShort2) throws IOException {
    getPeer().notifyAlertReceived(paramShort1, paramShort2);
    if (paramShort1 == 1) {
      handleAlertWarningMessage(paramShort2);
    } else {
      handleFailure();
      throw new TlsFatalAlertReceived(paramShort2);
    } 
  }
  
  protected void handleAlertWarningMessage(short paramShort) throws IOException {
    if (paramShort == 0) {
      if (!this.appDataReady)
        throw new TlsFatalAlert((short)40); 
      handleClose(false);
    } 
  }
  
  protected void handleChangeCipherSpecMessage() throws IOException {}
  
  protected void handleClose(boolean paramBoolean) throws IOException {
    if (!this.closed) {
      this.closed = true;
      if (paramBoolean && !this.appDataReady)
        raiseAlertWarning((short)90, "User canceled handshake"); 
      raiseAlertWarning((short)0, "Connection closed");
      this.recordStream.safeClose();
      if (!this.appDataReady)
        cleanupHandshake(); 
    } 
  }
  
  protected void handleException(short paramShort, String paramString, Throwable paramThrowable) throws IOException {
    if (!this.closed) {
      raiseAlertFatal(paramShort, paramString, paramThrowable);
      handleFailure();
    } 
  }
  
  protected void handleFailure() {
    this.closed = true;
    this.failedWithError = true;
    invalidateSession();
    this.recordStream.safeClose();
    if (!this.appDataReady)
      cleanupHandshake(); 
  }
  
  protected abstract void handleHandshakeMessage(short paramShort, ByteArrayInputStream paramByteArrayInputStream) throws IOException;
  
  protected void applyMaxFragmentLengthExtension() throws IOException {
    if (this.securityParameters.maxFragmentLength >= 0) {
      if (!MaxFragmentLength.isValid(this.securityParameters.maxFragmentLength))
        throw new TlsFatalAlert((short)80); 
      int i = 1 << 8 + this.securityParameters.maxFragmentLength;
      this.recordStream.setPlaintextLimit(i);
    } 
  }
  
  protected void checkReceivedChangeCipherSpec(boolean paramBoolean) throws IOException {
    if (paramBoolean != this.receivedChangeCipherSpec)
      throw new TlsFatalAlert((short)10); 
  }
  
  protected void cleanupHandshake() {
    if (this.expected_verify_data != null) {
      Arrays.fill(this.expected_verify_data, (byte)0);
      this.expected_verify_data = null;
    } 
    this.securityParameters.clear();
    this.peerCertificate = null;
    this.offeredCipherSuites = null;
    this.offeredCompressionMethods = null;
    this.clientExtensions = null;
    this.serverExtensions = null;
    this.resumedSession = false;
    this.receivedChangeCipherSpec = false;
    this.secure_renegotiation = false;
    this.allowCertificateStatus = false;
    this.expectSessionTicket = false;
  }
  
  protected void blockForHandshake() throws IOException {
    if (this.blocking)
      while (this.connection_state != 16) {
        if (this.closed)
          throw new TlsFatalAlert((short)80); 
        safeReadRecord();
      }  
  }
  
  protected void completeHandshake() throws IOException {
    try {
      this.connection_state = 16;
      this.alertQueue.shrink();
      this.handshakeQueue.shrink();
      this.recordStream.finaliseHandshake();
      this.appDataSplitEnabled = !TlsUtils.isTLSv11(getContext());
      if (!this.appDataReady) {
        this.appDataReady = true;
        if (this.blocking) {
          this.tlsInputStream = new TlsInputStream(this);
          this.tlsOutputStream = new TlsOutputStream(this);
        } 
      } 
      if (this.tlsSession != null) {
        if (this.sessionParameters == null) {
          this.sessionParameters = (new SessionParameters.Builder()).setCipherSuite(this.securityParameters.getCipherSuite()).setCompressionAlgorithm(this.securityParameters.getCompressionAlgorithm()).setMasterSecret(this.securityParameters.getMasterSecret()).setPeerCertificate(this.peerCertificate).setPSKIdentity(this.securityParameters.getPSKIdentity()).setSRPIdentity(this.securityParameters.getSRPIdentity()).setServerExtensions(this.serverExtensions).build();
          this.tlsSession = new TlsSessionImpl(this.tlsSession.getSessionID(), this.sessionParameters);
        } 
        getContextAdmin().setResumableSession(this.tlsSession);
      } 
      getPeer().notifyHandshakeComplete();
    } finally {
      cleanupHandshake();
    } 
  }
  
  protected void processRecord(short paramShort, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    switch (paramShort) {
      case 21:
        this.alertQueue.addData(paramArrayOfbyte, paramInt1, paramInt2);
        processAlertQueue();
        return;
      case 23:
        if (!this.appDataReady)
          throw new TlsFatalAlert((short)10); 
        this.applicationDataQueue.addData(paramArrayOfbyte, paramInt1, paramInt2);
        processApplicationDataQueue();
        return;
      case 20:
        processChangeCipherSpec(paramArrayOfbyte, paramInt1, paramInt2);
        return;
      case 22:
        if (this.handshakeQueue.available() > 0) {
          this.handshakeQueue.addData(paramArrayOfbyte, paramInt1, paramInt2);
          processHandshakeQueue(this.handshakeQueue);
        } else {
          ByteQueue byteQueue = new ByteQueue(paramArrayOfbyte, paramInt1, paramInt2);
          processHandshakeQueue(byteQueue);
          int i = byteQueue.available();
          if (i > 0)
            this.handshakeQueue.addData(paramArrayOfbyte, paramInt1 + paramInt2 - i, i); 
        } 
        return;
    } 
    throw new TlsFatalAlert((short)80);
  }
  
  private void processHandshakeQueue(ByteQueue paramByteQueue) throws IOException {
    while (paramByteQueue.available() >= 4) {
      TlsContext tlsContext;
      byte[] arrayOfByte = new byte[4];
      paramByteQueue.read(arrayOfByte, 0, 4, 0);
      short s = TlsUtils.readUint8(arrayOfByte, 0);
      int i = TlsUtils.readUint24(arrayOfByte, 1);
      int j = 4 + i;
      if (paramByteQueue.available() < j)
        break; 
      checkReceivedChangeCipherSpec((this.connection_state == 16 || s == 20));
      switch (s) {
        case 0:
          break;
        case 20:
          tlsContext = getContext();
          if (this.expected_verify_data == null && tlsContext.getSecurityParameters().getMasterSecret() != null)
            this.expected_verify_data = createVerifyData(!tlsContext.isServer()); 
        default:
          paramByteQueue.copyTo(this.recordStream.getHandshakeHashUpdater(), j);
          break;
      } 
      paramByteQueue.removeData(4);
      ByteArrayInputStream byteArrayInputStream = paramByteQueue.readFrom(i);
      handleHandshakeMessage(s, byteArrayInputStream);
    } 
  }
  
  private void processApplicationDataQueue() {}
  
  private void processAlertQueue() throws IOException {
    while (this.alertQueue.available() >= 2) {
      byte[] arrayOfByte = this.alertQueue.removeData(2, 0);
      short s1 = (short)arrayOfByte[0];
      short s2 = (short)arrayOfByte[1];
      handleAlertMessage(s1, s2);
    } 
  }
  
  private void processChangeCipherSpec(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    for (byte b = 0; b < paramInt2; b++) {
      short s = TlsUtils.readUint8(paramArrayOfbyte, paramInt1 + b);
      if (s != 1)
        throw new TlsFatalAlert((short)50); 
      if (this.receivedChangeCipherSpec || this.alertQueue.available() > 0 || this.handshakeQueue.available() > 0)
        throw new TlsFatalAlert((short)10); 
      this.recordStream.receivedReadCipherSpec();
      this.receivedChangeCipherSpec = true;
      handleChangeCipherSpecMessage();
    } 
  }
  
  protected int applicationDataAvailable() {
    return this.applicationDataQueue.available();
  }
  
  protected int readApplicationData(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 < 1)
      return 0; 
    while (this.applicationDataQueue.available() == 0) {
      if (this.closed) {
        if (this.failedWithError)
          throw new IOException("Cannot read application data on failed TLS connection"); 
        if (!this.appDataReady)
          throw new IllegalStateException("Cannot read application data until initial handshake completed."); 
        return -1;
      } 
      safeReadRecord();
    } 
    paramInt2 = Math.min(paramInt2, this.applicationDataQueue.available());
    this.applicationDataQueue.removeData(paramArrayOfbyte, paramInt1, paramInt2, 0);
    return paramInt2;
  }
  
  protected void safeCheckRecordHeader(byte[] paramArrayOfbyte) throws IOException {
    try {
      this.recordStream.checkRecordHeader(paramArrayOfbyte);
    } catch (TlsFatalAlert tlsFatalAlert) {
      handleException(tlsFatalAlert.getAlertDescription(), "Failed to read record", tlsFatalAlert);
      throw tlsFatalAlert;
    } catch (IOException iOException) {
      handleException((short)80, "Failed to read record", iOException);
      throw iOException;
    } catch (RuntimeException runtimeException) {
      handleException((short)80, "Failed to read record", runtimeException);
      throw new TlsFatalAlert((short)80, runtimeException);
    } 
  }
  
  protected void safeReadRecord() throws IOException {
    try {
      if (this.recordStream.readRecord())
        return; 
      if (!this.appDataReady)
        throw new TlsFatalAlert((short)40); 
    } catch (TlsFatalAlertReceived tlsFatalAlertReceived) {
      throw tlsFatalAlertReceived;
    } catch (TlsFatalAlert tlsFatalAlert) {
      handleException(tlsFatalAlert.getAlertDescription(), "Failed to read record", tlsFatalAlert);
      throw tlsFatalAlert;
    } catch (IOException iOException) {
      handleException((short)80, "Failed to read record", iOException);
      throw iOException;
    } catch (RuntimeException runtimeException) {
      handleException((short)80, "Failed to read record", runtimeException);
      throw new TlsFatalAlert((short)80, runtimeException);
    } 
    handleFailure();
    throw new TlsNoCloseNotifyException();
  }
  
  protected void safeWriteRecord(short paramShort, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    try {
      this.recordStream.writeRecord(paramShort, paramArrayOfbyte, paramInt1, paramInt2);
    } catch (TlsFatalAlert tlsFatalAlert) {
      handleException(tlsFatalAlert.getAlertDescription(), "Failed to write record", tlsFatalAlert);
      throw tlsFatalAlert;
    } catch (IOException iOException) {
      handleException((short)80, "Failed to write record", iOException);
      throw iOException;
    } catch (RuntimeException runtimeException) {
      handleException((short)80, "Failed to write record", runtimeException);
      throw new TlsFatalAlert((short)80, runtimeException);
    } 
  }
  
  protected void writeData(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (this.closed)
      throw new IOException("Cannot write application data on closed/failed TLS connection"); 
    while (paramInt2 > 0) {
      if (this.appDataSplitEnabled)
        switch (this.appDataSplitMode) {
          case 2:
            this.appDataSplitEnabled = false;
          case 1:
            safeWriteRecord((short)23, TlsUtils.EMPTY_BYTES, 0, 0);
            break;
          default:
            safeWriteRecord((short)23, paramArrayOfbyte, paramInt1, 1);
            paramInt1++;
            paramInt2--;
            break;
        }  
      if (paramInt2 > 0) {
        int i = Math.min(paramInt2, this.recordStream.getPlaintextLimit());
        safeWriteRecord((short)23, paramArrayOfbyte, paramInt1, i);
        paramInt1 += i;
        paramInt2 -= i;
      } 
    } 
  }
  
  protected void setAppDataSplitMode(int paramInt) {
    if (paramInt < 0 || paramInt > 2)
      throw new IllegalArgumentException("Illegal appDataSplitMode mode: " + paramInt); 
    this.appDataSplitMode = paramInt;
  }
  
  protected void writeHandshakeMessage(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 < 4)
      throw new TlsFatalAlert((short)80); 
    short s = TlsUtils.readUint8(paramArrayOfbyte, paramInt1);
    if (s != 0)
      this.recordStream.getHandshakeHashUpdater().write(paramArrayOfbyte, paramInt1, paramInt2); 
    int i = 0;
    do {
      int j = Math.min(paramInt2 - i, this.recordStream.getPlaintextLimit());
      safeWriteRecord((short)22, paramArrayOfbyte, paramInt1 + i, j);
      i += j;
    } while (i < paramInt2);
  }
  
  public OutputStream getOutputStream() {
    if (!this.blocking)
      throw new IllegalStateException("Cannot use OutputStream in non-blocking mode! Use offerOutput() instead."); 
    return this.tlsOutputStream;
  }
  
  public InputStream getInputStream() {
    if (!this.blocking)
      throw new IllegalStateException("Cannot use InputStream in non-blocking mode! Use offerInput() instead."); 
    return this.tlsInputStream;
  }
  
  public void closeInput() throws IOException {
    if (this.blocking)
      throw new IllegalStateException("Cannot use closeInput() in blocking mode!"); 
    if (this.closed)
      return; 
    if (this.inputBuffers.available() > 0)
      throw new EOFException(); 
    if (!this.appDataReady)
      throw new TlsFatalAlert((short)40); 
    throw new TlsNoCloseNotifyException();
  }
  
  public void offerInput(byte[] paramArrayOfbyte) throws IOException {
    if (this.blocking)
      throw new IllegalStateException("Cannot use offerInput() in blocking mode! Use getInputStream() instead."); 
    if (this.closed)
      throw new IOException("Connection is closed, cannot accept any more input"); 
    this.inputBuffers.addBytes(paramArrayOfbyte);
    while (this.inputBuffers.available() >= 5) {
      byte[] arrayOfByte = new byte[5];
      this.inputBuffers.peek(arrayOfByte);
      int i = TlsUtils.readUint16(arrayOfByte, 3) + 5;
      if (this.inputBuffers.available() < i) {
        safeCheckRecordHeader(arrayOfByte);
        break;
      } 
      safeReadRecord();
      if (this.closed) {
        if (this.connection_state != 16)
          throw new TlsFatalAlert((short)80); 
        break;
      } 
    } 
  }
  
  public int getAvailableInputBytes() {
    if (this.blocking)
      throw new IllegalStateException("Cannot use getAvailableInputBytes() in blocking mode! Use getInputStream().available() instead."); 
    return applicationDataAvailable();
  }
  
  public int readInput(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (this.blocking)
      throw new IllegalStateException("Cannot use readInput() in blocking mode! Use getInputStream() instead."); 
    try {
      return readApplicationData(paramArrayOfbyte, paramInt1, Math.min(paramInt2, applicationDataAvailable()));
    } catch (IOException iOException) {
      throw new RuntimeException(iOException.toString());
    } 
  }
  
  public void offerOutput(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (this.blocking)
      throw new IllegalStateException("Cannot use offerOutput() in blocking mode! Use getOutputStream() instead."); 
    if (!this.appDataReady)
      throw new IOException("Application data cannot be sent until the handshake is complete!"); 
    writeData(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public int getAvailableOutputBytes() {
    if (this.blocking)
      throw new IllegalStateException("Cannot use getAvailableOutputBytes() in blocking mode! Use getOutputStream() instead."); 
    return this.outputBuffer.getBuffer().available();
  }
  
  public int readOutput(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (this.blocking)
      throw new IllegalStateException("Cannot use readOutput() in blocking mode! Use getOutputStream() instead."); 
    int i = Math.min(getAvailableOutputBytes(), paramInt2);
    this.outputBuffer.getBuffer().removeData(paramArrayOfbyte, paramInt1, i, 0);
    return i;
  }
  
  protected void invalidateSession() {
    if (this.sessionParameters != null) {
      this.sessionParameters.clear();
      this.sessionParameters = null;
    } 
    if (this.tlsSession != null) {
      this.tlsSession.invalidate();
      this.tlsSession = null;
    } 
  }
  
  protected void processFinishedMessage(ByteArrayInputStream paramByteArrayInputStream) throws IOException {
    if (this.expected_verify_data == null)
      throw new TlsFatalAlert((short)80); 
    byte[] arrayOfByte = TlsUtils.readFully(this.expected_verify_data.length, paramByteArrayInputStream);
    assertEmpty(paramByteArrayInputStream);
    if (!Arrays.constantTimeAreEqual(this.expected_verify_data, arrayOfByte))
      throw new TlsFatalAlert((short)51); 
  }
  
  protected void raiseAlertFatal(short paramShort, String paramString, Throwable paramThrowable) throws IOException {
    getPeer().notifyAlertRaised((short)2, paramShort, paramString, paramThrowable);
    byte[] arrayOfByte = { 2, (byte)paramShort };
    try {
      this.recordStream.writeRecord((short)21, arrayOfByte, 0, 2);
    } catch (Exception exception) {}
  }
  
  protected void raiseAlertWarning(short paramShort, String paramString) throws IOException {
    getPeer().notifyAlertRaised((short)1, paramShort, paramString, null);
    byte[] arrayOfByte = { 1, (byte)paramShort };
    safeWriteRecord((short)21, arrayOfByte, 0, 2);
  }
  
  protected void sendCertificateMessage(Certificate paramCertificate) throws IOException {
    if (paramCertificate == null)
      paramCertificate = Certificate.EMPTY_CHAIN; 
    if (paramCertificate.isEmpty()) {
      TlsContext tlsContext = getContext();
      if (!tlsContext.isServer()) {
        ProtocolVersion protocolVersion = getContext().getServerVersion();
        if (protocolVersion.isSSL()) {
          String str = protocolVersion.toString() + " client didn't provide credentials";
          raiseAlertWarning((short)41, str);
          return;
        } 
      } 
    } 
    HandshakeMessage handshakeMessage = new HandshakeMessage((short)11);
    paramCertificate.encode(handshakeMessage);
    handshakeMessage.writeToRecordStream();
  }
  
  protected void sendChangeCipherSpecMessage() throws IOException {
    byte[] arrayOfByte = { 1 };
    safeWriteRecord((short)20, arrayOfByte, 0, arrayOfByte.length);
    this.recordStream.sentWriteCipherSpec();
  }
  
  protected void sendFinishedMessage() throws IOException {
    byte[] arrayOfByte = createVerifyData(getContext().isServer());
    HandshakeMessage handshakeMessage = new HandshakeMessage((short)20, arrayOfByte.length);
    handshakeMessage.write(arrayOfByte);
    handshakeMessage.writeToRecordStream();
  }
  
  protected void sendSupplementalDataMessage(Vector paramVector) throws IOException {
    HandshakeMessage handshakeMessage = new HandshakeMessage((short)23);
    writeSupplementalData(handshakeMessage, paramVector);
    handshakeMessage.writeToRecordStream();
  }
  
  protected byte[] createVerifyData(boolean paramBoolean) {
    TlsContext tlsContext = getContext();
    String str = paramBoolean ? "server finished" : "client finished";
    byte[] arrayOfByte1 = paramBoolean ? TlsUtils.SSL_SERVER : TlsUtils.SSL_CLIENT;
    byte[] arrayOfByte2 = getCurrentPRFHash(tlsContext, this.recordStream.getHandshakeHash(), arrayOfByte1);
    return TlsUtils.calculateVerifyData(tlsContext, str, arrayOfByte2);
  }
  
  public void close() throws IOException {
    handleClose(true);
  }
  
  protected void flush() throws IOException {
    this.recordStream.flush();
  }
  
  public boolean isClosed() {
    return this.closed;
  }
  
  protected short processMaxFragmentLengthExtension(Hashtable paramHashtable1, Hashtable paramHashtable2, short paramShort) throws IOException {
    short s = TlsExtensionsUtils.getMaxFragmentLengthExtension(paramHashtable2);
    if (s >= 0 && (!MaxFragmentLength.isValid(s) || (!this.resumedSession && s != TlsExtensionsUtils.getMaxFragmentLengthExtension(paramHashtable1))))
      throw new TlsFatalAlert(paramShort); 
    return s;
  }
  
  protected void refuseRenegotiation() throws IOException {
    if (TlsUtils.isSSL(getContext()))
      throw new TlsFatalAlert((short)40); 
    raiseAlertWarning((short)100, "Renegotiation not supported");
  }
  
  protected static void assertEmpty(ByteArrayInputStream paramByteArrayInputStream) throws IOException {
    if (paramByteArrayInputStream.available() > 0)
      throw new TlsFatalAlert((short)50); 
  }
  
  protected static byte[] createRandomBlock(boolean paramBoolean, RandomGenerator paramRandomGenerator) {
    byte[] arrayOfByte = new byte[32];
    paramRandomGenerator.nextBytes(arrayOfByte);
    if (paramBoolean)
      TlsUtils.writeGMTUnixTime(arrayOfByte, 0); 
    return arrayOfByte;
  }
  
  protected static byte[] createRenegotiationInfo(byte[] paramArrayOfbyte) throws IOException {
    return TlsUtils.encodeOpaque8(paramArrayOfbyte);
  }
  
  protected static void establishMasterSecret(TlsContext paramTlsContext, TlsKeyExchange paramTlsKeyExchange) throws IOException {
    byte[] arrayOfByte = paramTlsKeyExchange.generatePremasterSecret();
    try {
      (paramTlsContext.getSecurityParameters()).masterSecret = TlsUtils.calculateMasterSecret(paramTlsContext, arrayOfByte);
    } finally {
      if (arrayOfByte != null)
        Arrays.fill(arrayOfByte, (byte)0); 
    } 
  }
  
  protected static byte[] getCurrentPRFHash(TlsContext paramTlsContext, TlsHandshakeHash paramTlsHandshakeHash, byte[] paramArrayOfbyte) {
    Digest digest = paramTlsHandshakeHash.forkPRFHash();
    if (paramArrayOfbyte != null && TlsUtils.isSSL(paramTlsContext))
      digest.update(paramArrayOfbyte, 0, paramArrayOfbyte.length); 
    byte[] arrayOfByte = new byte[digest.getDigestSize()];
    digest.doFinal(arrayOfByte, 0);
    return arrayOfByte;
  }
  
  protected static Hashtable readExtensions(ByteArrayInputStream paramByteArrayInputStream) throws IOException {
    if (paramByteArrayInputStream.available() < 1)
      return null; 
    byte[] arrayOfByte = TlsUtils.readOpaque16(paramByteArrayInputStream);
    assertEmpty(paramByteArrayInputStream);
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
    Hashtable<Object, Object> hashtable = new Hashtable<Object, Object>();
    while (byteArrayInputStream.available() > 0) {
      Integer integer = Integers.valueOf(TlsUtils.readUint16(byteArrayInputStream));
      byte[] arrayOfByte1 = TlsUtils.readOpaque16(byteArrayInputStream);
      if (null != hashtable.put(integer, arrayOfByte1))
        throw new TlsFatalAlert((short)47); 
    } 
    return hashtable;
  }
  
  protected static Vector readSupplementalDataMessage(ByteArrayInputStream paramByteArrayInputStream) throws IOException {
    byte[] arrayOfByte = TlsUtils.readOpaque24(paramByteArrayInputStream);
    assertEmpty(paramByteArrayInputStream);
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
    Vector<SupplementalDataEntry> vector = new Vector();
    while (byteArrayInputStream.available() > 0) {
      int i = TlsUtils.readUint16(byteArrayInputStream);
      byte[] arrayOfByte1 = TlsUtils.readOpaque16(byteArrayInputStream);
      vector.addElement(new SupplementalDataEntry(i, arrayOfByte1));
    } 
    return vector;
  }
  
  protected static void writeExtensions(OutputStream paramOutputStream, Hashtable paramHashtable) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    writeSelectedExtensions(byteArrayOutputStream, paramHashtable, true);
    writeSelectedExtensions(byteArrayOutputStream, paramHashtable, false);
    byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
    TlsUtils.writeOpaque16(arrayOfByte, paramOutputStream);
  }
  
  protected static void writeSelectedExtensions(OutputStream paramOutputStream, Hashtable paramHashtable, boolean paramBoolean) throws IOException {
    Enumeration<Integer> enumeration = paramHashtable.keys();
    while (enumeration.hasMoreElements()) {
      Integer integer = enumeration.nextElement();
      int i = integer.intValue();
      byte[] arrayOfByte = (byte[])paramHashtable.get(integer);
      if (paramBoolean == ((arrayOfByte.length == 0))) {
        TlsUtils.checkUint16(i);
        TlsUtils.writeUint16(i, paramOutputStream);
        TlsUtils.writeOpaque16(arrayOfByte, paramOutputStream);
      } 
    } 
  }
  
  protected static void writeSupplementalData(OutputStream paramOutputStream, Vector<SupplementalDataEntry> paramVector) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    for (byte b = 0; b < paramVector.size(); b++) {
      SupplementalDataEntry supplementalDataEntry = paramVector.elementAt(b);
      int i = supplementalDataEntry.getDataType();
      TlsUtils.checkUint16(i);
      TlsUtils.writeUint16(i, byteArrayOutputStream);
      TlsUtils.writeOpaque16(supplementalDataEntry.getData(), byteArrayOutputStream);
    } 
    byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
    TlsUtils.writeOpaque24(arrayOfByte, paramOutputStream);
  }
  
  protected static int getPRFAlgorithm(TlsContext paramTlsContext, int paramInt) throws IOException {
    boolean bool = TlsUtils.isTLSv12(paramTlsContext);
    switch (paramInt) {
      case 59:
      case 60:
      case 61:
      case 62:
      case 63:
      case 64:
      case 103:
      case 104:
      case 105:
      case 106:
      case 107:
      case 108:
      case 109:
      case 156:
      case 158:
      case 160:
      case 162:
      case 164:
      case 166:
      case 168:
      case 170:
      case 172:
      case 186:
      case 187:
      case 188:
      case 189:
      case 190:
      case 191:
      case 192:
      case 193:
      case 194:
      case 195:
      case 196:
      case 197:
      case 49187:
      case 49189:
      case 49191:
      case 49193:
      case 49195:
      case 49197:
      case 49199:
      case 49201:
      case 49266:
      case 49268:
      case 49270:
      case 49272:
      case 49274:
      case 49276:
      case 49278:
      case 49280:
      case 49282:
      case 49284:
      case 49286:
      case 49288:
      case 49290:
      case 49292:
      case 49294:
      case 49296:
      case 49298:
      case 49308:
      case 49309:
      case 49310:
      case 49311:
      case 49312:
      case 49313:
      case 49314:
      case 49315:
      case 49316:
      case 49317:
      case 49318:
      case 49319:
      case 49320:
      case 49321:
      case 49322:
      case 49323:
      case 49324:
      case 49325:
      case 49326:
      case 49327:
      case 52392:
      case 52393:
      case 52394:
      case 52395:
      case 52396:
      case 52397:
      case 52398:
      case 65280:
      case 65281:
      case 65282:
      case 65283:
      case 65284:
      case 65285:
      case 65296:
      case 65297:
      case 65298:
      case 65299:
      case 65300:
      case 65301:
        if (bool)
          return 1; 
        throw new TlsFatalAlert((short)47);
      case 157:
      case 159:
      case 161:
      case 163:
      case 165:
      case 167:
      case 169:
      case 171:
      case 173:
      case 49188:
      case 49190:
      case 49192:
      case 49194:
      case 49196:
      case 49198:
      case 49200:
      case 49202:
      case 49267:
      case 49269:
      case 49271:
      case 49273:
      case 49275:
      case 49277:
      case 49279:
      case 49281:
      case 49283:
      case 49285:
      case 49287:
      case 49289:
      case 49291:
      case 49293:
      case 49295:
      case 49297:
      case 49299:
        if (bool)
          return 2; 
        throw new TlsFatalAlert((short)47);
      case 175:
      case 177:
      case 179:
      case 181:
      case 183:
      case 185:
      case 49208:
      case 49211:
      case 49301:
      case 49303:
      case 49305:
      case 49307:
        return bool ? 2 : 0;
    } 
    return bool ? 1 : 0;
  }
  
  class HandshakeMessage extends ByteArrayOutputStream {
    HandshakeMessage(short param1Short) throws IOException {
      this(param1Short, 60);
    }
    
    HandshakeMessage(short param1Short, int param1Int) throws IOException {
      super(param1Int + 4);
      TlsUtils.writeUint8(param1Short, this);
      this.count += 3;
    }
    
    void writeToRecordStream() throws IOException {
      int i = this.count - 4;
      TlsUtils.checkUint24(i);
      TlsUtils.writeUint24(i, this.buf, 1);
      TlsProtocol.this.writeHandshakeMessage(this.buf, 0, this.count);
      this.buf = null;
    }
  }
}
