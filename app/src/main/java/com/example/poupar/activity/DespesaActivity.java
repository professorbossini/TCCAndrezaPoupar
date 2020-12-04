package com.example.poupar.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.poupar.R;
import com.example.poupar.config.ConfiguracaoFirebase;
import com.example.poupar.helper.Base64Custom;
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

import java.util.Objects;

public class DespesaActivity extends AppCompatActivity {
    private TextInputEditText campoData;
    private TextInputEditText campoCategoria;
    private TextInputEditText campoDescricao;
    private EditText campoValor;
    private Lancamento lancamento;
    private FirebaseAuth autenticacao = FirebaseAuth.getInstance();
    private String idUsuario =
            autenticacao.getCurrentUser().getEmail().replace("@", ".").replace(".", "");
    private DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();
    private Double despesaTotal = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesa);
        //getSupportActionBar().setTitle("Despesa");

        campoValor = findViewById(R.id.editValor);
        campoData = findViewById(R.id.editData);
        campoCategoria = findViewById(R.id.editCategoria);
        campoDescricao = findViewById(R.id.editDescricao);

        //Preenche o campo data com a data atual
        campoData.setText(DateCustom.dataAtual());
    }

    public void incluirDespesa(View view){

        if(validarCamposDespesa()){
            lancamento = new Lancamento();
            String data = campoData.getText().toString();
            Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());
            lancamento.setValor(valorRecuperado);
            lancamento.setCategoria(campoCategoria.getText().toString());
            lancamento.setDescricao(campoDescricao.getText().toString());
            lancamento.setData(data);
            lancamento.setTipo("d");

            //atualizarDespesa(despesaAtualizada);

            //lancamento.incluirLancamento(data);
            inserirLancamentoEAtualizarDespesa(lancamento);


        }
    }

    private void inserirLancamentoEAtualizarDespesa(Lancamento lancamento){
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
                                        child("despesaTotal").setValue(u.getDespesaTotal() + lancamento.getValor());
                                finish();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });

    }


    public Boolean validarCamposDespesa(){
        String textoValor = campoValor.getText().toString();
        String textoData = campoData.getText().toString();
        String textoCategoria = campoCategoria.getText().toString();
        String textoDescricao = campoDescricao.getText().toString();

        if( !textoValor.isEmpty() ){
            if( !textoData.isEmpty() ){
                if( !textoCategoria.isEmpty() ){
                    if( !textoDescricao.isEmpty() ){
                        return true;
                    }else{
                        Toast.makeText(DespesaActivity.this, "Valor não foi preenchido", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else{
                    Toast.makeText(DespesaActivity.this, "Valor não foi preenchido", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(DespesaActivity.this, "Valor não foi preenchido", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(DespesaActivity.this, "Valor não foi preenchido", Toast.LENGTH_SHORT).show();
            return false;
        }

    }
    public void recuperarDespesaTotal(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64((emailUsuario));
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                if (usuario != null) {
                    despesaTotal = usuario.getDespesaTotal();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void incluirLancamento(Lancamento lancamento){
        FirebaseAuth autenticacao = FirebaseAuth.getInstance();
        String idUsuario = Base64Custom.codificarBase64(Objects.requireNonNull(Objects.requireNonNull(autenticacao.getCurrentUser()).getEmail()));
        String mesAno = DateCustom.mesAnoDataSelecionada(lancamento.getData());

        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference("usuario");
        //aqui
        //firebase.child("lancamento")
                firebase.child(idUsuario)
                .child(mesAno)
                .push()
                .setValue(lancamento);
    }

    public void atualizarDespesa(Double despesa){

        String emailUsuario = Objects.requireNonNull(autenticacao.getCurrentUser()).getEmail();
        String idUsuario = Base64Custom.codificarBase64((emailUsuario));
        DatabaseReference usuarioRef = firebaseRef.child("usuario").child(idUsuario);

        usuarioRef.child("despesaTotal").setValue(despesa);
    }
}