package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;















public final class FlowEntryToken
  extends Token
{
  public FlowEntryToken(Mark startMark, Mark endMark) {
    super(startMark, endMark);
  }

  
  public Token.ID getTokenId() {
    return Token.ID.FlowEntry;
  }
}
