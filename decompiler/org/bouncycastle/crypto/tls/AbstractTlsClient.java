package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

public abstract class AbstractTlsClient extends AbstractTlsPeer implements TlsClient {
  protected TlsCipherFactory cipherFactory;
  
  protected TlsClientContext context;
  
  protected Vector supportedSignatureAlgorithms;
  
  protected int[] namedCurves;
  
  protected short[] clientECPointFormats;
  
  protected short[] serverECPointFormats;
  
  protected int selectedCipherSuite;
  
  protected short selectedCompressionMethod;
  
  public AbstractTlsClient() {
    this(new DefaultTlsCipherFactory());
  }
  
  public AbstractTlsClient(TlsCipherFactory paramTlsCipherFactory) {
    this.cipherFactory = paramTlsCipherFactory;
  }
  
  protected boolean allowUnexpectedServerExtension(Integer paramInteger, byte[] paramArrayOfbyte) throws IOException {
    switch (paramInteger.intValue()) {
      case 10:
        TlsECCUtils.readSupportedEllipticCurvesExtension(paramArrayOfbyte);
        return true;
    } 
    return false;
  }
  
  protected void checkForUnexpectedServerExtension(Hashtable paramHashtable, Integer paramInteger) throws IOException {
    byte[] arrayOfByte = TlsUtils.getExtensionData(paramHashtable, paramInteger);
    if (arrayOfByte != null && !allowUnexpectedServerExtension(paramInteger, arrayOfByte))
      throw new TlsFatalAlert((short)47); 
  }
  
  public void init(TlsClientContext paramTlsClientContext) {
    this.context = paramTlsClientContext;
  }
  
  public TlsSession getSessionToResume() {
    return null;
  }
  
  public ProtocolVersion getClientHelloRecordLayerVersion() {
    return getClientVersion();
  }
  
  public ProtocolVersion getClientVersion() {
    return ProtocolVersion.TLSv12;
  }
  
  public boolean isFallback() {
    return false;
  }
  
  public Hashtable getClientExtensions() throws IOException {
    Hashtable hashtable = null;
    ProtocolVersion protocolVersion = this.context.getClientVersion();
    if (TlsUtils.isSignatureAlgorithmsExtensionAllowed(protocolVersion)) {
      this.supportedSignatureAlgorithms = TlsUtils.getDefaultSupportedSignatureAlgorithms();
      hashtable = TlsExtensionsUtils.ensureExtensionsInitialised(hashtable);
      TlsUtils.addSignatureAlgorithmsExtension(hashtable, this.supportedSignatureAlgorithms);
    } 
    if (TlsECCUtils.containsECCCipherSuites(getCipherSuites())) {
      this.namedCurves = new int[] { 23, 24 };
      this.clientECPointFormats = new short[] { 0, 1, 2 };
      hashtable = TlsExtensionsUtils.ensureExtensionsInitialised(hashtable);
      TlsECCUtils.addSupportedEllipticCurvesExtension(hashtable, this.namedCurves);
      TlsECCUtils.addSupportedPointFormatsExtension(hashtable, this.clientECPointFormats);
    } 
    return hashtable;
  }
  
  public ProtocolVersion getMinimumVersion() {
    return ProtocolVersion.TLSv10;
  }
  
  public void notifyServerVersion(ProtocolVersion paramProtocolVersion) throws IOException {
    if (!getMinimumVersion().isEqualOrEarlierVersionOf(paramProtocolVersion))
      throw new TlsFatalAlert((short)70); 
  }
  
  public short[] getCompressionMethods() {
    return new short[] { 0 };
  }
  
  public void notifySessionID(byte[] paramArrayOfbyte) {}
  
  public void notifySelectedCipherSuite(int paramInt) {
    this.selectedCipherSuite = paramInt;
  }
  
  public void notifySelectedCompressionMethod(short paramShort) {
    this.selectedCompressionMethod = paramShort;
  }
  
  public void processServerExtensions(Hashtable paramHashtable) throws IOException {
    if (paramHashtable != null) {
      checkForUnexpectedServerExtension(paramHashtable, TlsUtils.EXT_signature_algorithms);
      checkForUnexpectedServerExtension(paramHashtable, TlsECCUtils.EXT_elliptic_curves);
      if (TlsECCUtils.isECCCipherSuite(this.selectedCipherSuite)) {
        this.serverECPointFormats = TlsECCUtils.getSupportedPointFormatsExtension(paramHashtable);
      } else {
        checkForUnexpectedServerExtension(paramHashtable, TlsECCUtils.EXT_ec_point_formats);
      } 
      checkForUnexpectedServerExtension(paramHashtable, TlsExtensionsUtils.EXT_padding);
    } 
  }
  
  public void processServerSupplementalData(Vector paramVector) throws IOException {
    if (paramVector != null)
      throw new TlsFatalAlert((short)10); 
  }
  
  public Vector getClientSupplementalData() throws IOException {
    return null;
  }
  
  public TlsCompression getCompression() throws IOException {
    switch (this.selectedCompressionMethod) {
      case 0:
        return new TlsNullCompression();
    } 
    throw new TlsFatalAlert((short)80);
  }
  
  public TlsCipher getCipher() throws IOException {
    int i = TlsUtils.getEncryptionAlgorithm(this.selectedCipherSuite);
    int j = TlsUtils.getMACAlgorithm(this.selectedCipherSuite);
    return this.cipherFactory.createCipher(this.context, i, j);
  }
  
  public void notifyNewSessionTicket(NewSessionTicket paramNewSessionTicket) throws IOException {}
}
