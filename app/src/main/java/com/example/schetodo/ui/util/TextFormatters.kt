package com.example.schetodo.ui.util

import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

fun appendDotsToStrings(strings: List<String>, separator: String) =
    buildAnnotatedString {
        strings.forEachIndexed { index, element ->
            withStyle(style = ParagraphStyle(textIndent = TextIndent(restLine = 12.sp))) {
                append("\u2022")
                append("\t\t")
                append(element)
                if (index != strings.lastIndex)
                    append(separator)
            }
        }
    }