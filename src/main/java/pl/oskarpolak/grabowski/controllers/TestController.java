package pl.oskarpolak.grabowski.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.oskarpolak.grabowski.models.CsvInfo;
import pl.oskarpolak.grabowski.models.SoapDriver;
import pl.oskarpolak.grabowski.models.services.CSVService;

import java.util.List;

@Controller
public class TestController {

    @Autowired
    SoapDriver soapDriver;

    @Autowired
    CSVService csvService;

    @GetMapping("/")
    @ResponseBody
    public String index(){
        soapDriver.loginIn();
        List<CsvInfo> infoList = soapDriver.startParsing();
        csvService.generateCSV(infoList);

        return infoList.get(0).toString();
    }

}
