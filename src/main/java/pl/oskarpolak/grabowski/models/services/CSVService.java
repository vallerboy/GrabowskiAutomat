package pl.oskarpolak.grabowski.models.services;

import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import pl.oskarpolak.grabowski.Config;
import pl.oskarpolak.grabowski.models.CsvInfo;

import java.io.*;
import java.util.List;

@Service
public class CSVService {

    public void generateCSV(List<CsvInfo> data) {
        FileWriter fileWriter = null;
        BufferedWriter out = null;
        File file = new File("file.csv");
        try {
             if(!file.exists()){
                 file.createNewFile();
             }

             fileWriter = new FileWriter(file);
             out = new BufferedWriter(fileWriter);


             ICsvBeanWriter csvWriter = new CsvBeanWriter(out, CsvPreference.EXCEL_PREFERENCE);
             csvWriter.writeHeader(Config.CSV_HEADERS);


            for (CsvInfo aBook : data) {
                csvWriter.write(aBook, Config.CSV_HEADERS);
            }
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
