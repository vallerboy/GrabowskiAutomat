package pl.oskarpolak.grabowski.models.services;

import com.fleetboard.ws.basicservice.BasicService;
import com.fleetboard.ws.basicservice.BasicService_Service;
import com.fleetboard.ws.posservice.PosService;
import com.fleetboard.ws.posservice.PosService_Service;
import com.fleetboard.ws.vehiclerequestservice.VehicleRequestService;
import com.fleetboard.ws.vehiclerequestservice.VehicleRequestServiceImpl;
import com.fleetboard.ws.vehiclerequestservice.VehicleRequestServiceInterface;
import com.fleetboard.ws.vehiclerequestservice.VehicleRequestServiceInterface_VehicleRequestService_Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.oskarpolak.grabowski.Config;
import pl.oskarpolak.grabowski.Session;

import javax.xml.ws.BindingProvider;

@Service
public class RewriteUrlService {

    @Autowired
    Session session;

    public BasicService rewriteBasicServiceUrl(BasicService_Service service){
        BasicService port = service.getBasicService();
        BindingProvider bindingProvider = (BindingProvider) port;
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                Config.BASE_SERVICE_URL + session.getSession());
        return port;
    }

    public PosService rewritePosServiceUrl(PosService_Service service){
        PosService port = service.getPosService();
        BindingProvider bindingProvider = (BindingProvider) port;
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                Config.POS_SERVICE_URL + session.getSession());
        return port;
    }

    public VehicleRequestServiceInterface rewriteVehicleServiceUrl(VehicleRequestService service){
        VehicleRequestServiceInterface port = service.getVehicleRequestService();
        BindingProvider bindingProvider = (BindingProvider) port;
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                Config.REQUEST_SERVIE_URL + session.getSession());
        return port;
    }
}
