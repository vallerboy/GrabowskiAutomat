package pl.oskarpolak.grabowski.models;

import com.fleetboard.data.*;
import com.fleetboard.ws.basicservice.BasicService;
import com.fleetboard.ws.basicservice.BasicService_Service;
import com.fleetboard.ws.posservice.PosService;
import com.fleetboard.ws.posservice.PosService_Service;
import com.fleetboard.ws.vehiclerequestservice.VehicleRequestService;
import com.fleetboard.ws.vehiclerequestservice.VehicleRequestServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import pl.oskarpolak.grabowski.Config;
import pl.oskarpolak.grabowski.Session;
import pl.oskarpolak.grabowski.models.services.RewriteUrlService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


@Service
public class SoapDriver {
    /*
       **Facade**
       * Driver of soap api
       * Oskar Polak Fivarto
     */
    private static final Logger logger = LoggerFactory.getLogger(SoapDriver.class);

    private static ObjectFactory objectFactory = new ObjectFactory();

    private BasicService_Service basicService = new BasicService_Service();
    private BasicService basicServiceImpl = basicService.getBasicService();

    private PosService_Service posService = new PosService_Service();
    private PosService posServiceImpl = posService.getPosService();

    private VehicleRequestService vehicleSerivce = new VehicleRequestService();
    private VehicleRequestServiceInterface vehicleSerivceImpl = vehicleSerivce.getVehicleRequestService();

    @Autowired
    private Session session;

    @Autowired
    private RewriteUrlService rewriteUrlService;

    public void loginIn() {
        Login login = new Login();
        LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUser(Config.LOGIN);
            loginRequest.setPassword(Config.PASSWWORD);
            loginRequest.setFleetname(Config.FLEET);
        login.setLoginRequest(loginRequest);

        // Make login and handle session
        LoginResponse2 responseFirst = basicServiceImpl.login(login);
        LoginResponse  reponseLast = responseFirst.getLoginResponse();

        // Got new session
        session.setSession(reponseLast.getSessionid());
        // Rewiring URL
        basicServiceImpl = rewriteUrlService.rewriteBasicServiceUrl(basicService);
        posServiceImpl = rewriteUrlService.rewritePosServiceUrl(posService);
        vehicleSerivceImpl = rewriteUrlService.rewriteVehicleServiceUrl(vehicleSerivce);

        logger.info("Login into soap service and rewrite url");
    }

    public void logout() {
        Logout logout = new Logout();
        LogoutRequest logoutRequest = new LogoutRequest();

        //sending request
        basicServiceImpl.logout(logout);
        logger.info("Logout from soap service");
    }


    public void getPositionRaport() {
        GetPosReport posReport = new GetPosReport();

        GetPosReportRequestType reportRequestType = new GetPosReportRequestType();
            TPDate date = new TPDate();
            date.setBegin("2017-09-07 07:00:00");
            date.setEnd("2017-09-07 7:05:00");
        reportRequestType.setRealVehicleTimestamp(date);
        posReport.setGetPosReportRequest(reportRequestType);

        GetPosReportResponse reportResponse = posServiceImpl.getPosReport(posReport);
        for (Msg msg : reportResponse.getGetPosReportResponse().getMsg()) {
            Header header = msg.getHeader();
            logger.info("Sent: " + header.getSentstatus());
            logger.info("Km: " + header.getPosition().getKM());
        }
    }


    private List<GetLastPositionResponseType.Positions> getLastPosition(){
        List<GetLastPositionResponseType.Positions> positionsList = new ArrayList<>();

        GetLastPosition lastPosition = new GetLastPosition();
        GetLastPositionRequestType lastPositionRequestType = new GetLastPositionRequestType();
        lastPosition.setGetLastPositionRequest(lastPositionRequestType);

        GetLastPositionResponse response = posServiceImpl.getLastPosition(lastPosition);

        for (GetLastPositionResponseType.Positions position : response.getGetLastPositionResponse().getPositions()) {
            positionsList.add(position);
        }
        return positionsList;
    }

