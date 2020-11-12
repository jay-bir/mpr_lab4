import exception.ClientAlreadyExistsException;
import exception.ClientAlreadyInactiveException;
import exception.ClientNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CarShopControllerTest {

    @Mock
    ClientService clientService;

    @Mock
    CarService carService;
    @Mock
    PaymentService paymentService;

    @Test
    public void createNewClientWithCarShouldReturn200AndClientWithCar(){
        String clientId = "12345";
        Car car = new Car();
        car.setMake("mercedes");
        Client client = new Client(clientId, true, null);

        CarShopController carShopController = new CarShopController(clientService, carService, paymentService);

        when(clientService.createNewClient(clientId)).thenReturn(client);

        Response response =carShopController.createNewClient(clientId, car);

        assertEquals(200, response.getStatus());
        assertEquals(clientId, response.getClient().getId());
        assertTrue(response.getClient().isActive());
        assertEquals("mercedes",response.getClient().getCar().getMake());
    }

    @Test
    public void createNewClientWithoutCarShouldReturn400WhenClientAlreadyExists(){
        String clientId = "12345";
        Car car = new Car();
        car.setMake("mercedes");

        CarShopController carShopController = new CarShopController(clientService, carService, paymentService);

        when(clientService.createNewClient(clientId)).thenThrow(ClientAlreadyExistsException.class);

        Response response =carShopController.createNewClient(clientId, car);

        assertEquals(400, response.getStatus());
        assertNull(response.getClient());
    }

    @Test
    public void disableClientAccountWithNoClientShouldReturn404(){
        String clientId = "12345";
        CarShopController carShopController = new CarShopController(clientService, carService,paymentService);

        when(clientService.disableClientAccount(clientId)).thenThrow(ClientNotFoundException.class);

        Response response = carShopController.disableClientAccount(clientId);

        assertEquals(404,response.getStatus());
        assertNull(response.getClient());
    }

    @Test
    public void disableClientAccountWithClientInactiveShouldReturn400(){
        String clientId = "12345";
        CarShopController carShopController = new CarShopController(clientService, carService, paymentService);

        when(clientService.disableClientAccount(clientId)).thenThrow(ClientAlreadyInactiveException.class);

        Response response = carShopController.disableClientAccount(clientId);

        assertEquals(400,response.getStatus());
        assertNull(response.getClient());
    }

    @Test
    public void disableClientAccountWithClientActiveShouldReturn200AndClient(){
        String clientId = "12345";
        CarShopController carShopController = new CarShopController(clientService, carService, paymentService);
        Client client = new Client(clientId,true,null);

        when(clientService.disableClientAccount(clientId)).thenReturn(client);

        Response response = carShopController.disableClientAccount(clientId);

        assertEquals(200,response.getStatus());
        assertEquals(clientId, response.getClient().getId());
        assertTrue(response.getClient().isActive());
        assertNull(response.getClient().getCar());
    }
}
