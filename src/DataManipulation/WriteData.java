package DataManipulation;

/**
 *
 * @author vitor
 */
public class WriteData {
    
    public WriteData(){
        FileManager.writer("./datas/Data.csv", "");
    }
    
    public void Write(String line){
        FileManager.writerAppend("./datas/Data.csv", line);
        FileManager.writerAppend("./datas/Data.csv", "\n");
    }
}
