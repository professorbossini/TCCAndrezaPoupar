package com.example.poupar.helper;

import java.text.SimpleDateFormat;

public class DateCustom {

    public static String dataAtual(){
        long date = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy");
        String dataString = simpleDateFormat.format(date);
        return dataString;
    }

    public static String mesAnoDataSelecionada(String data){
        String retornoData[] = data.split("/"); //quebra da string
        String dia = retornoData[0];
        String mes = retornoData[1];
        String ano = retornoData[2];

        String mesAno = mes + ano;
        return mesAno;
    }
}