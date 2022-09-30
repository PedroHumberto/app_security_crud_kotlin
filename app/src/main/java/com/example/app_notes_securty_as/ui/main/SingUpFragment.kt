package com.example.app_notes_securty_as.ui.main

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.example.app_notes_securty_as.R
import com.example.app_notes_securty_as.databinding.FragmentSingUpBinding
import com.example.app_notes_securty_as.domain.models.User
import com.example.app_notes_securty_as.service.UserDAO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SingUpFragment : Fragment() {


    private lateinit var _binding: FragmentSingUpBinding
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth
    private val userDAO = UserDAO()
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSingUpBinding.inflate(inflater, container, false)

        mAuth = FirebaseAuth.getInstance()

        db = Firebase.firestore

        binding.btnSingup.setOnClickListener {
            val email = binding.editEmailSingUp.text.toString()
            val password = binding.editPasswordSingUp.text.toString()
            val userName = binding.editName.text.toString()
            if (email.isEmpty() || password.isEmpty() || userName.isEmpty()) {
                Toast.makeText(context, "PREENCHA TODOS OS CAMPOS", Toast.LENGTH_LONG).show()
            } else {
                mAuth
                    .createUserWithEmailAndPassword(
                        email, password
                    )
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.w(TAG, "createUserWithEmail:success")
                            Log.d(TAG, "ID USER => ${it.result.user!!.uid}")

                            val user = User(it.result.user!!.uid, userName, email)

                            userDAO.addUser(user)

                            val loginFragment = LoginFragment()
                            var fragmentTransaction: FragmentTransaction =
                                requireActivity().supportFragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.mainContainer, loginFragment)
                                .remove(this).commit()

                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", it.exception)
                            Toast.makeText(
                                context, "CAMPO EMAIL ESTA INVALIDO",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }


        return binding.root
    }


}