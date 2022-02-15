package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;















public final class FlowMappingStartToken
  extends Token
{
  public FlowMappingStartToken(Mark startMark, Mark endMark) {
    super(startMark, endMark);
  }

  
  public Token.ID getTokenId() {
    return Token.ID.FlowMappingStart;
  }
}
