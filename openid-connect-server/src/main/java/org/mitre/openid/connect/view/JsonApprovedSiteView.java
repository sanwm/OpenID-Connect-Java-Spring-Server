/**
 * 
 */
package org.mitre.openid.connect.view;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.view.AbstractView;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author jricher
 *
 */
@Component("jsonApprovedSiteView")
public class JsonApprovedSiteView extends AbstractView {

	private static Logger logger = LoggerFactory.getLogger(JsonApprovedSiteView.class);

	private Gson gson = new GsonBuilder()
	    .setExclusionStrategies(new ExclusionStrategy() {
	
	        public boolean shouldSkipField(FieldAttributes f) {
	        	
	        	if (f.getDeclaringClass() == OAuth2AccessTokenEntity.class) {
	        		if (f.getName().equals("id")) {
	        			return false;
	        		}
	        		return true;
	        	}
	        	
	            return false;
	        }
	
	        public boolean shouldSkipClass(Class<?> clazz) {
	            // skip the JPA binding wrapper
	            if (clazz.equals(BeanPropertyBindingResult.class)) {
	                return true;
	            }
	            return false;
	        }
	
	    })
	    .serializeNulls()
	    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
	    .create();

	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {

		response.setContentType("application/json");

        
		HttpStatus code = (HttpStatus) model.get("code");
		if (code == null) {
			code = HttpStatus.OK; // default to 200
		}
		
		response.setStatus(code.value());
		
		try {
			
			Writer out = response.getWriter();
			Object obj = model.get("entity");
	        gson.toJson(obj, out);
	        
		} catch (IOException e) {
			
			//TODO: Error Handling
			logger.error("IOException in JsonEntityView.java: ", e);
			
		}
    }

}
