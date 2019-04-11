package dominio;

import dominio.repositorio.RepositorioProducto;
import sun.security.jca.GetInstance;
import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioGarantiaExtendida;
import java.util.Calendar;
import java.util.Date;

/**Logica del vendedor
 * @author davida.acevedo
 * @version 1.0.0
 * @since 11/04/2019
 */
public class Vendedor {
	
    public static final String EL_PRODUCTO_TIENE_GARANTIA = "Este producto ya cuenta con una garantia extendida";
    public static final String PRODUCTO_NO_EXISTE = "Este producto no se encuentra en nuestra base de datos";
    public static final String PRODUCTO_SIN_GARANTIA = "Este producto no cuenta con garantia extendida"; 
    
    //Variables para acceder a la Base de datos
    private RepositorioProducto repositorioProducto;
    private RepositorioGarantiaExtendida repositorioGarantia;
    
    /**Constructor clase vendedor
     * @param repositorioProducto Se encarga de administrar la tabla de productos
     * @param repositorioGarantia Se encarga de administrar la tabla de garantias extendidas
     */
    public Vendedor(RepositorioProducto repositorioProducto, RepositorioGarantiaExtendida repositorioGarantia) {
        this.repositorioProducto = repositorioProducto;
        this.repositorioGarantia = repositorioGarantia;

    }

    /**
     * @param codigo Sirve para generar la garantia extendida del producto
     * @param cliente Nombre del cliente para asociarle la garantia extendida del producto
     * @throws GarantiaExtendidaException Maneja las excepciones al crear la garantia extendida
     */
    public void generarGarantia(String codigo, String cliente) throws GarantiaExtendidaException {
    		
    		if(!productoExiste(codigo))
    			throw new GarantiaExtendidaException(PRODUCTO_NO_EXISTE);
    		if(NumerarVocales(codigo)>=3)
    			throw new GarantiaExtendidaException(PRODUCTO_SIN_GARANTIA);
    		if(tieneGarantia(codigo))
    			throw new GarantiaExtendidaException(EL_PRODUCTO_TIENE_GARANTIA);
    		
    		//Already exists the product, can be store in the variable product
    		Producto producto = repositorioProducto.obtenerPorCodigo(codigo);
    		//variables used to the Date of warranty
    		int diasGarantia;
    		double precioProducto = producto.getPrecio();
    		double precioGarantia;
    		//Calculating the price and date of extended warranty
    		if(precioProducto > 500000) {
    			precioGarantia = precioProducto*0.2;
    			diasGarantia = 200;
    			
    		}else {
    			precioGarantia = precioProducto*0.1;
    			diasGarantia = 100;
    		}
    		
    		//Starting date of warranty and his method to calculate the end
    		Date inicioGarantia = Calendar.getInstance().getTime();
    		Date finGarantia = fechaExactaGarantia(inicioGarantia, diasGarantia);
    		
    		//Creating object of extended warranty
    		GarantiaExtendida nuevaGarantia = new GarantiaExtendida(producto, inicioGarantia, finGarantia, precioGarantia, cliente);
    		//Adding the object to our Database
    		repositorioGarantia.agregar(nuevaGarantia);
    }

    /**
     * Check if the code of the product has at least three vocals
     * @param codigo 
     * @return the number of vocals in the code
     */
    public int NumerarVocales(String codigo){
    	int vocales=0,i=0;
    	char posicion = '\u0000';
    	while(i < codigo.length()){
    		posicion = codigo.toLowerCase().charAt(i);
    		switch(posicion){
    		case 'a':
    			vocales++;
    			i++;
    			break;
    		case 'e':
    			vocales ++;
    			i++;
    			break;
    		case 'i':
    			vocales++;
    			i++;
    			break;
    		case 'o':
    			vocales++;
    			i++;
    			break;
    		case 'u':
    			vocales++;
    			i++;
    			break;
    		default:
    			i++;
    			// no es una vocal
    		}
    	}
    	return vocales;
    }
    
    
    /**
     * Estimate the exact date of the warranty
     * @param fechaInicial Starting date of the warranty
     * @param dias Number of days, can be 200 or 100
     * @return the final date of the warranty 
     */
    public Date fechaExactaGarantia(Date fechaInicial, int dias) {
    	int i=0;
    	Calendar calendario = Calendar.getInstance();
    	calendario.setTime(fechaInicial);
    	
    	while(i<dias) {
    		if(calendario.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
    			i++;
    		calendario.add(Calendar.DAY_OF_MONTH,1);
    	}
    	
    	if(calendario.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
    		calendario.add(Calendar.DAY_OF_MONTH,1);
    	
    	return calendario.getTime();
    }
    
    /** Check if the product exists in the Database
     * @param codigo Is used to search the product
     * @return Is True if the product exists otherwise shall be False
     */
    public boolean productoExiste(String codigo) {
    	if(repositorioProducto.obtenerPorCodigo(codigo) != null) {
    		return true;
    	}
        return false;
    }
    
    /** Check if the Product has previous warranties in the Database
     * @param codigo Is used to search the product
     * @return True if the product & the warranty exists in the Database otherwise shall be F
     */
    public boolean tieneGarantia(String codigo) {
    	Producto producto = repositorioProducto.obtenerPorCodigo(codigo);
    	if(producto != null && repositorioGarantia.obtener(codigo) != null) {
    		return true;
    	}
        return false;
    }

}
