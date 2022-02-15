package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;















public final class KeyToken
  extends Token
{
  public KeyToken(Mark startMark, Mark endMark) {
    super(startMark, endMark);
  }

  
  public Token.ID getTokenId() {
    return Token.ID.Key;
  }
}
