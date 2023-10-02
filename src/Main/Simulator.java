package Main;

import DataManipulation.ReadData;
import DataManipulation.MarketData;
import Strategys.MediaMovel;
import java.util.List;
import java.util.ArrayList;

import Strategys.DataBase;
import Strategys.Strategy;
import Strategys.SimpleStrategy;

/**
 *
 * @author vitor
 */
public class Simulator {

    public static void main(String[] args) {
        List<MarketData> marketUpdate = ReadData.run();
        List<Strategy> strategies = new ArrayList<>();
        
        MediaMovel mediaMovel = new MediaMovel(0, 0.0, 1, marketUpdate.size());
        SimpleStrategy simpleStrategy = new SimpleStrategy(0, 0.0, 1, marketUpdate.size());
        DataBase dataBase = new DataBase(marketUpdate.size());
        //strategies.add(simpleStrategy);
        strategies.add(mediaMovel);    
        strategies.add(dataBase);
        
        for (Strategy st : strategies) {
            st.Create();
        }
        for (MarketData day : marketUpdate) {

            for (Strategy st : strategies) {
                st.Start(day.getDate());
            }

            for (Strategy st : strategies) {
                st.Update(day);
            }

            for (Strategy st : strategies) {
                st.Finish(day);
            }
        }

        //simpleStrategy.ApresentaCapital();
        //mediaMovel.ApresentaCapital();
    }

}
