package edu.gvsu.cis.cis656.client;

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
    public void setStatus(RegistrationInfo reg, boolean status) throws Exception {
        RegistrationInfo resource = this.chordCLient.retrieveResource(reg.getUserName());
        if (resource != null){
            resource.setStatus(status);
            this.chordCLient.insertResource(resource.getUserName(), resource);
        }
    }

    @Override
    public RegistrationInfo[] listRegisteredUsers() throws Exception {
        return new RegistrationInfo[0];
    }
}
