package com.example.workandstudy_app.document.Document_Activity

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(private val space: Int): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        if(parent.getChildAdapterPosition(view)==0){
            outRect.top=space
        }
        else{
            outRect.top=14
        }
    }
}