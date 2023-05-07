package com.westik.file.me.helpers

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class FileItemDecorator(private val drawable: Drawable) : RecyclerView.ItemDecoration() {

    private val mBounds = Rect()

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        drawDecorator(canvas, parent)
    }

    private fun drawDecorator(canvas: Canvas, parent: RecyclerView) {
        val left = 0
        val right = parent.width

        for (i in 0 until (parent.childCount - 1).coerceAtLeast(0)) {
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, mBounds)
            val bottom = mBounds.bottom + child.translationY.roundToInt()
            val top = bottom - drawable.intrinsicHeight
            drawable.setBounds(left, top, right, bottom)
            drawable.draw(canvas)
        }

    }


}