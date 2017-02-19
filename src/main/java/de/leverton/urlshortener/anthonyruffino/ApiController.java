package de.leverton.urlshortener.anthonyruffino;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class ApiController {

	UrlShortener urlShortener = new UrlShortener();

	@RequestMapping("/")
	public String index() {

		StringBuilder stringBuilder = new StringBuilder("<h1>Anthony's URL Shortener!</h1>");

		stringBuilder.append("<br/>");
		stringBuilder.append("<br/>");

		addApiActionMessage(stringBuilder, "POST /add?url=www.example.com",
				"  -result: {url:\"/www.example.com}\", id:\"\"exampleId\"");
		addApiActionMessage(stringBuilder, "PUT /update?id=exampleId,url=www.newexample.com",
				"  -result: {url:\"/exampleId}\", id:\"\"exampleId\"");
		addApiActionMessage(stringBuilder, "DELETE /delete?id=exampleId",
				"  -result: {url:\"/exampleId}\", id:\"\"exampleId\"");
		addApiActionMessage(stringBuilder, "GET /get/exampleId", "  -301 Redirect to www.example.com");

		return stringBuilder.toString();
	}

	private void addApiActionMessage(StringBuilder stringBuilder, String action, String result) {
		stringBuilder.append(action);
		stringBuilder.append("<br/>");
		stringBuilder.append(result);
		stringBuilder.append("<br/>");
		stringBuilder.append("<br/>");
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ShortenedUrlResponse add(@RequestParam(value = "url", required = false, defaultValue = "") String url) {
		
		if(url== null || url.trim().isEmpty()){
			return null;
		}
		
		if(!url.toLowerCase().startsWith("http")){
			url = "http://" + url;
		}
		
		ShortenedUrlResponse response = new ShortenedUrlResponse();
		response.setUrl(url);
		response.setId(urlShortener.add(url));
		
		return response;
	}

	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	public ShortenedUrlResponse update(@RequestParam(value = "id", required = false, defaultValue = "") String id, @RequestParam(value = "newUrl", required = false, defaultValue = "") String newUrl) {
		
		ShortenedUrlResponse response = new ShortenedUrlResponse();
		response.setUrl(newUrl);
		response.setId(id);

		String oldValue = urlShortener.update(id, newUrl);

		if (oldValue == null) {
			return null;
		}

		return response;
	}

	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public ShortenedUrlResponse delete(@RequestParam(value = "id", required = false, defaultValue = "") String id) {
		ShortenedUrlResponse response = new ShortenedUrlResponse();
		response.setId(id);

		String oldValue = urlShortener.remove(id);
		response.setUrl(oldValue);

		return response;
	}

	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
	public ModelAndView get(@PathVariable(value = "id") String id) {

		String url = urlShortener.get(id);

		if (url == null) {
			return null;
		}

		return new ModelAndView("redirect:" + url);
	}
	
}
