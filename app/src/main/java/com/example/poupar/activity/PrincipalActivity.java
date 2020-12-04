package com.example.poupar.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.poupar.activity.ContaActivity;
import com.example.poupar.activity.DespesaActivity;
import com.example.poupar.activity.ReceitaActivity;
import com.example.poupar.MainActivity;
import com.example.poupar.adapter.AdapterLancamento;
import com.example.poupar.config.ConfiguracaoFirebase;
import com.example.poupar.helper.Base64Custom;
import com.example.poupar.helper.DateCustom;
import com.example.poupar.model.Lancamento;
import com.example.poupar.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poupar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {
    private MaterialCalendarView calendarView;
    private TextView textSaudacao, textSaldo;
    private FirebaseAuth autenticacao = FirebaseAuth.getInstance();
    private String idUsuario =
            autenticacao.getCurrentUser().getEmail().replace("@", ".").replace(".", "");
    private DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();
    private Double despesaTotal=0.0;
    private Double receitaTotal=0.0;
    private Double resumoUsuario=0.0;
    private DatabaseReference usuarioRef;
    private ValueEventListener valueEventListenerUsuario;
    private ValueEventListener valueEventListenerlancamentos;

    private AdapterLancamento adapterLancamento;
    private List<Lancamento> lancamentos = new ArrayList();
    private Lancamento lancamento;
    private RecyclerView recyclerView;
    private DatabaseReference lancamentoRef;
    private String mesAnoSelecionado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lancamentos");
        textSaudacao = findViewById(R.id.textSaudacao);
        textSaldo = findViewById(R.id.textSaldo);
        calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.recyclerLancamentos);

        configuraCalendarView();

        //Configurar adapter
        adapterLancamento = new AdapterLancamento(lancamentos, this);

        //Configurar RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterLancamento);
        recyclerView.scrollToPosition(0);

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    //excluir cardviews atraves do deslize
    public void swipe(){
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags,  swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                excluirLancamento(viewHolder);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
    }

    public void excluirLancamento(final RecyclerView.ViewHolder viewHolder){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        //Configura AlertDialog
        alertDialog.setTitle("Excluir Lançamento da Conta");
        alertDialog.setMessage("Você tem certeza que deseja excluir este lançamento?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int position = viewHolder.getAdapterPosition();
                lancamento = lancamentos.get(position);

                //String emailUsuario = autenticacao.getCurrentUser().getEmail();
                //String idUsuario = Base64Custom.codificarBase64((emailUsuario));
                lancamentoRef =
                        firebaseRef.child("lancamento")
                                .child(idUsuario)
                                .child(mesAnoSelecionado);
                lancamentoRef.child(lancamento.getKey()).removeValue();
                adapterLancamento.notifyItemRemoved(position);
                atualizarSaldo();

                Toast.makeText(PrincipalActivity.this, "Lancamento excluído", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(PrincipalActivity.this, "Cancelado", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public void atualizarSaldo(){
        //String emailUsuario = autenticacao.getCurrentUser().getEmail();
        //String idUsuario = Base64Custom.codificarBase64((emailUsuario));

        lancamentoRef = firebaseRef.child("lancamento")
                .child(idUsuario)
                .child(mesAnoSelecionado);

        if(lancamento.getTipo().equals("r")){
            receitaTotal = receitaTotal - lancamento.getValor();
            usuarioRef.child("receitaTotal").setValue(receitaTotal);
        }

        if(lancamento.getTipo().equals("d")){
            despesaTotal = despesaTotal - lancamento.getValor();
            usuarioRef.child("despesaTotal").setValue(despesaTotal);
        }
    }

    public void recuperarLancamentos(int month){
        //String emailUsuario = autenticacao.getCurrentUser().getEmail();
        //String idUsuario = Base64Custom.codificarBase64((emailUsuario));

        //aqui

        FirebaseDatabase.getInstance().
                getReference("usuarios").child(idUsuario).
                child("lancamentos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lancamentos.clear();
                for(DataSnapshot dados: snapshot.getChildren()){
                    Lancamento lancamento = dados.getValue(Lancamento.class);
                    lancamento.setKey(dados.getKey());
                    lancamentos.add(lancamento);
                }

                adapterLancamento.notifyDataSetChanged();
                recyclerView.scrollToPosition(lancamentos.size() - 1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PrincipalActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        /*lancamentoRef = firebaseRef.child("lancamento")
                .child(idUsuario)
                .child(mesAnoSelecionado);

        valueEventListenerlancamentos = lancamentoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lancamentos.clear();

                for(DataSnapshot dados: dataSnapshot.getChildren()){
                    Lancamento lancamento = dados.getValue(Lancamento.class);
                    lancamento.setKey(dados.getKey());
                    lancamentos.add(lancamento);
                }

                adapterLancamento.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }

    public void recuperarResumo(){

//        String emailUsuario = autenticacao.getCurrentUser().getEmail();
//        String idUsuario = Base64Custom.codificarBase64((emailUsuario));
        //usuarioRef = firebaseRef.child("usuarios").child(idUsuario).child("usuario");

        Log.i("Evento", "evento foi adicionado");
       FirebaseDatabase.getInstance().
               getReference("usuarios").
               child(idUsuario).
               addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                despesaTotal = usuario.getDespesaTotal();
                receitaTotal = usuario.getReceitaTotal();
                resumoUsuario = receitaTotal - despesaTotal;

                //DecimalFormat decimalFormat = new DecimalFormat("0.##"); //caso seja zero, nao sera exibido
                //String resultadoFormatado = decimalFormat.format(resumoUsuario);
                NumberFormat format = NumberFormat.getCurrencyInstance();
                textSaudacao.setText("Olá, seja bem-vindo(a) " + usuario.getNome());
                textSaldo.setText(format.format(resumoUsuario));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("Evento", "Erro ao apresentar valor do saldo total");
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        recuperarResumo();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Evento", "evento foi removido");
        //aqui
        //usuarioRef.removeEventListener(valueEventListenerUsuario);
        //lancamentoRef.removeEventListener(valueEventListenerUsuario);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.menuSair :
                autenticacao.signOut();
                startActivity(new Intent(this, LoginActivity.class)); //pode voltar pra mainactivity
                finish();
                break;
            case R.id.contaUsuario :
                startActivity(new Intent(this, ContaActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void adicionarDespesa(View view){
        startActivity(new Intent(this, DespesaActivity.class));
    }

    public void adicionarReceita(View view){
        startActivity(new Intent(this, ReceitaActivity.class));
    }

    public void configuraCalendarView(){

        CharSequence meses[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        calendarView.setTitleMonths(meses);

        CalendarDay dataAtual = calendarView.getCurrentDate();
        final String mesSelecionado = String.format("%02d", (dataAtual.getMonth() + 1));
        mesAnoSelecionado = String.valueOf( mesSelecionado + "" + dataAtual.getYear());

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                final String mesSelecionado = String.format("%02d", (date.getMonth() + 1));
                mesAnoSelecionado = String.valueOf(mesSelecionado + "" + date.getYear());

                //lancamentoRef.removeEventListener(valueEventListenerlancamentos);
                recuperarLancamentos(date.getMonth() + 1);
            }
        });
    }
}