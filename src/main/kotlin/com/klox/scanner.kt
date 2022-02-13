@file:Suppress("SameParameterValue")

package com.klox

private val keywords = mapOf(
  "and" to TokenType.AND,
  "class" to TokenType.CLASS,
  "else" to TokenType.ELSE,
  "false" to TokenType.FALSE,
  "for" to TokenType.FOR,
  "fun" to TokenType.FUN,
  "if" to TokenType.IF,
  "nil" to TokenType.NIL,
  "or" to TokenType.OR,
  "print" to TokenType.PRINT,
  "return" to TokenType.RETURN,
  "super" to TokenType.SUPER,
  "this" to TokenType.THIS,
  "true" to TokenType.TRUE,
  "var" to TokenType.VAR,
  "while" to TokenType.WHILE,
)

class Scanner(
  private val source: String
) {

  private var line = 1
  private var current = 0
  private var start = 0

  private val tokens = arrayListOf<Token>()

  fun scanTokens(): List<Token> {
    while (!isAtEnd()) {
      start = current
      scanToken()
    }

    tokens.add(Token(TokenType.EOF, "", null, line))

    return tokens
  }

  private fun scanToken() {
    val c = advance()

    when {
      c == '(' -> addToken(TokenType.LEFT_PAREN)
      c == ')' -> addToken(TokenType.RIGHT_PAREN)
      c == '{' -> addToken(TokenType.LEFT_BRACE)
      c == '}' -> addToken(TokenType.RIGHT_BRACE)
      c == ',' -> addToken(TokenType.COMMA)
      c == '.' -> addToken(TokenType.DOT)
      c == '-' -> addToken(TokenType.MINUS)
      c == '+' -> addToken(TokenType.PLUS)
      c == ';' -> addToken(TokenType.SEMICOLON)
      c == '*' -> addToken(TokenType.START)

      c == '!' -> addToken(if (matchNext('=')) TokenType.BANG_EQUAL else TokenType.BANG)
      c == '=' -> addToken(if (matchNext('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
      c == '<' -> addToken(if (matchNext('=')) TokenType.LESS_EQUAL else TokenType.LESS)
      c == '>' -> addToken(if (matchNext('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)

      c == '/' -> {
        if (matchNext('/')) { // comments
          while (peek() != '\n' && !isAtEnd()) advance()
        } else {
          addToken(TokenType.SLASH)
        }
      }

      c == ' ' -> {}
      c == '\r' -> {}
      c == '\t' -> {}

      c == '\n' -> line++

      c == '"' -> string()

      isDigit(c) -> number()

      isAlpha(c) -> identifier()

      else -> Klox.error(line, "Unsupported character")
    }
  }

  private fun advance(): Char =
    source[current++]

  private fun addToken(type: TokenType) {
    addToken(type, null)
  }

  private fun addToken(type: TokenType, literal: Any?) {
    val text = source.substring(start, current)
    tokens.add(Token(type, text, literal, line))
  }

  private fun matchNext(expected: Char): Boolean {
    if (isAtEnd()) return false
    if (source[current] != expected) return false

    current++
    return true
  }

  private fun peek(): Char {
    if (isAtEnd()) return '\u0000'
    return source[current]
  }

  private fun peekNext(): Char {
    if (current + 1 >= source.length) return '\u0000'
    return source[current + 1]
  }

  private fun string() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') line++
      advance()
    }

    if (isAtEnd()) {
      Klox.error(line, "Unterminated string")
      return
    }

    advance()

    val text = source.substring(start + 1, current - 1)
    addToken(TokenType.STRING, text)
  }

  private fun number() {
    while (isDigit(peek())) advance()

    if (peek() == '.' && isDigit(peekNext())) {
      advance()
      while (isDigit(peek())) advance()
    }

    addToken(TokenType.NUMBER, source.substring(start, current).toDouble())
  }

  private fun identifier() {
    while (isAlphaNumeric(peek())) advance()

    val text = source.substring(start, current)
    val type = keywords[text] ?: TokenType.IDENTIFIER

    addToken(type)
  }

  private fun isDigit(char: Char): Boolean =
    char in '0'..'9'

  private fun isAtEnd() =
    current >= source.length

  private fun isAlpha(char: Char) =
    (char in 'a'..'z') ||
      (char in 'A'..'Z') ||
      char == '_'

  private fun isAlphaNumeric(char: Char) =
    isAlpha(char) || isDigit(char)
}
