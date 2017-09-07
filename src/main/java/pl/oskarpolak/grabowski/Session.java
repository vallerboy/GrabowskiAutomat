package pl.oskarpolak.grabowski;

import org.springframework.stereotype.Service;

@Service
public class Session {
    private String session;

    public Session() {
        session = null;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getSession() {
        return session == null ? "" :  ";jsessionid=" + session;
    }
}
