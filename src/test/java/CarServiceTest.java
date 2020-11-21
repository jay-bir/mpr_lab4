import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CarServiceTest {

    @Mock
    CarCreator carCreator;

    @Mock
    ClientRepository clientRepository;

    @Mock
    PaymentRepository paymentRepository;

    @Test
    public void registerClientCarTestShouldSetCarToClient(){
        String clientId = "12345";
        Client client = new Client("12345",true, null);

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);

        String carMake = "FastestCars";
        String carColor = "Red";
        String carPlate = "54321";
        Car car = new Car();
        car.setMake(carMake);
        car.setColor(carColor);
        car.setPlate(carPlate);

        when(carCreator.createCar(carMake,carColor,carPlate)).thenReturn(car);
        when(clientRepository.findClient(clientId)).thenReturn(client);

        CarService carService = new CarService(carCreator,clientRepository,paymentRepository);
        Client returnedClient = carService.registerClientCar(clientId,carMake,carColor,carPlate);

        verify(clientRepository).saveClient(captor.capture());

        assertEquals(clientId,returnedClient.getId());
        assertTrue(returnedClient.isActive());
        assertEquals(carMake,returnedClient.getCar().getMake());
        assertEquals(carColor,returnedClient.getCar().getColor());
        assertEquals(carPlate,returnedClient.getCar().getPlate());

        assertEquals(clientId,captor.getValue().getId());
        assertEquals(carMake,captor.getValue().getCar().getMake());
        assertEquals(carColor,captor.getValue().getCar().getColor());
        assertEquals(carPlate,captor.getValue().getCar().getPlate());

    }
}
