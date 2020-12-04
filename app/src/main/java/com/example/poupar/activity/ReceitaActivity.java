package com.example.poupar.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.poupar.R;
import com.example.poupar.helper.DateCustom;
import com.example.poupar.model.Lancamento;
import com.example.poupar.model.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReceitaActivity extends AppCompatActivity {
    private TextInputEditText campoData;
    private TextInputEditText campoCategoria;
    private TextInputEditText campoDescricao;
    private EditText campoValor;
    private Lancamento lancamento;
    private DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth autenticacao = FirebaseAuth.getInstance();
    private String idUsuario =
            autenticacao.getCurrentUser().getEmail().replace("@", ".").replace(".", "");
    private Double receitaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receita);
        //getSupportActionBar().setTitle("Receita");

        campoValor = findViewById(R.id.editValor);
        campoData = findViewById(R.id.editData);
        campoCategoria = findViewById(R.id.editCategoria);
        campoDescricao = findViewById(R.id.editDescricao);

        //Preenche o campo data com a data atual
        campoData.setText(DateCustom.dataAtual());
    }

    public void incluirReceita(View view){

        if(validarCamposReceita()){
            Lancamento lancamento = new Lancamento();
            String data = campoData.getText().toString();
            double valorRecuperado = Double.parseDouble(campoValor.getText().toString());
            lancamento.setValor(valorRecuperado);
            lancamento.setCategoria(campoCategoria.getText().toString());
            lancamento.setDescricao(campoDescricao.getText().toString());
            lancamento.setData(data);
            lancamento.setTipo("r");
            inserirLancamentoEAtualizarReceita(lancamento);
            //atualizarReceita(lancamento);
            //double receitaAtualizada = receitaTotal + valorRecuperado;
            //atualizarReceita(receitaAtualizada);

            //lancamento.incluirLancamento(data);
            //finish();
        }
    }

    private void inserirLancamentoEAtualizarReceita(Lancamento lancamento){
        //String emailUsuario = autenticacao.getCurrentUser().getEmail();
        //String idUsuario = Base64Custom.codificarBase64((emailUsuario));
        FirebaseDatabase.getInstance().
                getReference("usuarios").
                child(idUsuario).
                child("lancamentos").
                push().setValue(lancamento).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseDatabase.getInstance().
                        getReference("usuarios").
                        child(idUsuario).
                        addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Usuario u = snapshot.getValue(Usuario.class);
                                FirebaseDatabase.getInstance().
                                        getReference("usuarios").
                                        child(idUsuario).
                                        child("receitaTotal").setValue(u.getReceitaTotal() + lancamento.getValor());
                                        finish();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });

    }

    public boolean validarCamposReceita(){
        try{
            String textoValor = campoValor.getEditableText().toString();
            String textoData = campoData.getEditableText().toString();
            String textoCategoria = campoCategoria.getEditableText().toString();
            String textoDescricao = campoDescricao.getEditableText().toString();

            if( textoValor.length() > 0 ){
                if( textoData.length() > 0 ){
                    if( textoCategoria.length() > 0 ){
                        if( textoDescricao.length() > 0 ){
                            return true;
                        }else{
                            Toast.makeText(ReceitaActivity.this, "Valor não foi preenchido", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }else{
                        Toast.makeText(ReceitaActivity.this, "Valor não foi preenchido", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else{
                    Toast.makeText(ReceitaActivity.this, "Valor não foi preenchido", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(ReceitaActivity.this, "Valor não foi preenchido", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    public void recuperarDespesaTotal(){

        //String emailUsuario = autenticacao.getCurrentUser().getEmail();
        ////String idUsuario = Base64Custom.codificarBase64((emailUsuario));
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void atualizarReceita(Double receitaAtualizada){

        //String emailUsuario = autenticacao.getCurrentUser().getEmail();
        //String idUsuario = Base64Custom.codificarBase64((emailUsuario));
        //aqui
        //        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuario").child(idUsuario);
        usuarioRef.child("receitaTotal").push().setValue(receitaAtualizada);

    }
}