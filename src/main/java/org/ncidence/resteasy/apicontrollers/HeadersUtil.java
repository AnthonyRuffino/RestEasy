package org.ncidence.resteasy.apicontrollers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.ncidence.resteasy.exceptions.HttpRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class HeadersUtil {

	public static void checkIfMatch(HttpHeaders requestHeaders, String etag) throws HttpRequestException {
		if (requestHeaders.getIfMatch() == null || requestHeaders.getIfMatch().isEmpty()) {
			return;
		}

		for (String ifMatch : requestHeaders.getIfMatch()) {
			if (ifMatch == null) {
				continue;
			}
			if (ifMatch.equals("*") || ifMatch.equals(etag)) {
				return;
			}
		}

		throw new HttpRequestException("No match", HttpStatus.PRECONDITION_FAILED);
	}

	public static void checkIfUnModifiedSInce(HttpHeaders requestHeaders, Date lastModified)
			throws HttpRequestException {
		if (!requestHeaders.containsKey(HttpHeaders.IF_UNMODIFIED_SINCE)) {
			return;
		}
		Instant instant = lastModified.toInstant();
		Instant instantTruncated = instant.truncatedTo( ChronoUnit.SECONDS );
		long epochLastModified = instantTruncated.getEpochSecond();
		if (epochLastModified > requestHeaders.getIfUnmodifiedSince()) {
			throw new HttpRequestException("Modified: " + epochLastModified + ">" + requestHeaders.getIfUnmodifiedSince(), HttpStatus.PRECONDITION_FAILED);
		}
	}
}
