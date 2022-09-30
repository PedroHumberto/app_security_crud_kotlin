package com.example.app_notes_securty_as.ui.main

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.example.app_notes_securty_as.R
import com.example.app_notes_securty_as.databinding.FragmentLoginBinding
import com.example.app_notes_securty_as.ui.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private var mUser: FirebaseUser? = null

    //Tela para login ou inclusão de usuário com autenticação no Firebase.
    //A tela de login só deve ser apresentada se o usuário ainda não estiver logado.
    private lateinit var _binding: FragmentLoginBinding
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        mAuth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            val email = binding.editEmailLogin.text.toString()
            val password = binding.editPasswordLogin.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "COLOQUE O EMAIL E SENHA", Toast.LENGTH_LONG).show()
            } else {
                mAuth.signInWithEmailAndPassword(
                    email, password
                )
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(ContentValues.TAG, "signInWithEmail:success")
                            mUser = mAuth.currentUser
                            updateUI(mUser)
                        } else {

                            Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                            binding.txtAlert.visibility = View.VISIBLE

                            updateUI(null)
                        }
                    }
            }
        }

        binding.txtSingup.setOnClickListener {

            val singUpFragment = SingUpFragment()
            var fragmentTransaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.mainContainer, singUpFragment).remove(this).commit()
        }



        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.txtAlert.visibility = View.INVISIBLE
    }

    fun updateUI(mUser: FirebaseUser?) {
        if (mUser == null) {
            //SE O LOGIN FALHAR DISPARA A MSG PARA O USUARIO
            Toast.makeText(
                context, "EMAIL OU SENHA INVALIDO",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            //CASO DÊ CERTO, ELE SERA ENVIADO PARA A TELA HOME DO APP
            Toast.makeText(
                context, "AUTENTICADO",
                Toast.LENGTH_SHORT
            ).show()
            val homeActivity = Intent(context, HomeActivity::class.java)
            startActivity(homeActivity)
        }
    }


}