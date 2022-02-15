package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;















public final class BlockEndToken
  extends Token
{
  public BlockEndToken(Mark startMark, Mark endMark) {
    super(startMark, endMark);
  }

  
  public Token.ID getTokenId() {
    return Token.ID.BlockEnd;
  }
}
