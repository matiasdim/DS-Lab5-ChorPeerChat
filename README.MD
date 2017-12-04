# Client application
Lab 04 - Ditributed systems CIS656 - Client application.

## Based on the original source code: [gae-restlet-client-example](https://github.com/jengelsma/gae-restlet-client-example)
A chat httpClient that connects with [an APi](https://github.com/matiasdim/DS-Lab4-WebServices) to perform several tasks 

## Tree
```bash
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── edu
│   │   │   │   ├── gvsu
│   │   │   │   │   ├── restpi
│   │   │   │   │   │   ├── client
│   │   │   │   │   │   │   ├── App.java
│   │   │   │   │   │   │   ├── PresenceService.java
│   │   │   │   │   │   │   ├── RESTClient.java
│   │   │   │   │   │   │   ├── RegistrationInfo.java
│   │   │   │   │   │   │   ├── TextListener.java
```
* **App.java:** Main class, handles all de "UI" related things, that on the scope of this project is de command line.
* **PresenceService.java:** THe provided presenceService interface to implement the needed API calls.
* **RESTClient.java:** The RESTClient class that handles both the api calls and the implementation of the presence service.
* **RegistrationInfo.java:** Provided Registration info class that acts as a model class (User model)
* **TextListener.java:** The class that handles the message reception for the client.


## Clarification:
In order to preserve the RESTful characteristics on the API, I needed to do a little change on the Presnce Service Interface. 
The only resource update the client peforms is "setStatus". The interface only received for that methods the name of the
user and the new status value. THe server for the PUT call following RESTful practices always wait for the complete resource
so that's why I changed the "setStatus" method and now it receives the resource to be updated and the new status value.

Other options I had where:
* Just wait for the statu sparameter on the PUT function (and then violate REST good practices)
* Crate a PATCH method instead of a PUT method on the server andthis way avoid the server waiting for all the resource on this call. 
 
