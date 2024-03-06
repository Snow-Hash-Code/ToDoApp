package com.example.lista_de_tareas_app

import android.app.AlertDialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.lista_de_tareas_app.adapter.ToDoAdapter

class RecyclerItemTouchHelper(
    private val adapter: ToDoAdapter
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        TODO("Not yet implemented")
    }

    @Suppress("DEPRECATION")
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition

        if(direction == ItemTouchHelper.LEFT){
            AlertDialog.Builder(adapter.getContext()).apply {
                setTitle("Delete task")
                setMessage("¿Estas seguro de que deseas eliminar esta tarea?")
                setPositiveButton("Confirm"){_,_ ->
                    adapter.deleteItem(position)
                }
                setNegativeButton(android.R.string.cancel){
                        dialog, _ ->
                    adapter.notifyItemChanged(viewHolder.adapterPosition)
                    dialog.dismiss()
                }
            }.create().show()
        }else{
            println("edit")
            adapter.editItem(position)
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val icon : Drawable?
        val background : ColorDrawable
        val itemView = viewHolder.itemView
        val backgroundCornerOffset = 20

        if(dX >0){
            icon = ContextCompat.getDrawable(adapter.getContext(),R.drawable.ic_baseline_edit)
            background = ColorDrawable(ContextCompat.getColor(/* context = */ adapter.getContext(),/* id = */
                android.R.color.holo_orange_dark))
        }else{
            icon = ContextCompat.getDrawable(adapter.getContext(),R.drawable.baseline_delete)
            background = ColorDrawable(Color.RED)
        }

        icon?.let {
            val iconMargin = (itemView.height - it.intrinsicHeight)/2
            val iconTop = itemView.top + (itemView.height - it.intrinsicHeight) / 2
            val iconBottom = iconTop + it.intrinsicHeight

            if (dX > 0) { // Swiping to the right
                val iconLeft = itemView.left + iconMargin
                val iconRight = itemView.left + iconMargin + it.intrinsicWidth
                it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt() + backgroundCornerOffset, itemView.bottom)
            } else if (dX < 0) { // Swiping to the left
                val iconLeft = itemView.right - iconMargin - it.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                background.setBounds(itemView.right + dX.toInt() - backgroundCornerOffset, itemView.top, itemView.right, itemView.bottom)
            } else { // View is unSwiped
                background.setBounds(0, 0, 0, 0)
            }
            background.draw(c)
            it.draw(c)
        }
    }
}