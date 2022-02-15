package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;















public final class BlockEntryToken
  extends Token
{
  public BlockEntryToken(Mark startMark, Mark endMark) {
    super(startMark, endMark);
  }

  
  public Token.ID getTokenId() {
    return Token.ID.BlockEntry;
  }
}
