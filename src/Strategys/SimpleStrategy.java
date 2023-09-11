package Strategys;

import java.util.Date;
import DataManipulation.MarketData;

/**
 *
 * @author vitor
 */
public class SimpleStrategy implements Strategy {

    private State state;
    private double yesterday_price;
    private int position = 0;
    private double capital;
    private double slippage;
    private int volumeNegociado;
    private int total_days;
    private int num_days = 0;
    private boolean finish;

    public SimpleStrategy(double capital, double slippage, int volumeNegociado, int total_days) {
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
    }

    @Override
    public void Start(Date init) {
        //System.out.println("Start [SimpleStrategy]" + init.toString());
    }

    @Override
    public void Update(MarketData day) {
        if (yesterday_price != 0) {

            if (day.getClose() > yesterday_price && (this.state != State.SHORT)) {
                this.Venda(day);
            }

            if (day.getClose() < yesterday_price && (this.state != State.LONG)) {
                this.Compra(day);
            }
        }
        
        this.yesterday_price = day.getClose();
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
            
            System.out.println("Capital Estimado: " + capitalEstimado + " no estado: " + this.state);
        }
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

    public void Venda(MarketData update) {
        double volumeReal = this.volumeNegociado;
        boolean finish = this.total_days != this.num_days;
        if (this.state != State.FLAT && finish) {
            volumeReal = 2 * this.volumeNegociado;
        }
        this.capital = this.capital + (update.getClose() - this.slippage) * volumeReal;
        this.state = State.SHORT;
    }

    public void ApresentaCapital() {
        String resultado = String.format("%.2f", this.capital);
        System.out.println("Estrategia Simples: " + resultado);
    }

}
