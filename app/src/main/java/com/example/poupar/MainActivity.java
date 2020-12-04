package com.example.poupar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.poupar.activity.CadastroActivity;
import com.example.poupar.activity.LoginActivity;
import com.example.poupar.activity.PrincipalActivity;
import com.example.poupar.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class MainActivity extends IntroActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main); //nao utilizamos mais o activity_main, mas os slides

        verificarUsuarioLogado();

        setButtonBackVisible(false);
        setButtonNextVisible(false);

        /*addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_1)
                .build()
        );

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_2)
                .build()
        );

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_3)
                .build()
        );

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_4)
                .canGoForward(false) //avancar slide e canGoBackward impede ou n de voltar
                .build()
        );*/

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_cadastro)
                .canGoForward(false) //avancar slide e canGoBackward impede ou n de voltar
                .build()
        );


        /*addSlide(new SimpleSlide.Builder()
                .title("Titulo")
                .description("Descricao")
                .image(R.drawable.um)
                .background(android.R.color.holo_orange_light)
                .build()
        );

        addSlide(new SimpleSlide.Builder()
                .title("Titulo 2")
                .description("Descricao 2")
                .image(R.drawable.dois)
                .background(android.R.color.holo_orange_light)
                .build()
        );

        addSlide(new SimpleSlide.Builder()
                .title("Titulo 3")
                .description("Descricao 3")
                .image(R.drawable.tres)
                .background(android.R.color.holo_orange_light)
                .build()
        );

        addSlide(new SimpleSlide.Builder()
                .title("Titulo 4")
                .description("Descricao 4")
                .image(R.drawable.quatro)
                .background(android.R.color.holo_orange_light)
                .build()
        )*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        //verificarUsuarioLogado();
    }

    public void btEntrar(View view){
        startActivity(new Intent(this, LoginActivity.class));
    }
    public void btCadastrar(View view){
        startActivity(new Intent(this, CadastroActivity.class));
    }

    public void verificarUsuarioLogado() {
        autenticacao = FirebaseAuth.getInstance();
        if (autenticacao.getCurrentUser() != null) {
            abrirTelaPrincipal();
        }
    }

    public void abrirTelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }
}