/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Strategys;

import DataManipulation.MarketData;
import java.util.Date;

/**
 *
 * @author vitor
 */
public class MediaMovel implements Strategy {

    private double lDay; // Media movel longa / Preciso do valor atual e a do dia anterior / Media Pricinpal
    private double cDay; // Media movel curta  / Preciso do valor atual e a do dia anterior
    private double mmLonga[];  // vetor media movel longa
    private double mmCurta[]; // vetor media movel curta

    private double capital;
    private double yesterday_price;
    private double slippage;

    private State state;
    private int position = 0;
    private int volumeNegociado;
    private int total_days;
    private int num_days = 0;
    private boolean finish;

    // Valor da long é 20 e a da curta é 7
    // Se a longa for maior que a curta no dia atual
    // e a longa anterior for menor do que a curta anterior
    // e o estado for diferente de SHORT, eu VENDO
    // Se a longa for menor que a curta no dia atual
    // e a longa anterior for maior do que a curta anterior
    // e o estado for diferente de LONG, eu COMPRO
    
    public MediaMovel(double capital, double slippage, int volumeNegociado, int total_days) {
        this.capital = capital;
        this.slippage = slippage;
        this.volumeNegociado = volumeNegociado;
        this.total_days = total_days;
    }

    @Override
    public void Create() {
        this.state = State.FLAT;
        this.yesterday_price = 0;
        this.position = 0;
        iniciaMedia();
    }

    @Override
    public void Start(Date init) {
        //System.out.println("Start [MediaMovel]" + init.toString());
    }

    @Override
    public void Update(MarketData update) {
        atualizaMedia(update);
        
        if (this.num_days >= this.mmLonga.length) {
            calculaMedia();
            if ((this.lDay >= this.cDay) && (this.state != State.SHORT)) {
                Venda(update);
            } else if ((this.lDay <= this.cDay) && (this.state != State.LONG)) {
                Compra(update);
            }
        }

        this.num_days += 1;
    }

    @Override
    public void Finish(MarketData finish) {
        if (num_days == total_days) {
            double volumeReal = this.volumeNegociado;
            if (this.state == State.LONG) {
                this.capital = this.capital + (finish.getClose() - this.slippage) * volumeReal;
                this.state = State.FLAT;
            }
            if (this.state == State.SHORT) {
                this.capital = this.capital - (finish.getClose() + this.slippage) * volumeReal;
                this.state = State.FLAT;
            }
        } else {
            // Estimativa de retorno
            double capitalEstimado = this.capital;
            
            if(this.state == State.LONG){
                capitalEstimado = this.capital + finish.getClose();
            }
            
            if(this.state == State.SHORT){
                capitalEstimado = this.capital - finish.getClose();
            }
            
            System.out.println("Capital Estimado: " + capitalEstimado + " no estado: " + this.state + " no dia: " + this.num_days);
            System.out.println("Media Movel Curta: " + this.cDay + " Media Movel Longa: " + this.lDay);
            System.out.println("");
        }
    }

    @Override
    public void Venda(MarketData update) {
        double volumeReal = this.volumeNegociado;
        boolean finish = this.total_days != this.num_days;
        if (this.state != State.FLAT && finish) {
            volumeReal = 2 * this.volumeNegociado;
        }
        this.capital = this.capital + (update.getClose() - this.slippage) * volumeReal;
        this.state = State.SHORT;
    }

    @Override
    public void Compra(MarketData update) {
        double volumeReal = this.volumeNegociado;
        boolean finish = this.total_days != this.num_days;

        if (this.state != State.FLAT && finish) {
            volumeReal = 2 * this.volumeNegociado;
        }
        this.capital = this.capital - (update.getClose() + this.slippage) * volumeReal;
        this.state = State.LONG;
    }

    private void iniciaMedia() {
        this.mmLonga = new double[20];
        this.mmCurta = new double[7];

        for (int i = 0; i < this.mmLonga.length; i++) {
            this.mmLonga[i] = Double.MAX_VALUE;
        }

        for (int j = 0; j < this.mmCurta.length; j++) {
            this.mmCurta[j] = Double.MAX_VALUE;
        }
    }

    private void calculaMedia() {
        double calcLonga = 0, calcCurta = 0;
        for (double longa : this.mmLonga) {
            calcLonga += longa;
        }
        this.lDay = calcLonga / this.mmLonga.length;

        for (double curta : this.mmCurta) {
            calcCurta += curta;
        }
        this.cDay = calcCurta / this.mmCurta.length;
    }

    private void atualizaMedia(MarketData update) {
        int positionL = this.num_days, positionC = this.num_days;

        while (positionL >= this.mmLonga.length) {
            positionL = Math.abs(positionL - this.mmLonga.length);
        }

        while (positionC >= this.mmCurta.length) {
            positionC = Math.abs(positionC - this.mmCurta.length);
        }

        this.mmLonga[positionL] = update.getClose();
        this.mmCurta[positionC] = update.getClose();
    }

    public void ApresentaCapital() {
        String resultado = String.format("%.2f", this.capital);
        System.out.println("Estrategia Media Movel: " + resultado);
    }
}
