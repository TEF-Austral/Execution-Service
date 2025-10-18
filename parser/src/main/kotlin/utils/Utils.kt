package parser.utils

import Token
import parser.Parser
import parser.result.ParserResult
import type.CommonTypes

fun checkType(
    type: CommonTypes,
    current: Token?,
): Boolean {
    if (current == null) return false
    return current.getType() == type
}

fun isValidResultAndCurrentToken(result: ParserResult): Boolean =
    (result.isSuccess()) && result.getParser().peak() != null

fun verifyCurrentToken(
    types: List<CommonTypes>,
    parser: Parser,
): Boolean {
    for (type in types) {
        if (checkType(type, parser.peak())) {
            return true
        }
    }
    return false
}

fun isSemiColon(token: Token?): Boolean {
    if (token == null) return false
    return token.getType() == CommonTypes.DELIMITERS &&
        token.getValue().trimStart().trimEnd() == ";"
}

fun isOpeningParenthesis(parser: Parser): Boolean {
    val token = parser.peak() ?: return false
    return token.getType() == CommonTypes.DELIMITERS &&
        token.getValue().trimStart().trimEnd() == "("
}

fun isClosingParenthesis(parser: Parser): Boolean {
    val token = parser.peak() ?: return false
    return token.getType() == CommonTypes.DELIMITERS &&
        token.getValue().trimStart().trimEnd() == ")"
}

fun isOpeningBrace(parser: Parser): Boolean {
    val token = parser.peak()
    return checkType(CommonTypes.DELIMITERS, token) &&
        token?.getValue()?.trimStart()?.trimEnd() == "{"
}

fun isClosingBrace(parser: Parser): Boolean {
    val token = parser.peak()
    return checkType(CommonTypes.DELIMITERS, token) &&
        token?.getValue()?.trimStart()?.trimEnd() == "}"
}

fun advancePastSemiColon(parser: Parser): Parser {
    val peak = parser.peak()
    return if (isSemiColon(peak)) {
        parser.consume(CommonTypes.DELIMITERS).getParser()
    } else {
        parser
    }
}
