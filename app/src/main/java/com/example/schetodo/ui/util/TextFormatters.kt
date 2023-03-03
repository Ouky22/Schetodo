package com.example.schetodo.ui.util

import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

fun formatToListWithDotsString(listItems: List<String>) =
    buildAnnotatedString {
        listItems.forEach {
            withStyle(style = ParagraphStyle(textIndent = TextIndent(restLine = 12.sp))) {
                append("\u2022")
                append("\t\t")
                append(it)
                append("\n")
            }
        }
    }