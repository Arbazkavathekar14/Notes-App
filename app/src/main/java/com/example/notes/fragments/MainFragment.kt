package com.example.notes.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes.R
import com.example.notes.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var notes: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notes = getNotes(requireContext())
        noteAdapter = NoteAdapter(notes) { position ->
            showNoteOptionsDialog(position)
        }

        binding.notesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notesRecyclerView.adapter = noteAdapter

        binding.addNoteButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddNoteFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun getNotes(context: Context): MutableList<String> {
        val sharedPref = context.getSharedPreferences("notes_prefs", Context.MODE_PRIVATE)
        val notesSet = sharedPref.getStringSet("notes", mutableSetOf())
        return notesSet?.toMutableList() ?: mutableListOf()
    }

    private fun saveNotes(context: Context, notes: List<String>) {
        val sharedPref = context.getSharedPreferences("notes_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putStringSet("notes", notes.toSet())
            apply()
        }
    }

    private fun showNoteOptionsDialog(position: Int) {
        val note = notes[position]
        val options = arrayOf("Update Note", "Delete Note")

        AlertDialog.Builder(requireContext())
            .setTitle("Choose an option")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showUpdateNoteDialog(position, note)
                    1 -> deleteNoteAt(position)
                }
            }
            .show()
    }

    private fun showUpdateNoteDialog(position: Int, oldNote: String) {
        val editText = EditText(requireContext())
        editText.setText(oldNote)

        AlertDialog.Builder(requireContext())
            .setTitle("Update Note")
            .setView(editText)
            .setPositiveButton("Update") { _, _ ->
                val newNote = editText.text.toString()
                updateNoteAt(position, newNote)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateNoteAt(position: Int, newNote: String) {
        notes[position] = newNote
        saveNotes(requireContext(), notes)
        noteAdapter.notifyItemChanged(position)
    }

    private fun deleteNoteAt(position: Int) {
        notes.removeAt(position)
        saveNotes(requireContext(), notes)
        noteAdapter.notifyItemRemoved(position)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}



