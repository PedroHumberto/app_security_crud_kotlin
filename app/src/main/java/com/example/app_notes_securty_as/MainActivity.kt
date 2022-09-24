package com.example.app_notes_securty_as

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


//Tela para listagem das anotações, apresentando data e título em cada linha.
//Tela para cadastro e visualização da anotação, com data, título, foto e texto.
//A tela de listagem deve mostrar os dados do usuário logado e permitir o logout.
//As informações de localização serão acrescentadas automaticamente no início do texto, apenas na inclusão.
//As permissões para acesso ao GPS serão solicitadas em tempo de execução.
//Acesso à câmera com Intent do sistema.
//Serão gerados dois arquivos internos para cada anotação: um para o texto e outro para a foto.
//O nome dos arquivos será no formato TÍTULO(DATA), com as extensões txt e fig.
//O conteúdo dos arquivos será criptografado.
//O Banner será exibido na tela de listagem, junto a um botão para desbloqueio da versão Premium.
//O botão e o Banner ficarão invisíveis após a compra do produto gerenciado que dá direito à versão Premium.

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}