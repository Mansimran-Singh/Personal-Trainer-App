package com.lemedebug.personaltrainer.exercise

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MuscleDecoration : RecyclerView.ItemDecoration() {


    override fun getItemOffsets(outRect : Rect, view : View, parent : RecyclerView, state : RecyclerView.State) {
        outRect.set(0, 0, 0, 0)  // args is : left,top,right,bottom
    }
}