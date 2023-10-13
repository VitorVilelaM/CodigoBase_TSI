package Strategys;

import DataManipulation.FileManager;
import DataManipulation.MarketData;
import Strategys.State;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author douglascastilho
 */
public class ReturnSimulation implements Strategys.Strategy {

    private State estado;
    private int posicao;
    private double slippage;
    private int volumeNegociado;
    private double capital;
    private MarketData lastMarketData;
    private HashMap<String, Prediction> predictions;

    public ReturnSimulation() {
        this.slippage = 0.0;
        this.volumeNegociado = 1;
        this.capital = 0;
    }

    public ReturnSimulation(double slippage, int volumeNegociado, double capital) {
        this.slippage = slippage;
        this.volumeNegociado = volumeNegociado;
        this.capital = capital;
    }

    @Override
    public void Start(Date date) {
    }

    @Override
    public void Update(MarketData update) {

        SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd");
        String day;
        if(update.getDate().getDate() < 10){
            day = "0" + update.getDate().getDate();
        }else{
            day = "" + update.getDate().getDate();
        }
        String month;
        if(update.getDate().getMonth()<= 8){
            month = "0" + (update.getDate().getMonth() + 1);
        }else{
            month = "" + (update.getDate().getMonth()+1);
        }
        
        String currentDate = "" + (1900+update.getDate().getYear()) + (month) + day;
//        String currentDate = sdf.format(update.getDate());
//        System.out.println(update.getDate());
//        System.out.println(currentDate);
        Prediction pred = this.predictions.get(currentDate);
        
        if (pred != null && pred.getOutput() <= 0
                && this.estado != State.SHORT) {
            this.Venda(update);
        }

        if (pred != null && pred.getOutput() > 0
                && this.estado != State.LONG) {
            this.Compra(update);
        }

        this.lastMarketData = update;
    }

    @Override
    public void Create() {

        System.out.println("Creating the strategy - Return Simulation");

        this.estado = State.FLAT;
        this.posicao = 0;
        this.predictions = new HashMap<>();

        ArrayList<String> outputFile = FileManager.stringReader("./src/ML/output.csv");
        for (int i = 1; i < outputFile.size(); i++)     {
            String line = outputFile.get(i);
            String split[] = line.split(",");
            Prediction pred = new Prediction(split[0], Double.parseDouble(split[1]));
            this.predictions.put(split[0], pred);
        }
    }

    @Override
    public void Finish(MarketData finish) {
        DecimalFormat df = new DecimalFormat("##.##");
        if (finish.getDate() == null) {
            this.finalizarExecucao(this.lastMarketData);
            //System.out.println(df.format(this.capital) + "\t" + df.format(this.lastMarketData.getClose()));
        } else {
            double capitalEstimado = this.capital;
            if (this.estado == State.LONG) {
                capitalEstimado = this.capital + this.lastMarketData.getClose();
            }
            if (this.estado == State.SHORT) {
                capitalEstimado = this.capital - this.lastMarketData.getClose();
            }
            System.out.println(df.format(capitalEstimado) + "\t" + df.format(this.lastMarketData.getClose()));
        }
    }

    @Override
    public void Venda(MarketData update) {
        double volumeReal = this.volumeNegociado;
        if (this.estado != State.FLAT) {
            volumeReal = 2 * this.volumeNegociado;
        }
        this.capital = this.capital
                + (update.getClose() - this.slippage) * volumeReal;
        this.estado = State.SHORT;
    }

    @Override
    public void Compra(MarketData update) {
        double volumeReal = this.volumeNegociado;
        if (this.estado != State.FLAT) {
            volumeReal = 2 * this.volumeNegociado;
        }
        this.capital = this.capital
                - (update.getClose() + this.slippage) * volumeReal;
        this.estado = State.LONG;
    }

    private void finalizarExecucao(MarketData update) {
        double volumeReal = this.volumeNegociado;
        if (this.estado == State.LONG) {
            this.capital = this.capital + (update.getClose() - this.slippage) * volumeReal;
            this.estado = State.FLAT;
        }
        if (this.estado == State.SHORT) {
            this.capital = this.capital - (update.getClose() + this.slippage) * volumeReal;
            this.estado = State.FLAT;
        }

    }

    private class Prediction {

        private String date;
        private double output;

        public Prediction() {

        }

        public Prediction(String date, double output) {
            this.date = date;
            this.output = output;
        }

        public String getData() {
            return date;
        }

        public void setData(String date) {
            this.date = date;
        }

        public double getOutput() {
            return output;
        }

        public void setOutput(double output) {
            this.output = output;
        }

        @Override
        public String toString() {
            return "Prediction{" + "date=" + date + ", output=" + output + '}';
        }
        
        

    }

}
