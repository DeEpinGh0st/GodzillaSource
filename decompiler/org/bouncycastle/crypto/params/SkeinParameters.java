package org.bouncycastle.crypto.params;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.Integers;

public class SkeinParameters implements CipherParameters {
  public static final int PARAM_TYPE_KEY = 0;
  
  public static final int PARAM_TYPE_CONFIG = 4;
  
  public static final int PARAM_TYPE_PERSONALISATION = 8;
  
  public static final int PARAM_TYPE_PUBLIC_KEY = 12;
  
  public static final int PARAM_TYPE_KEY_IDENTIFIER = 16;
  
  public static final int PARAM_TYPE_NONCE = 20;
  
  public static final int PARAM_TYPE_MESSAGE = 48;
  
  public static final int PARAM_TYPE_OUTPUT = 63;
  
  private Hashtable parameters;
  
  public SkeinParameters() {
    this(new Hashtable<Object, Object>());
  }
  
  private SkeinParameters(Hashtable paramHashtable) {
    this.parameters = paramHashtable;
  }
  
  public Hashtable getParameters() {
    return this.parameters;
  }
  
  public byte[] getKey() {
    return (byte[])this.parameters.get(Integers.valueOf(0));
  }
  
  public byte[] getPersonalisation() {
    return (byte[])this.parameters.get(Integers.valueOf(8));
  }
  
  public byte[] getPublicKey() {
    return (byte[])this.parameters.get(Integers.valueOf(12));
  }
  
  public byte[] getKeyIdentifier() {
    return (byte[])this.parameters.get(Integers.valueOf(16));
  }
  
  public byte[] getNonce() {
    return (byte[])this.parameters.get(Integers.valueOf(20));
  }
  
  public static class Builder {
    private Hashtable parameters = new Hashtable<Object, Object>();
    
    public Builder() {}
    
    public Builder(Hashtable param1Hashtable) {
      Enumeration<Integer> enumeration = param1Hashtable.keys();
      while (enumeration.hasMoreElements()) {
        Integer integer = enumeration.nextElement();
        this.parameters.put(integer, param1Hashtable.get(integer));
      } 
    }
    
    public Builder(SkeinParameters param1SkeinParameters) {
      Enumeration<Integer> enumeration = param1SkeinParameters.parameters.keys();
      while (enumeration.hasMoreElements()) {
        Integer integer = enumeration.nextElement();
        this.parameters.put(integer, param1SkeinParameters.parameters.get(integer));
      } 
    }
    
    public Builder set(int param1Int, byte[] param1ArrayOfbyte) {
      if (param1ArrayOfbyte == null)
        throw new IllegalArgumentException("Parameter value must not be null."); 
      if (param1Int != 0 && (param1Int <= 4 || param1Int >= 63 || param1Int == 48))
        throw new IllegalArgumentException("Parameter types must be in the range 0,5..47,49..62."); 
      if (param1Int == 4)
        throw new IllegalArgumentException("Parameter type 4 is reserved for internal use."); 
      this.parameters.put(Integers.valueOf(param1Int), param1ArrayOfbyte);
      return this;
    }
    
    public Builder setKey(byte[] param1ArrayOfbyte) {
      return set(0, param1ArrayOfbyte);
    }
    
    public Builder setPersonalisation(byte[] param1ArrayOfbyte) {
      return set(8, param1ArrayOfbyte);
    }
    
    public Builder setPersonalisation(Date param1Date, String param1String1, String param1String2) {
      try {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream, "UTF-8");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYYMMDD");
        outputStreamWriter.write(simpleDateFormat.format(param1Date));
        outputStreamWriter.write(" ");
        outputStreamWriter.write(param1String1);
        outputStreamWriter.write(" ");
        outputStreamWriter.write(param1String2);
        outputStreamWriter.close();
        return set(8, byteArrayOutputStream.toByteArray());
      } catch (IOException iOException) {
        throw new IllegalStateException("Byte I/O failed: " + iOException);
      } 
    }
    
    public Builder setPersonalisation(Date param1Date, Locale param1Locale, String param1String1, String param1String2) {
      try {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream, "UTF-8");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYYMMDD", param1Locale);
        outputStreamWriter.write(simpleDateFormat.format(param1Date));
        outputStreamWriter.write(" ");
        outputStreamWriter.write(param1String1);
        outputStreamWriter.write(" ");
        outputStreamWriter.write(param1String2);
        outputStreamWriter.close();
        return set(8, byteArrayOutputStream.toByteArray());
      } catch (IOException iOException) {
        throw new IllegalStateException("Byte I/O failed: " + iOException);
      } 
    }
    
    public Builder setPublicKey(byte[] param1ArrayOfbyte) {
      return set(12, param1ArrayOfbyte);
    }
    
    public Builder setKeyIdentifier(byte[] param1ArrayOfbyte) {
      return set(16, param1ArrayOfbyte);
    }
    
    public Builder setNonce(byte[] param1ArrayOfbyte) {
      return set(20, param1ArrayOfbyte);
    }
    
    public SkeinParameters build() {
      return new SkeinParameters(this.parameters);
    }
  }
}
