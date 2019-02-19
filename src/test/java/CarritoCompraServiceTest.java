import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.runners.MethodSorters;
import static org.mockito.Mockito.*;

import java.rmi.AccessException;

import org.junit.*;
import org.junit.rules.ExpectedException;

import com.everis.bootcamp.tallerjunit.Articulo;
import com.everis.bootcamp.tallerjunit.BaseDeDatosService;
import com.everis.bootcamp.tallerjunit.CarritoCompraService;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CarritoCompraServiceTest {
	
	private static CarritoCompraService service;
	
	BaseDeDatosService mock;
	
	@Before
	public void before() {
		service = new CarritoCompraService();
		mock = mock(BaseDeDatosService.class);
		service.setBbddService(mock);
	}
	
	@Test
	public void testAddArticulo() {
		System.out.println("Probando addArticulo():");
		assertTrue("La lista no tiene elementos", service.getArticulos().isEmpty());
		service.addArticulo(new Articulo("Articulo 1", 10d));
		assertFalse(service.getArticulos().isEmpty());
	}
	
	@Test
	public void testLimpiarCesta() {
		System.out.println("Probando limpiarCesta():");

		assertTrue("La lista no tiene elementos", service.getArticulos().isEmpty());
		service.addArticulo(new Articulo("Articulo 1", 10d));
		service.limpiarCesta();
		assertTrue("La lista no está vacía", service.getArticulos().isEmpty());
	}
	
	@Test
	public void testTotalPrice() {
		System.out.println("Probando totalPrice");
		service.addArticulo(new Articulo("Articulo 1", 10d));
		service.addArticulo(new Articulo("Articulo 2", 20d));
		service.addArticulo(new Articulo("Articulo 3", 30d));
		service.addArticulo(new Articulo("Articulo 4", 40d));
		assertEquals(service.totalPrice(), new Double(100));
	}
	
    @Test
	public void testCalculaDescuento() {
		System.out.println("Probando calculaDescuento");
		assertEquals(CarritoCompraService.calculadorDescuento(100, 10d), new Double(90));
	}
    
    @Test
    public void testEj1() {
    	System.out.println("Probando findArticulo ej1");
		when(mock.findArticuloById(1)).thenReturn(new Articulo("Camiseta", 10.0));
		Double result = service.aplicarDescuento(1, 12.0);
		System.out.println("El precio tras el descuento es: " + result);
		Assert.assertNotNull(result);
		Assert.assertEquals(new Double(8.8), result);
		verify(mock, times(1)).findArticuloById(eq(1));
    }

    @Test (expected = Exception.class)
    public void testEj2() {
    	System.out.println("Probando findArticulo ej2");
    	when(mock.findArticuloById(0)).thenThrow(new Exception());
    	doThrow(new Exception()).when(mock).findArticuloById(0);
		Double result = service.aplicarDescuento(0, 12.0);
		System.out.println("El precio tras el descuento es: " + result);
    }
    
    @Test
    public void testReto() {
    	Articulo articulo = new Articulo("Camiseta", 10.0);
    	System.out.println("Probando reto");
    	// Definimos el comportamiento del mock (stubbing)
    	when(mock.insertArticulo(eq(articulo))).thenReturn(new Integer(9));
    	// Insertamos el artículo al carrito
    	Integer result = service.insertar(articulo);
    	// Comprobamos que el método de la BBDD funciona bien
    	Assert.assertEquals(new Integer(9), result);
    	// Comprobamos que el carrito posee el articulo insertado
    	Assert.assertTrue(service.getArticulos().contains(articulo));
    	// Comprobamos que se ha llamado al método de insertar del mock al menos una vez
    	verify(mock, atLeast(1)).insertArticulo(articulo);
    }
}
