import exception.ClientAlreadyInactiveException;
import exception.ClientNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientServiceTest {

    @Mock
    ClientRepository clientRepository;


    @Test
    public void disableClientAccountMakesClientInactiveAndSavesToDbWhenClientIsFoundAndIsActive(){
        String id = "12345";
        ClientService clientService = new ClientService(clientRepository);
        Client client = new Client(id,true,null);

        when(clientRepository.findClient(id)).thenReturn(client);
        doAnswer(invocation ->{
            Client c = invocation.getArgument(0);
            assertFalse(c.isActive());
            return null;
        }).when(clientRepository).saveClient(any());

        Client returnedClient = clientService.disableClientAccount(id);

        verify(clientRepository).saveClient(client);


        assertEquals(id, returnedClient.getId());
        assertFalse(returnedClient.isActive());

    }


    @Test(expected = ClientNotFoundException.class)
    public void disableClientAccountThrowsExceptionWhenClientNotFound(){
        String id = "12345";
        ClientService clientService = new ClientService(clientRepository);

        when(clientRepository.findClient(id)).thenReturn(null);

        clientService.disableClientAccount(id);
    }

    @Test(expected = ClientAlreadyInactiveException.class)
    public void disableClientAccountThrowsExceptionWhenClientAlreadyInactive(){
        String id = "12345";
        ClientService clientService = new ClientService(clientRepository);
        Client client = new Client(id,false,null);

        when(clientRepository.findClient(id)).thenReturn(client);

        clientService.disableClientAccount(id);
    }

    @Test
    public void updateClientAccountIdTestWithClientExpectedClientWithNewId(){
        String id ="12345";
        String newId = "54321";
        Client client = new Client(id,true,null);
        ClientService clientService = new ClientService(clientRepository);

        when(clientRepository.findClient(id)).thenReturn(client);

        Client returnedClient = clientService.updateClientAccountId(id,newId);

        verify(clientRepository).saveClient(client);
        verify(clientRepository).deleteClient(id);

        assertEquals(newId,returnedClient.getId());
        assertTrue(returnedClient.isActive());
        assertNull(returnedClient.getCar());
    }

    @Test(expected = ClientNotFoundException.class)
    public void updateClientAccountIdWithNoClient(){
        String id = "12345";
        String newId = "54321";
        ClientService clientService = new ClientService(clientRepository);

        when(clientRepository.findClient(id)).thenReturn(null);

        clientService.updateClientAccountId(id,newId);
    }
}
