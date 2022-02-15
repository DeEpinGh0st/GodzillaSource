package com.formdev.flatlaf.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;















































class JsonParser
{
  private static final int MAX_NESTING_LEVEL = 1000;
  private static final int MIN_BUFFER_SIZE = 10;
  private static final int DEFAULT_BUFFER_SIZE = 1024;
  private final JsonHandler<Object, Object> handler;
  private Reader reader;
  private char[] buffer;
  private int bufferOffset;
  private int index;
  private int fill;
  private int line;
  private int lineOffset;
  private int current;
  private StringBuilder captureBuffer;
  private int captureStart;
  private int nestingLevel;
  
  public JsonParser(JsonHandler<?, ?> handler) {
    if (handler == null) {
      throw new NullPointerException("handler is null");
    }
    this.handler = (JsonHandler)handler;
    handler.parser = this;
  }









  
  public void parse(String string) {
    if (string == null) {
      throw new NullPointerException("string is null");
    }
    int bufferSize = Math.max(10, Math.min(1024, string.length()));
    try {
      parse(new StringReader(string), bufferSize);
    } catch (IOException exception) {
      
      throw new RuntimeException(exception);
    } 
  }















  
  public void parse(Reader reader) throws IOException {
    parse(reader, 1024);
  }

















  
  public void parse(Reader reader, int buffersize) throws IOException {
    if (reader == null) {
      throw new NullPointerException("reader is null");
    }
    if (buffersize <= 0) {
      throw new IllegalArgumentException("buffersize is zero or negative");
    }
    this.reader = reader;
    this.buffer = new char[buffersize];
    this.bufferOffset = 0;
    this.index = 0;
    this.fill = 0;
    this.line = 1;
    this.lineOffset = 0;
    this.current = 0;
    this.captureStart = -1;
    read();
    skipWhiteSpace();
    readValue();
    skipWhiteSpace();
    if (!isEndOfText()) {
      throw error("Unexpected character");
    }
  }
  
  private void readValue() throws IOException {
    switch (this.current) {
      case 110:
        readNull();
        return;
      case 116:
        readTrue();
        return;
      case 102:
        readFalse();
        return;
      case 34:
        readString();
        return;
      case 91:
        readArray();
        return;
      case 123:
        readObject();
        return;
      case 45:
      case 48:
      case 49:
      case 50:
      case 51:
      case 52:
      case 53:
      case 54:
      case 55:
      case 56:
      case 57:
        readNumber();
        return;
    } 
    throw expected("value");
  }

  
  private void readArray() throws IOException {
    Object array = this.handler.startArray();
    read();
    if (++this.nestingLevel > 1000) {
      throw error("Nesting too deep");
    }
    skipWhiteSpace();
    if (readChar(']')) {
      this.nestingLevel--;
      this.handler.endArray(array);
      return;
    } 
    while (true) {
      skipWhiteSpace();
      this.handler.startArrayValue(array);
      readValue();
      this.handler.endArrayValue(array);
      skipWhiteSpace();
      if (!readChar(',')) {
        if (!readChar(']')) {
          throw expected("',' or ']'");
        }
        this.nestingLevel--;
        this.handler.endArray(array);
        return;
      } 
    }  }
  private void readObject() throws IOException { Object object = this.handler.startObject();
    read();
    if (++this.nestingLevel > 1000) {
      throw error("Nesting too deep");
    }
    skipWhiteSpace();
    if (readChar('}')) {
      this.nestingLevel--;
      this.handler.endObject(object);
      return;
    } 
    while (true) {
      skipWhiteSpace();
      this.handler.startObjectName(object);
      String name = readName();
      this.handler.endObjectName(object, name);
      skipWhiteSpace();
      if (!readChar(':')) {
        throw expected("':'");
      }
      skipWhiteSpace();
      this.handler.startObjectValue(object, name);
      readValue();
      this.handler.endObjectValue(object, name);
      skipWhiteSpace();
      if (!readChar(',')) {
        if (!readChar('}')) {
          throw expected("',' or '}'");
        }
        this.nestingLevel--;
        this.handler.endObject(object);
        return;
      } 
    }  } private String readName() throws IOException {
    if (this.current != 34) {
      throw expected("name");
    }
    return readStringInternal();
  }
  
  private void readNull() throws IOException {
    this.handler.startNull();
    read();
    readRequiredChar('u');
    readRequiredChar('l');
    readRequiredChar('l');
    this.handler.endNull();
  }
  
  private void readTrue() throws IOException {
    this.handler.startBoolean();
    read();
    readRequiredChar('r');
    readRequiredChar('u');
    readRequiredChar('e');
    this.handler.endBoolean(true);
  }
  
  private void readFalse() throws IOException {
    this.handler.startBoolean();
    read();
    readRequiredChar('a');
    readRequiredChar('l');
    readRequiredChar('s');
    readRequiredChar('e');
    this.handler.endBoolean(false);
  }
  
  private void readRequiredChar(char ch) throws IOException {
    if (!readChar(ch)) {
      throw expected("'" + ch + "'");
    }
  }
  
