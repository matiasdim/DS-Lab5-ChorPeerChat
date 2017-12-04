package edu.gvsu.cis.cis656.client;

import com.sun.org.apache.regexp.internal.RE;
import edu.gvsu.cis.cis656.ChordClient;

public class ConnectionHandler implements PresenceService {

    // Chrod handler
    ChordClient chordCLient;

    public ConnectionHandler(String host, Boolean isMaster){
        chordCLient = new ChordClient(host, isMaster);
    }

    @Override
    public void register(RegistrationInfo reg) throws Exception {
        this.chordCLient.insertResource(reg.getUserName(), reg);
    }

    @Override
    public RegistrationInfo lookup(String name) throws Exception {
        RegistrationInfo resource = this.chordCLient.retrieveResource(name);
        if (resource != null){
            return resource;
        }
        return null;
    }

    @Override
    public void unregister(String userName) throws Exception {
        RegistrationInfo resource = this.chordCLient.retrieveResource(userName);
        if (resource != null){
            this.chordCLient.removeResource(userName, resource);
        }
    }

    @Override
    public void setStatus(String userName, boolean status) throws Exception {
        RegistrationInfo resource = this.chordCLient.retrieveResource(userName);
        if (resource != null){
            this.chordCLient.insertResource(resource.getUserName(), status);
        }
    }

    @Override
    public RegistrationInfo[] listRegisteredUsers() throws Exception {
        return new RegistrationInfo[0];
    }
}
