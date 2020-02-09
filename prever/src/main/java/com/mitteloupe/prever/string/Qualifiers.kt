package com.mitteloupe.prever.string

private val dateRegex = ".*(date|expire|valid|expiration).*".toRegex(RegexOption.IGNORE_CASE)
private val titleRegex = "(^|.*(\\s|_))(title|label).*".toRegex(RegexOption.IGNORE_CASE)
private val subtitleRegex = ".*(subtitle).*".toRegex(RegexOption.IGNORE_CASE)
private val blurbRegex = ".*(blurb|text|description|body).*".toRegex(RegexOption.IGNORE_CASE)
private val generatedRegex = "((sub)?item) \\d+".toRegex(RegexOption.IGNORE_CASE)

fun String.isDate() = matches(dateRegex)

fun String.isTitle() = matches(titleRegex)

fun String.isSubtitle() = matches(subtitleRegex)

fun String.isBlurb() = matches(blurbRegex)

fun String.isGenerated() = matches(generatedRegex)
