package com.example.app_notes_securty_as.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.app_notes_securty_as.databinding.FragmentLoginBinding
import com.example.app_notes_securty_as.ui.home.HomeActivity

class LoginFragment : Fragment() {

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


        binding.btnLogin.setOnClickListener {
            val homeIntent = Intent(context, HomeActivity::class.java)
            startActivity(homeIntent)
        }



        return binding.root
    }


}