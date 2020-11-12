import enums.PaymentType;
import exception.ClientAlreadyExistsException;
import exception.ClientAlreadyInactiveException;
import exception.ClientNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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


    @Test
    public void updateClientAccountIdTestNoClientExpected404ResponseStatus(){
        String clientId = "12345";
        String newClientId = "54321";
        CarShopController carShopController = new CarShopController(clientService, carService, paymentService);

        when(clientService.updateClientAccountId(clientId, newClientId)).thenThrow(ClientNotFoundException.class);

        Response response = carShopController.updateClientAccountId(clientId, newClientId);

        assertEquals(404,response.getStatus());
        assertNull(response.getClient());
    }

    @Test
    public void updateClientAccountIdTestWithClientExpected200ResponseAndClient(){
        String clientId = "12345";
        String newClientId = "54321";
        Client client = new Client(clientId,true,null);
        CarShopController carShopController = new CarShopController(clientService, carService, paymentService);

        when(clientService.updateClientAccountId(clientId,newClientId)).thenReturn(client);

        Response response = carShopController.updateClientAccountId(clientId, newClientId);

        assertEquals(200,response.getStatus());
        assertEquals(clientId, response.getClient().getId());
        assertTrue(response.getClient().isActive());
        assertNull(response.getClient().getCar());
    }

    @Test
    public void registerClientCarWithCoClientExpected404ResponseStatus(){
        String clientId = "12345";
        String carMake = "FastestCars";
        String carColor = "Red";
        String carPlate = "12345";

        CarShopController carShopController = new CarShopController(clientService, carService, paymentService);

        when(carService.registerClientCar(clientId, carMake,carColor,carPlate)).thenThrow(ClientNotFoundException.class);

        Response response = carShopController.registerClientCar(clientId, carMake,carColor,carPlate);

        assertEquals(404,response.getStatus());
        assertNull(response.getClient());
    }

    @Test
    public void registerClientCarWithClientExpected200ResponseStatusAndClient(){
        String clientId = "12345";
        String carMake = "FastestCars";
        String carColor = "Red";
        String carPlate = "12345";
        Car car = new Car();
        car.setMake(carMake);
        car.setColor(carColor);
        Client client = new Client(clientId, true, car);

        CarShopController carShopController = new CarShopController(clientService, carService, paymentService);

        when(carService.registerClientCar(clientId, carMake, carColor, carPlate)).thenReturn(client);

        Response response = carShopController.registerClientCar(clientId, carMake, carColor, carPlate);

        assertEquals(200, response.getStatus());
        assertEquals(clientId, response.getClient().getId());
        assertEquals(carMake, response.getClient().getCar().getMake());
        assertEquals(carColor, response.getClient().getCar().getColor());
        assertTrue(response.getClient().isActive());
    }

    @Test
    public void processPaymentTestWithCarPaymentExpected201ResponseStatusAndOneCallCarServiceSavepaymentMethod(){
        PaymentType paymentType = PaymentType.CAR_PAYMENT;
        Payment payment = new Payment();
        payment.setType(paymentType);
        CarShopController carShopController = new CarShopController(clientService, carService, paymentService);

        Response response = carShopController.processPayment(payment);
        assertEquals(201,response.getStatus());
        verify(carService,only()).savePayment(payment);
    }

    @Test
    public void processPaymentTestWithCarPaymentExpected201ResponseStatusAndOneCallPaymentServiceSavepaymentMethod(){
        PaymentType paymentType = PaymentType.REGISTRATION_PAYMENT;
        Payment payment = new Payment();
        payment.setType(paymentType);
        CarShopController carShopController = new CarShopController(clientService, carService, paymentService);

        Response response = carShopController.processPayment(payment);
        assertEquals(201,response.getStatus());
        verify(paymentService,only()).savePayment(payment);
    }
}
