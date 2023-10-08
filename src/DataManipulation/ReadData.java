package DataManipulation;



import DataManipulation.MarketData;
import DataManipulation.FileManager;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 202011020022
 */
public class ReadData {
    public static List<MarketData> run() {
        List<MarketData> dataObjects = new ArrayList<>();
        List<String> data = FileManager.stringReader("./datas/BBDC4_2.csv");

        for (String linha : data) {
            String[] dataDay = linha.split(",");
            String d = dataDay[0];
            Double o = Double.valueOf(dataDay[1]);
            Double h = Double.valueOf(dataDay[2]);
            Double l = Double.valueOf(dataDay[3]);
            Double c = Double.valueOf(dataDay[4]);
            int v = Integer.valueOf(dataDay[5]);
                        
            MarketData aux = new MarketData(d, o, h, l, c, v);  
            
            dataObjects.add(aux);
        }
        return dataObjects;
    }
}
