/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Strategys;

import DataManipulation.FileManager;
import DataManipulation.MarketData;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author douglascastilho
 */
public class StrategyDataBase implements Strategy{

    private double precoOntem;
    private String linha;
    private ArrayList<String> arquivoCsv;
    
    public StrategyDataBase(){}
    
    @Override
    public void Create() {
        
        precoOntem = 0;
        this.arquivoCsv = new ArrayList<>();
    
    }

    @Override
    public void Start(Date date) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void Update(MarketData update) {
    
        double saidaOntem = precoOntem - update.getClose();
        linha = linha + "," + saidaOntem;
        
        if (saidaOntem > 0){
            linha = linha + ",1";
        }
        else {
            linha = linha + ",0";
        }
        
        if (precoOntem != 0){
            System.out.println(linha);
            this.arquivoCsv.add(linha);
            FileManager.writerAppend("arquivo.csv", linha + "\n");
        }
        
        linha = update.getDate() + "," +
                update.getClose() + "," +
                update.getVolume() + "," + 
                update.getHigh();
        
        this.precoOntem = update.getClose();
    }
    
    @Override
    public void Finish(MarketData finish) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void Venda(MarketData update) {
 //       throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void Compra(MarketData update) {
   //     throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
