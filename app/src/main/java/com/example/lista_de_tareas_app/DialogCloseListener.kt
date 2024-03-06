package com.example.lista_de_tareas_app

import android.content.DialogInterface

interface DialogCloseListener {
    fun handleDialogClose(dialog : DialogInterface)
}