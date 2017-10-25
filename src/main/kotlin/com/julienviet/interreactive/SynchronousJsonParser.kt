package com.julienviet.interreactive

class SynchronousJsonParser(val stream: Iterator<Char>) {

  var c: Char? = null

  fun parse() {
    c = if (stream.hasNext()) stream.next() else null
    parseElement()
  }

  fun parseElement() {
    when (c) {
      'n' -> parseNull()
      't' -> parseTrue()
      'f' -> parseFalse()
      '"' -> parseString()
      '[' -> parseArray()
      '{' -> parseObject()
      in '0'..'9' -> parseNumber()
      '-' -> parseNumber()
      else -> throw IllegalStateException()
    }
  }

  fun parseNull() {
    assertChar('n')
    assertChar('u')
    assertChar('l')
    assertChar('l')
  }

  fun parseObject() {
    assertChar('{')
    if (c == '}') {
      assertChar('}')
    } else {
      assertChar('"')
      while (c != '"') {
        when (c) {
          in 'A'..'Z' -> assertChar()
          in 'a'..'z' -> assertChar()
          '"' -> {}
          else -> throw IllegalStateException()
        }
      }
      assertChar('"')
      assertChar(':')
      parseElement()
    }
  }

  fun parseTrue() {
  }

  fun parseFalse() {
  }

  fun parseString() {
  }

  fun parseArray() {
  }

  fun parseNumber() {
  }

  fun assertChar(expected: Char? = null) {
    if (expected != null && c != expected) {
      throw IllegalStateException()
    }
    if (stream.hasNext()) {
      c = stream.next()
    } else {
      c = null
    }
  }
}
