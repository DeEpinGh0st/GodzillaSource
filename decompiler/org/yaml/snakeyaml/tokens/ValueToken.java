package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;















public final class ValueToken
  extends Token
{
  public ValueToken(Mark startMark, Mark endMark) {
    super(startMark, endMark);
  }

  
  public Token.ID getTokenId() {
    return Token.ID.Value;
  }
}
