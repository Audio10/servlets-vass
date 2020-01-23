package mx.com.vass.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

/**
 * Servlet implementation class Proxies
 */
@WebServlet(description = "Servlets de facturacion", urlPatterns = { "/Proxies" })
public class Proxies extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = Logger.getLogger("mx.com.vass.servlet.Proxies");
	String theUrl ="";
	private HttpHeaders headers = new HttpHeaders();
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Proxies() {
        super();
        theUrl = Configuracion.URL;
        headers = new HttpHeaders();
        Charset utf8 = Charset.forName("UTF-8");
        MediaType mediaType = new MediaType("text", "html", utf8);
        headers.setContentType(mediaType);
        headers.set(Configuracion.API_KEY, Configuracion.KEY_VALUE);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sec = request.getParameter("sec");
		
		response.setContentType("text/html");
    	RestTemplate restTemplate = new RestTemplate();
    	
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		PrintWriter escritor = response.getWriter();
		
		try {
			LOGGER.log(Level.INFO, sec);
			ResponseEntity<String> responseEntity = restTemplate.exchange(theUrl + "evo-redirect-html?sec=" + sec, HttpMethod.GET, entity, String.class);
			escritor.println(responseEntity.getBody());
			
		} catch (HttpStatusCodeException e) {
			LOGGER.log(Level.WARNING, sec);
			escritor.println(ResponseEntity.status(e.getRawStatusCode()).headers(e.getResponseHeaders())
	                .body(e.getResponseBodyAsString()).getBody());
		}
		escritor.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sec = request.getParameter("sec");
		String PaRes = request.getParameter("PaRes");
		response.setContentType("text/html");
		RestTemplate restTemplate = new RestTemplate();
		
		EvoResponseDTO body = new EvoResponseDTO(PaRes, sec);
		Gson gson = new Gson();
		String JSON = gson.toJson(body);
		
		HttpEntity<String> entity = new HttpEntity<>(JSON,headers);
		
		PrintWriter escritor = response.getWriter();
		try {
			LOGGER.log(Level.INFO, sec);
			LOGGER.log(Level.INFO, PaRes);
			ResponseEntity<String> responseEntity = restTemplate.exchange(theUrl + "evo-authentication", HttpMethod.POST, entity, String.class);
			escritor.println(responseEntity.getBody());
			
		} catch (HttpStatusCodeException e) {
			LOGGER.log(Level.WARNING, sec);
			LOGGER.log(Level.WARNING, PaRes);
			escritor.println(ResponseEntity.status(e.getRawStatusCode()).headers(e.getResponseHeaders())
	                .body(e.getResponseBodyAsString()).getBody());
		}
		escritor.close();
	}

}
