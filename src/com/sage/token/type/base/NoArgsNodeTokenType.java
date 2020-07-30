package com.sage.token.type.base;

import com.sage.token.*;

import java.util.List;

public interface NoArgsNodeTokenType extends NodeTokenType {
    @Override
    default TokenArgs getArguments(List<Token<?>> tokenList, int idx, ParseMode parseMode) {
        return new TokenArgs(new TokenArgIndices(0, 0), new TokenList[0]);
    }
}
