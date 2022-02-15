package org.bouncycastle.util.encoders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class UrlBase64 {
  private static final Encoder encoder = new UrlBase64Encoder();
  
  public static byte[] encode(byte[] paramArrayOfbyte) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try {
      encoder.encode(paramArrayOfbyte, 0, paramArrayOfbyte.length, byteArrayOutputStream);
    } catch (Exception exception) {
      throw new EncoderException("exception encoding URL safe base64 data: " + exception.getMessage(), exception);
    } 
    return byteArrayOutputStream.toByteArray();
  }
  
  public static int encode(byte[] paramArrayOfbyte, OutputStream paramOutputStream) throws IOException {
    return encoder.encode(paramArrayOfbyte, 0, paramArrayOfbyte.length, paramOutputStream);
  }
  
  public static byte[] decode(byte[] paramArrayOfbyte) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try {
      encoder.decode(paramArrayOfbyte, 0, paramArrayOfbyte.length, byteArrayOutputStream);
    } catch (Exception exception) {
      throw new DecoderException("exception decoding URL safe base64 string: " + exception.getMessage(), exception);
    } 
    return byteArrayOutputStream.toByteArray();
  }
  
  public static int decode(byte[] paramArrayOfbyte, OutputStream paramOutputStream) throws IOException {
    return encoder.decode(paramArrayOfbyte, 0, paramArrayOfbyte.length, paramOutputStream);
  }
  
  public static byte[] decode(String paramString) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try {
      encoder.decode(paramString, byteArrayOutputStream);
    } catch (Exception exception) {
      throw new DecoderException("exception decoding URL safe base64 string: " + exception.getMessage(), exception);
    } 
    return byteArrayOutputStream.toByteArray();
  }
  
  public static int decode(String paramString, OutputStream paramOutputStream) throws IOException {
    return encoder.decode(paramString, paramOutputStream);
  }
}
