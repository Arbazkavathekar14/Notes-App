package com.example.notes.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.notes.databinding.FragmentAddNoteBinding


class AddNoteFragment : Fragment() {

    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

                binding.saveNoteButton.setOnClickListener {
            val noteContent = binding.noteEditText.text.toString()
            saveNote(requireContext(), noteContent)
            parentFragmentManager.popBackStack()
        }
    }

    private fun saveNote(context: Context, note: String) {
        val sharedPref = context.getSharedPreferences("notes_prefs", Context.MODE_PRIVATE)
        val notesSet = sharedPref.getStringSet("notes", mutableSetOf())?.toMutableSet()
        notesSet?.add(note)
        with(sharedPref.edit()) {
            putStringSet("notes", notesSet)
            apply()
        }
    }
}
