package org.bouncycastle.crypto;

public enum PasswordConverter implements CharToByteConverter {
  ASCII {
    public String getType() {
      return "ASCII";
    }
    
    public byte[] convert(char[] param1ArrayOfchar) {
      return PBEParametersGenerator.PKCS5PasswordToBytes(param1ArrayOfchar);
    }
  },
  UTF8 {
    public String getType() {
      return "UTF8";
    }
    
    public byte[] convert(char[] param1ArrayOfchar) {
      return PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(param1ArrayOfchar);
    }
  },
  PKCS12 {
    public String getType() {
      return "PKCS12";
    }
    
    public byte[] convert(char[] param1ArrayOfchar) {
      return PBEParametersGenerator.PKCS12PasswordToBytes(param1ArrayOfchar);
    }
  };
}
