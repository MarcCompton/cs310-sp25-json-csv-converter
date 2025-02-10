package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.util.*;
import java.io.*;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
   
     @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        String result = "{}"; // default return value; replace later!
        
        try {
            // Csv Parser
            CSVReader reader = new CSVReader(new StringReader(csvString));
            
            // Read the Csv data
            List<String[]> full = reader.readAll();
            
            // Extract column headings
            String[] ColHeadings = full.get(0);
            
            // Initialize Json structures
            JsonObject CompleteJson = new JsonObject();
            JsonArray ProdNums = new JsonArray();
            JsonArray Data = new JsonArray();

            // Process each row (skips the header row)
            for (int i = 1; i < full.size(); i++) {
                String[] row = full.get(i);
                
                // Add ProdNum to array
                ProdNums.add(row[0]);
                
                // Create a Json array for row data
                JsonArray rowData = new JsonArray();
                
                for (int x = 1; x < row.length; x++) {
                String currentColumn = ColHeadings[x];
                String cellValue = row[x];

                // Check for "Season" or "Episode" columns
                if ("Season".equals(currentColumn) || "Episode".equals(currentColumn)) {
                    rowData.add(Integer.parseInt(cellValue)); // Convert to integer
                } else {
                    rowData.add(cellValue); // Add as is
                }
            }
                // Add the row data to array
                Data.add(rowData);
            }
            // Add the Json structures to the complete Json
            CompleteJson.put("ProdNums", ProdNums);
            CompleteJson.put("ColHeadings", ColHeadings);
            CompleteJson.put("Data", Data);
            
            // Convert the Json object to a string
            result = Jsoner.serialize(CompleteJson);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result.trim();
        
    }

    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        String result = ""; // default return value; replace later!
        
        try {
        // Parse Json string into JsonObject
        JsonObject CompleteJson = Jsoner.deserialize(jsonString, new JsonObject());
        // Extract arrays
        JsonArray ColHeadings = (JsonArray) CompleteJson.get("ColHeadings");
        JsonArray ProdNums = (JsonArray) CompleteJson.get("ProdNums");
        JsonArray Data = (JsonArray) CompleteJson.get("Data");
        // Create a Csv writer
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer);
        // Write the header row
        String[] headerRow = new String[ColHeadings.size()];
        for (int i = 0; i < ColHeadings.size(); i++) {
            headerRow[i] = ColHeadings.getString(i);
        }

        csvWriter.writeNext(headerRow);
        // Iterate through each row in Data
        for (int i = 0; i < Data.size(); i++) {
            JsonArray rowData = (JsonArray) Data.get(i);
            String[] row = new String[ColHeadings.size()];

            // Assign the Product Number
            row[0] = ProdNums.getString(i);

            // Populate the rest of the row
            int colIndex = 1;
            for (Object value : rowData) {
                String colName = ColHeadings.get(colIndex).toString();
                String cellValue = value.toString();

                // Format "Episode" with two digits, otherwise store as-is
                row[colIndex] = colName.equals("Episode") 
                                ? String.format("%02d", Integer.parseInt(cellValue)) 
                                : cellValue;
                
                // Move to the next column
                colIndex++; 
            }

            // Write the row to the Csv
            csvWriter.writeNext(row);
        }
        
        
        // Get the Csv string
        result = writer.toString();
    }
    catch (Exception e) {
        e.printStackTrace();
    }
    return result.trim();
    }
}