    /**
       Return list with all vehicles in fleet
     */
    private List<Long> requestAllVehicles(){
        List<Long> longList = new ArrayList<>();
        GetLastPosition lastPosition = new GetLastPosition();
        GetLastPositionRequestType lastPositionRequestType = new GetLastPositionRequestType();
        lastPosition.setGetLastPositionRequest(lastPositionRequestType);

        GetLastPositionResponse response = posServiceImpl.getLastPosition(lastPosition);
        for (GetLastPositionResponseType.Positions positions : response.getGetLastPositionResponse().getPositions()) {
            longList.add(positions.getVehicleID());
        }
        return longList;
    }


    /**
        Request data from all vehicles in fleet
        Wait 5 seconds for store data into fleetboard servers
     */
    private void requestPosFromAllVehicles() {
        List<Long> allVehicles = requestAllVehicles();
        SendVehicleRequest sendVehicleRequest = new SendVehicleRequest();
            SendVehicleRequestType sendVehicleRequestType = new SendVehicleRequestType();
            sendVehicleRequestType.getVehicleID().addAll(allVehicles);
            sendVehicleRequestType.getRequestType().add("POSREQUEST");
            sendVehicleRequestType.getRequestType().add("STATUS_CHECK");
        sendVehicleRequest.setSendVehicleRequestRequest(sendVehicleRequestType);
        SendVehicleRequestResponse requestResponse = vehicleSerivceImpl.sendVehicleRequest(sendVehicleRequest);
    }

    /**
       Get info about all drivers and store it into list;
     */
    private GetDriverResponseType.DriverInfo getDriverInfo(Long driverIds){
        GetDriver getDriver = new GetDriver();
        GetDriverRequestType getDriverRequestType = new GetDriverRequestType();
        getDriverRequestType.getDriverNameID().add(driverIds);
        getDriver.setGetDriverRequest(getDriverRequestType);

        GetDriverResponse response = basicServiceImpl.getDriver(getDriver);
        // Cause we want only one info
        return response.getGetDriverResponse().getDriverInfo().get(0);
    }

    private VEHICLE getVehicleInfo(long vehicleId){
        GetVehicle getVehicle = new GetVehicle();
        GetVehicleRequest getVehicleRequest = new GetVehicleRequest();
        getVehicleRequest.getVehicleID().add(String.valueOf(vehicleId));
        getVehicleRequest.setServiceType(ServiceType.ALL);
        getVehicle.setGetVehicleRequest(getVehicleRequest);
        GetVehicleResponse2 response = basicServiceImpl.getVehicle(getVehicle);
        // Cause we want only one info
        return response.getGetVehicleResponse().getVEHICLE().get(0);
    }

    /**
       Method from everythings its starting :)
       Collect data and convert it into CvsInfo object
       Must have: debug dupa dupa dupa
     */
    public List<CsvInfo> startParsing() {
        List<CsvInfo> csvInfos = new ArrayList<>();

        requestPosFromAllVehicles();

        //Sleep for 5 seconds for load data
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        CsvInfo tempCsv;
        List<GetLastPositionResponseType.Positions> lastKnowPositions = getLastPosition();
        for (GetLastPositionResponseType.Positions lastKnowPosition : lastKnowPositions) {
                GetDriverResponseType.DriverInfo driverInfo = getDriverInfo(lastKnowPosition.getDriverNameID());
                VEHICLE vehicleInfo = getVehicleInfo(lastKnowPosition.getVehicleID());

            tempCsv = new CsvInfo();
            tempCsv.setDriver(driverInfo.getFirstName() + " " + driverInfo.getLastName());
            tempCsv.setCourse(lastKnowPosition.getPosition().getKM());
            tempCsv.setLatitude(lastKnowPosition.getPosition().getLat());
            tempCsv.setLongitude(lastKnowPosition.getPosition().getLong());
            tempCsv.setRegistration(vehicleInfo.getFLEETVEHICLE().getREGISTRATION());
            tempCsv.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            csvInfos.add(tempCsv);
        }

        return csvInfos;
    }
}