  private void readString() throws IOException {
    this.handler.startString();
    this.handler.endString(readStringInternal());
  }
  
  private String readStringInternal() throws IOException {
    read();
    startCapture();
    while (this.current != 34) {
      if (this.current == 92) {
        pauseCapture();
        readEscape();
        startCapture(); continue;
      }  if (this.current < 32) {
        throw expected("valid string character");
      }
      read();
    } 
    
    String string = endCapture();
    read();
    return string;
  } private void readEscape() throws IOException {
    char[] hexChars;
    int i;
    read();
    switch (this.current) {
      case 34:
      case 47:
      case 92:
        this.captureBuffer.append((char)this.current);
        break;
      case 98:
        this.captureBuffer.append('\b');
        break;
      case 102:
        this.captureBuffer.append('\f');
        break;
      case 110:
        this.captureBuffer.append('\n');
        break;
      case 114:
        this.captureBuffer.append('\r');
        break;
      case 116:
        this.captureBuffer.append('\t');
        break;
      case 117:
        hexChars = new char[4];
        for (i = 0; i < 4; i++) {
          read();
          if (!isHexDigit()) {
            throw expected("hexadecimal digit");
          }
          hexChars[i] = (char)this.current;
        } 
        this.captureBuffer.append((char)Integer.parseInt(new String(hexChars), 16));
        break;
      default:
        throw expected("valid escape sequence");
    } 
    read();
  }
  
  private void readNumber() throws IOException {
    this.handler.startNumber();
    startCapture();
    readChar('-');
    int firstDigit = this.current;
    if (!readDigit()) {
      throw expected("digit");
    }
    if (firstDigit != 48) {
      while (readDigit());
    }
    
    readFraction();
    readExponent();
    this.handler.endNumber(endCapture());
  }
  
  private boolean readFraction() throws IOException {
    if (!readChar('.')) {
      return false;
    }
    if (!readDigit()) {
      throw expected("digit");
    }
    while (readDigit());
    
    return true;
  }
  
  private boolean readExponent() throws IOException {
    if (!readChar('e') && !readChar('E')) {
      return false;
    }
    if (!readChar('+')) {
      readChar('-');
    }
    if (!readDigit()) {
      throw expected("digit");
    }
    while (readDigit());
    
    return true;
  }
  
  private boolean readChar(char ch) throws IOException {
    if (this.current != ch) {
      return false;
    }
    read();
    return true;
  }
  
  private boolean readDigit() throws IOException {
    if (!isDigit()) {
      return false;
    }
    read();
    return true;
  }
  
  private void skipWhiteSpace() throws IOException {
    while (isWhiteSpace()) {
      read();
    }
  }
  
  private void read() throws IOException {
    if (this.index == this.fill) {
      if (this.captureStart != -1) {
        this.captureBuffer.append(this.buffer, this.captureStart, this.fill - this.captureStart);
        this.captureStart = 0;
      } 
      this.bufferOffset += this.fill;
      this.fill = this.reader.read(this.buffer, 0, this.buffer.length);
      this.index = 0;
      if (this.fill == -1) {
        this.current = -1;
        this.index++;
        return;
      } 
    } 
    if (this.current == 10) {
      this.line++;
      this.lineOffset = this.bufferOffset + this.index;
    } 
    this.current = this.buffer[this.index++];
  }
  
  private void startCapture() {
    if (this.captureBuffer == null) {
      this.captureBuffer = new StringBuilder();
    }
    this.captureStart = this.index - 1;
  }
  
  private void pauseCapture() {
    int end = (this.current == -1) ? this.index : (this.index - 1);
    this.captureBuffer.append(this.buffer, this.captureStart, end - this.captureStart);
    this.captureStart = -1;
  }
  
  private String endCapture() {
    int start = this.captureStart;
    int end = this.index - 1;
    this.captureStart = -1;
    if (this.captureBuffer.length() > 0) {
      this.captureBuffer.append(this.buffer, start, end - start);
      String captured = this.captureBuffer.toString();
      this.captureBuffer.setLength(0);
      return captured;
    } 
    return new String(this.buffer, start, end - start);
  }
  
  Location getLocation() {
    int offset = this.bufferOffset + this.index - 1;
    int column = offset - this.lineOffset + 1;
    return new Location(offset, this.line, column);
  }
  
  private ParseException expected(String expected) {
    if (isEndOfText()) {
      return error("Unexpected end of input");
    }
    return error("Expected " + expected);
  }
  
  private ParseException error(String message) {
    return new ParseException(message, getLocation());
  }
  
  private boolean isWhiteSpace() {
    return (this.current == 32 || this.current == 9 || this.current == 10 || this.current == 13);
  }
  
  private boolean isDigit() {
    return (this.current >= 48 && this.current <= 57);
  }
  
  private boolean isHexDigit() {
    return ((this.current >= 48 && this.current <= 57) || (this.current >= 97 && this.current <= 102) || (this.current >= 65 && this.current <= 70));
  }


  
  private boolean isEndOfText() {
    return (this.current == -1);
  }
}
