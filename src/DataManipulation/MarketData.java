package DataManipulation;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author vitor
 */
public class MarketData {

    private DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy"); 
    
    private Date date;    
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private int volume;

    public MarketData(String date, Double open,Double high, Double low, Double close, int volume){
        
        try {
            this.date = formatter.parse(date.split(" ")[0]);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
         
        this.high = high;
        this.open = open;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public void printData(){
        System.out.println(date);
        System.out.println(high);
        System.out.println(open);
        System.out.println(low);
        System.out.println(close);        
        System.out.println(volume);
    }   

    public DateFormat getFormatter() {
        return formatter;
    }

    public Date getDate() {
        return date;
    }

    public Double getOpen() {
        return open;
    }

    public Double getHigh() {
        return high;
    }

    public Double getLow() {
        return low;
    }

    public Double getClose() {
        return close;
    }

    public int getVolume() {
        return volume;
    }
    
    
}
