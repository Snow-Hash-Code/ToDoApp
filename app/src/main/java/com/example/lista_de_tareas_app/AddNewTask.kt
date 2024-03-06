package com.example.lista_de_tareas_app

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.lista_de_tareas_app.model.TodoModel
import com.example.lista_de_tareas_app.utils.DatabaseHandler


class AddNewTask : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "ActionBottomDialog"

        fun newInstance(): AddNewTask { return AddNewTask()}
    }

    private lateinit var newTaskText: EditText
    private lateinit var newTaskSaveButton: Button
    private lateinit var db: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.new_task, container, false)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newTaskText = view.findViewById(R.id.newTaskEditText)
        newTaskSaveButton = view.findViewById(R.id.newTaskButton)

        activity?.let {
            db = DatabaseHandler(it)
        }
        db.OpenDatabase()

        var isUpdate = false

        arguments?.let { bundle ->
            isUpdate = true
            val task = bundle.getString("task")
            newTaskText.setText(task)
            if (task?.isNotEmpty() == true) {
                newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            }
        }

        val hasText = newTaskText.text.isNotEmpty()
        val buttonColor = if (hasText) ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        else ContextCompat.getColor(requireContext(), android.R.color.darker_gray)
        newTaskSaveButton.backgroundTintList = ColorStateList.valueOf(buttonColor)
        newTaskSaveButton.setTextColor(Color.WHITE)
        newTaskText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val hasText = s.isNotEmpty()
                newTaskSaveButton.isEnabled = hasText

                // Cambia el color de fondo del botón
                val color = if (hasText) ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                else ContextCompat.getColor(requireContext(), android.R.color.darker_gray)
                newTaskSaveButton.backgroundTintList = ColorStateList.valueOf(color)
            }

            override fun afterTextChanged(s: Editable) {}
        })

        newTaskSaveButton.setOnClickListener {
            val text = newTaskText.text.toString().trim() // Usar trim() para eliminar espacios en blanco al inicio y al final

            // Asegúrate de que 'text' no esté vacío antes de proceder
            if (text.isNotEmpty()) {
                if (isUpdate) {
                    arguments?.getInt("id")?.let { id ->
                        // Solo actualiza si hay texto
                        db.updateTask(id, text)
                    }
                } else {
                    // Solo inserta una nueva tarea si hay texto
                    val task = TodoModel().apply {
                        this.task = text
                        status = 0
                    }
                    db.insertTask(task)
                }
                dismiss() // Cierra el BottomSheetDialogFragment solo si se realiza una acción
            } else {
                // Opcional: Mostrar un mensaje al usuario indicando que el texto no puede estar vacío
                Toast.makeText(context, "La tarea no puede estar vacía", Toast.LENGTH_SHORT).show()
            }
        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val activity = activity
        if (activity is DialogCloseListener) {
            (activity as DialogCloseListener).handleDialogClose(dialog)
        }
    }
}
