package com.example.app_notes_securty_as.ui.home

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_notes_securty_as.R
import com.example.app_notes_securty_as.databinding.FragmentHomeBinding
import com.example.app_notes_securty_as.domain.models.Note
import com.example.app_notes_securty_as.service.NoteDAO
import com.example.app_notes_securty_as.ui.form.NoteFormActivity
import com.example.app_notes_securty_as.ui.recyclerview.NoteAdapter
import com.example.app_notes_securty_as.ui.recyclerview.NoteListener

class HomeFragment : Fragment(), NoteListener {

    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding!!
    //-----database----
    private val noteDAO = NoteDAO()
    //---RecyclerView---
    private lateinit var note : ArrayList<Note>
    private lateinit var lstNote: RecyclerView
    private lateinit var myAdapter: NoteAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.btnNoteform.setOnClickListener {
            val formIntent = Intent(context, NoteFormActivity::class.java)
            startActivity(formIntent)

        }


        //-----------------adapter
        lstNote = binding.rowNotes
        lstNote.layoutManager = LinearLayoutManager(context)

        myAdapter = NoteAdapter(this)
        myAdapter.noteList
        lstNote.adapter = myAdapter


        setNotes()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setNotes()
    }

    override fun getNote(position: Int) {
        TODO("Not yet implemented")
    }


    fun setNotes(){
        noteDAO.getNotes()
            .addOnSuccessListener {

                var notes = ArrayList<Note>()
                for (document in it) {

                    notes.add(document.toObject(Note::class.java))
                }

                myAdapter.noteList = notes
                lstNote.adapter = myAdapter

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error get document => ${exception.message}")
            }


    }

}