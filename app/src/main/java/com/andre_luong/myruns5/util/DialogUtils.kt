package com.andre_luong.myruns5.util

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.text.InputType
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.EditText
import android.widget.FrameLayout

// General dialog functions for reusability
object DialogUtils {
    // Returns a double in callback when a user enters a number
    fun showNumberInputDialog(
        context: Context,
        title: String,
        allowDecimal: Boolean = true,
        numberEntered: (Double?) -> Unit
    ) {
        val container = FrameLayout(context)

        // Create number input
        val input = EditText(context).apply {
            layoutParams = buildInputLayoutParams(context.resources)
            if (allowDecimal) {
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            } else {
                inputType = InputType.TYPE_CLASS_NUMBER
            }
        }

        container.addView(input)

        AlertDialog.Builder(context)
            .setTitle(title)
            .setView(container)
            .setPositiveButton("Ok") { _, _ ->
                numberEntered(input.text.toString().toDoubleOrNull())
            }
            .setNegativeButton("Cancel") { _, _ ->
                numberEntered(null)
            }
            .create()
            .show()
    }

    // Returns a string in callback when a user enters a comment
    fun showCommentDialog(context: Context, initialText: String, commentEntered: (String?) -> Unit) {
        val container = FrameLayout(context)

        // Create text input
        val input = EditText(context).apply {
            layoutParams = buildInputLayoutParams(context.resources)
            setText(initialText)
        }

        container.addView(input)

        AlertDialog.Builder(context)
            .setTitle("Comment")
            .setView(container)
            .setPositiveButton("Ok") { _, _ ->
                commentEntered(input.text.toString())
            }
            .setNegativeButton("Cancel") { _, _ ->
                commentEntered(null)
            }
            .create()
            .show()
    }

    // Returns layout parameters for an input text dialog
    private fun buildInputLayoutParams(resources: Resources): FrameLayout.LayoutParams {
        // Converts 20dp to pixels
        val marginInDp = TypedValue.applyDimension(
            COMPLEX_UNIT_DIP,
            20f,
            resources.displayMetrics
        ).toInt()

        val params = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        params.setMargins(marginInDp, marginInDp / 2, marginInDp, marginInDp / 2)
        return params
    }
}