@file:Suppress("SameParameterValue")

package com.klox

import java.io.File
import kotlin.system.exitProcess

object Klox {

  private var hadError: Boolean = false

  fun run(vararg args: String) {
    when {
      args.isNotEmpty() -> {
        println("Usage: klox [script]")
        exitProcess(64) // sysexits.h
      }
      args.size == 1 -> runFile(args.first())
      else -> runPrompt()
    }
  }

  fun error(line: Int, message: String) {
    report(line, "", message)
  }

  private fun report(line: Int, where: String, message: String) {
    System.err.println("[$line] Error: $where: $message")
    hadError = true
  }

  // from file
  private fun runFile(path: String) {
    run(File(path).readText())

    if (hadError) exitProcess(65)
  }

  // interactive mode
  // todo - when to break?
  private fun runPrompt() {
    var line: String?

    do {
      println("> ")
      line = readln()
      run(line)
      hadError = false
    } while (true)
  }

  private fun run(source: String) {
    val tokens = Scanner(source).scanTokens()

    tokens.forEach { token ->
      println(token)
    }
  }
}
