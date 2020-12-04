package com.example.poupar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.poupar.R;
import com.example.poupar.model.Lancamento;

import java.util.List;

public class AdapterLancamento extends RecyclerView.Adapter<AdapterLancamento.MyViewHolder> {

    List<Lancamento> lancamentos;
    Context context;

    public AdapterLancamento(List<Lancamento> lancamentos, Context context) {
        this.lancamentos = lancamentos;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_lancamento, parent, false);
        return new MyViewHolder(itemLista);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Lancamento lancamento = lancamentos.get(position);

        holder.titulo.setText(lancamento.getDescricao());
        holder.valor.setText(String.valueOf(lancamento.getValor()));
        holder.categoria.setText(lancamento.getCategoria());
        holder.valor.setTextColor(context.getResources().getColor(R.color.colorPrimaryAccentReceita));

        if (lancamento.getTipo().equals("d")) {
            holder.valor.setTextColor(context.getResources().getColor(R.color.colorPrimaryAccentDespesa));
            holder.valor.setText("-" + lancamento.getValor());
        }
    }

    @Override
    public int getItemCount() {
        return lancamentos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titulo, valor, categoria;

        public MyViewHolder(View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.textAdapterTitulo);
            valor = itemView.findViewById(R.id.textAdapterValor);
            categoria = itemView.findViewById(R.id.textAdapterCategoria);
        }

    }

}