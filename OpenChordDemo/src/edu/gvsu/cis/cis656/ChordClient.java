package edu.gvsu.cis.cis656;
/**
 * Some sample OpenChord code.  See OpenChord manual and javadocs for more detail.
 * @author Jonathan Engelsma
 */

import java.net.InetAddress;
import java.net.MalformedURLException;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.*;
import de.uniba.wiai.lspi.chord.service.impl.*;
import edu.gvsu.cis.cis656.client.RegistrationInfo;

import java.util.*;
import java.io.Serializable;

public class ChordClient {

	Chord chord;
	
    public ChordClient(String masterHost, boolean master)
    {
        // Step 1: Load the Chord properties files.
        PropertiesLoader.loadPropertyFile();
		try {
			if(master) {
				this.createNetwork(masterHost);

			} else {
				this.joinNetwork(masterHost);				
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
    
	public void createNetwork(String host)
	{
		System.out.println(">>>>>>>>Creating Chord network on [" + host + "]");
		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
		URL localURL = null;
		try {
			localURL = new URL( protocol + "://" + host + ":8080/");
		} catch ( MalformedURLException e){
			throw new RuntimeException (e);
		}
		
		this.chord = new ChordImpl();
	
		try {
			this.chord.create( localURL );
			
			
		} catch ( ServiceException e) {
			throw new RuntimeException (" Could not create DHT!", e);
		}
		
	}
	
	public void joinNetwork(String host)
	
	{
		System.out.println(">>>>>>>>Joining Chord network on [" + host + "]");		
		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);		
		URL localURL = null;
		try {
            int randomPort = (int) (Math.random() * 5535) + 60000;
            localURL = new URL(protocol + "://" + InetAddress.getLocalHost().getHostAddress() + ":" + randomPort + "/");
		} catch ( MalformedURLException e){
			throw new RuntimeException (e);
		} catch ( Exception ex) {
			throw new RuntimeException (ex);
		}
		
		URL bootstrapURL = null;
		try {
			bootstrapURL = new URL( protocol + "://" + host + ":8080/");	
		} catch ( MalformedURLException e){
			throw new RuntimeException (e);
		}
		this.chord = new ChordImpl();
		try {
			this.chord.join( localURL , bootstrapURL );
		} catch ( ServiceException e) {
			throw new RuntimeException (" Could not join DHT!", e);
		}		
					
	}

	public void insertResource(String key, Serializable resource){
    	RegistrationInfo reg = this.retrieveResource(key);
    	if (reg != null){
    		this.removeResource(key, reg);
		}
        try {
            StringKey myKey = new StringKey(key);
            this.chord.insert(myKey, resource);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    public void removeResource(String key, RegistrationInfo resource){
        try {
            StringKey myKey = new StringKey(key);
            this.chord.remove(myKey, resource);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    public RegistrationInfo retrieveResource(String key){
        RegistrationInfo resource = null;
        try {
            StringKey myKey = new StringKey(key);
            // notice that we get a set of vals back... but if we make sure
            // only one item is inserted per key, we'll only get one item in our set.
            Set<Serializable> vals = this.chord.retrieve(myKey);
            Iterator<Serializable> it = vals.iterator();
            while(it.hasNext()) {
                resource = (RegistrationInfo) it.next();
            }
        }  catch (ServiceException e) {
            e.printStackTrace();
        }
        return resource;
    }


}
