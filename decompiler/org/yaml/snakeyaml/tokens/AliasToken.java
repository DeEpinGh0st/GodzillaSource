package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;














public final class AliasToken
  extends Token
{
  private final String value;
  
  public AliasToken(String value, Mark startMark, Mark endMark) {
    super(startMark, endMark);
    this.value = value;
  }
  
  public String getValue() {
    return this.value;
  }

  
  public Token.ID getTokenId() {
    return Token.ID.Alias;
  }
}
