package com.oyosite.ticon.specutils.util

import java.util.Locale

val String.capitalized get() = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
val String.toEnglishName get() = lowercase().split("_").joinToString(" ") { it.capitalized }