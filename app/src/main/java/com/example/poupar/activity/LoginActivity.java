package com.example.poupar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.poupar.R;
import com.example.poupar.config.ConfiguracaoFirebase;
import com.example.poupar.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    private EditText campoEmail, campoSenha;
    private TextView campoSemCadastro;
    private Button botaoEntrar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(null);
        //getSupportActionBar().setTitle("Poupar");

        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
        botaoEntrar = findViewById(R.id.buttonEntrar);
        campoSemCadastro = findViewById(R.id.textNaoCadastro);
        campoSemCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cadastro = new Intent(LoginActivity.this, CadastroActivity.class);
                startActivity(cadastro);
            }
        });
        botaoEntrar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String textEmail = campoEmail.getText().toString();
                String textSenha = campoSenha.getText().toString();

                if( !textEmail.isEmpty()){
                    if( !textSenha.isEmpty()){
                        usuario = new Usuario();
                        usuario.setEmail( textEmail );
                        usuario.setSenha( textSenha );
                        validarLogin();
                    }else{
                        Toast.makeText(LoginActivity.this, "Preencha a senha!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "Preencha o email!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void validarLogin(){
        autenticacao = FirebaseAuth.getInstance();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if ( task.isSuccessful() ){

                    abrirTelaPrincipal();

                }else{
                    String excecao = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        excecao = "Usuario não está cadastrado";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "E-mail e senha não correspondem a um usuário cadastrado";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void abrirTelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }
}