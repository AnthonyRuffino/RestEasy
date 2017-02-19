package org.ncidence.resteasy.apicontrollers;

import org.ncidence.resteasy.exceptions.HttpRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class HeadersUtil {
	
	public static Boolean checkIfMatch(HttpHeaders requestHeaders, String etag) throws HttpRequestException{
		if(requestHeaders.getIfMatch() == null || requestHeaders.getIfMatch().isEmpty()){
			return null;
		}
		
		for(String ifMatch : requestHeaders.getIfMatch()){
			if(ifMatch == null){
				continue;
			}
			if(ifMatch.equals("*") || ifMatch.equals(etag)){
				return true;
			}
		}
		
		throw new HttpRequestException("No match", HttpStatus.PRECONDITION_FAILED);
	}
}
