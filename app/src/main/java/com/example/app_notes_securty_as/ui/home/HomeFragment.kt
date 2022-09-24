package com.example.app_notes_securty_as.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.example.app_notes_securty_as.R
import com.example.app_notes_securty_as.databinding.FragmentHomeBinding
import com.example.app_notes_securty_as.ui.form.NoteFormActivity

class HomeFragment : Fragment() {

    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding!!


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




        return binding.root
    }

}