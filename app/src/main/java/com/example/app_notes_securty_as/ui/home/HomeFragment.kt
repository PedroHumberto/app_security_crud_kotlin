package com.example.app_notes_securty_as.ui.home

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_notes_securty_as.MainActivity
import com.example.app_notes_securty_as.databinding.FragmentHomeBinding
import com.example.app_notes_securty_as.domain.models.Note
import com.example.app_notes_securty_as.domain.models.User
import com.example.app_notes_securty_as.service.NoteDAO
import com.example.app_notes_securty_as.service.UserDAO
import com.example.app_notes_securty_as.ui.form.NoteFormActivity
import com.example.app_notes_securty_as.ui.recyclerview.NoteAdapter
import com.example.app_notes_securty_as.ui.recyclerview.NoteListener
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(), NoteListener {

    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding!!

    //-----database----
    private val noteDAO = NoteDAO()

    //---RecyclerView---
    private lateinit var lstNote: RecyclerView
    private lateinit var myAdapter: NoteAdapter
    //Get User Auth
    private lateinit var mAuth: FirebaseAuth
    private val userDAO = UserDAO()
    private lateinit var db: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        mAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        setUser()



        binding.btnLogout.setOnClickListener {
            mAuth.signOut()
            val loginIntent = Intent(context, MainActivity::class.java)
            startActivity(loginIntent)
        }

        binding.btnNoteform.setOnClickListener {
            val formIntent = Intent(context, NoteFormActivity::class.java)
            startActivity(formIntent)

        }
        context?.let { MobileAds.initialize(it) {} }

        var mAdManagerAdView = binding.adManagerAdView
        val adRequest = AdManagerAdRequest.Builder().build()
        mAdManagerAdView.loadAd(adRequest)

        binding.btnPremium.setOnClickListener {
            binding.adManagerAdView.visibility = View.INVISIBLE
        }


        //-----------------adapter
        lstNote = binding.rowNotes
        lstNote.layoutManager = LinearLayoutManager(context)

        myAdapter = NoteAdapter(this)
        myAdapter.noteList
        lstNote.adapter = myAdapter

        //load notes
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

    private fun setUser() {
        userDAO.getUser(mAuth.uid.toString())
            .addOnSuccessListener {


                Log.d(TAG, "THAT USER => ${it.toObject(User::class.java)!!.name}")
                val user = it.toObject(User::class.java)!!
                binding.txtUser.text = "Usuario: ${user.name}"
                binding.txtEmail.text = "Email: ${user.email}"

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error get document => ${exception.message}")
            }




    }


    fun setNotes() {
        noteDAO.getNotes(mAuth.uid.toString())
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