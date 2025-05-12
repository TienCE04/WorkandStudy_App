package com.example.workandstudy_app.data_school_schedule

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class DesignListSubject(private val space: Int): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = 0
        } else {
            outRect.top = space
        }
    }
}