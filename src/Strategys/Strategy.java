package Strategys;

import java.util.Date;
import DataManipulation.MarketData;

/**
 *
 * @author vitor
 */
public interface Strategy {

    public void Create();
    
    public void Start(Date init);

    public void Update(MarketData update);

    public void Finish(MarketData finish);
    
    public void Venda(MarketData update);
    
    public void Compra(MarketData update);
}
