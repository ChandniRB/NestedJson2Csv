package nestedjson2csv;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import com.opencsv.CSVWriter;
import org.apache.commons.lang3.ArrayUtils;

public class NestedJson2Csv {

    public static void main(String[] args) throws Exception {
        InputStream fis = new FileInputStream(Constants.JSON_FILE);
        FileWriter outputfile = new FileWriter(Constants.CSV_FILE);

        JsonReader reader = Json.createReader(fis);
        CSVWriter writer = new CSVWriter(outputfile);

        JsonArray jsonArray = reader.readArray();
        reader.close();
        
        
        String column0;

        for (JsonValue jsonValue : jsonArray) {
            JsonObject jsonObject = jsonValue.asJsonObject();

            column0 = jsonObject.getString(Constants.ROOT_ELEMENT);
            String[] csvRow= {column0};
            if(jsonObject.containsKey("distinctValues"))
            {
                JsonArray column2 =jsonObject.getJsonObject("distinctValues").getJsonArray("buckets");
                convertDistinctValuesToCSV(writer,csvRow,column2);
                //csvRow = ArrayUtils.addAll(csvRow,data);
            }
            else {
                int column2 = jsonObject.getInt(Constants.COUNT_FIELD);
                String[] data={String.valueOf(column2)};
                csvRow = ArrayUtils.addAll(csvRow, data);
                writer.writeNext(csvRow);
            }
        }
        writer.close();
    }

    static void convertDistinctValuesToCSV(CSVWriter writer ,String[] csvRow,JsonArray jsonArray) {
          
        String[] row = csvRow;

        for (JsonValue distinctValue : jsonArray) {
            JsonObject jsonObject = distinctValue.asJsonObject();
            String[] data={distinctValue.asJsonObject().getString("key")} ;
            int count = distinctValue.asJsonObject().getInt(Constants.COUNT_FIELD);
            if(jsonObject.containsKey("distinctValues"))
            {
                if(jsonObject.getJsonObject("distinctValues").containsKey("buckets"))
                {
                    JsonArray column2 =jsonObject.getJsonObject("distinctValues").getJsonArray("buckets");
                    convertDistinctValuesToCSV(writer,(String[]) ArrayUtils.addAll(row,data),column2);
                }
                else if(jsonObject.getJsonObject("distinctValues").containsKey("distinctValues") && jsonObject.getJsonObject("distinctValues").getJsonObject("distinctValues").containsKey("buckets"))
                {
                    JsonArray column2 =jsonObject.getJsonObject("distinctValues").getJsonObject("distinctValues").getJsonArray("buckets");
                    convertDistinctValuesToCSV(writer,(String[]) ArrayUtils.addAll(row,data),column2);

                }
                
                
            }
            else{
                csvRow=(String[]) ArrayUtils.addAll(row,data);
                String[] rowToWrite = new String[ csvRow.length+1];
                for(int i=0;i<csvRow.length;i++)
                    rowToWrite[i]=csvRow[i];
                rowToWrite[csvRow.length] = String.valueOf(count);
                //System.out.println(rowToWrite);
                writer.writeNext(rowToWrite);
                
            }
        }
    }

}