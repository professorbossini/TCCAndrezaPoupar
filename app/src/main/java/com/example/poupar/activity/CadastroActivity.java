package com.example.poupar.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poupar.R;
import com.example.poupar.config.ConfiguracaoFirebase;
import com.example.poupar.helper.Base64Custom;
import com.example.poupar.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class CadastroActivity extends AppCompatActivity {
    private EditText campoNome, campoEmail, campoSenha;
    Button botaoCadastrar, botaoExcluir;
    FirebaseAuth autenticacao;
    DatabaseReference firebase;
    Usuario usuario;

    //Parcelable
    public static final String DADOS_KEY = "dados";
    DadosUsuario dados1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("Cadastro");
        usuario = new Usuario();

        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);

        // Criando instâncias da classe Parcelable
        dados1 = new DadosUsuario(usuario.getIdUsuario(), usuario.getNome(), usuario.getEmail(), usuario.getSenha(), usuario.getReceitaTotal(), usuario.getDespesaTotal());

        incluirUsuario();
    }

    public void incluirUsuario() {
        botaoCadastrar = findViewById(R.id.buttonCadastrar);
        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textNome = campoNome.getText().toString().trim();
                String textEmail = campoEmail.getText().toString().trim();
                String textSenha = campoSenha.getText().toString().trim();
                //Validar se os campos foram preenchidos
                if (!textNome.isEmpty()) {
                    if (!textEmail.isEmpty()) {
                        if (!textSenha.isEmpty()) {
                            firebase = ConfiguracaoFirebase.getFirebaseDatabase();
                            String aux = firebase.push().getKey();
                            usuario.setIdUsuario(aux);
                            usuario.setNome(textNome);
                            usuario.setEmail(textEmail);
                            firebase.child("usuarios").
                                    child(usuario.getEmail().replace("@", ".").replace(".", ""))
                                    .child("usuario")
                                    .setValue(usuario);
                            cadastrarUsuario();

                            Toast.makeText(CadastroActivity.this, "Usuario incluído com sucesso!", Toast.LENGTH_SHORT).show();
                            limparCamposCadastro();

                            startActivity(new Intent(CadastroActivity.this, LoginActivity.class));
                        } else {
                            Toast.makeText(CadastroActivity.this, "Por favor preencha o campo senha", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(CadastroActivity.this, "Por favor preencha o campo e-mail", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CadastroActivity.this, "Por favor preencha o campo nome", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void cadastrarUsuario(){
        String user = campoEmail.getEditableText().toString();
        String password = campoSenha.getEditableText().toString();

        //firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        autenticacao = FirebaseAuth.getInstance();
        autenticacao.createUserWithEmailAndPassword(
                user, password
        ).addOnCompleteListener(
                this, task -> {
                    if (task.isSuccessful()){
                        finish();

                    }else{
                        String excecao = "";
                        try{
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthWeakPasswordException e) {
                            excecao = "Digite uma senha com no mínimo seis caracteres";
                        }catch (FirebaseAuthInvalidCredentialsException e){
                            excecao = "Por favor, digite um e-mail válido";
                        }catch (FirebaseAuthUserCollisionException e){
                            excecao = "Esta conta já foi cadastrada";
                        }catch (Exception e){
                            excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                            e.printStackTrace();
                        }
                        Toast.makeText(CadastroActivity.this, "Erro ao cadastrar usuário", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void consultarUsuario(){
        chamadaDadosUsuario(dados1);
    }

    public void chamadaDadosUsuario(DadosUsuario dados) {
        // Intent
        Intent intent = new Intent(this, ContaActivity.class);
        intent.putExtra(DADOS_KEY, dados);
        startActivity(intent);
    }

    public void limparCamposCadastro(){
        campoNome.setText("");
        campoEmail.setText("");
        campoSenha.setText("");
    }
}