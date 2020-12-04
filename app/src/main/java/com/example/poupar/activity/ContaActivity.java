package com.example.poupar.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.poupar.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.poupar.activity.CadastroActivity.DADOS_KEY;

public class ContaActivity extends AppCompatActivity {
    //Button botaoExcluir, botaoAlterar, botaoConsultar;
    DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();
    DadosUsuario dado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conta);

        dado = getIntent().getParcelableExtra(DADOS_KEY);

        /*botaoExcluir = findViewById(R.id.buttonExcluir);
        botaoExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ca.auxiliarExcluirUsuario();
            }
        });*/

        receberConsultaUsuario();

    }

    private void receberConsultaUsuario(){
        TextView textNomeUsuario = findViewById(R.id.textNomeUsuario);
        textNomeUsuario.setText(dado.getNome());

        TextView textEmailUsuario = findViewById(R.id.textEmailUsuario);
        textEmailUsuario.setText(dado.getEmail());

    }
}