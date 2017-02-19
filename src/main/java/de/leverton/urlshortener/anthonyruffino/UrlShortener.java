package de.leverton.urlshortener.anthonyruffino;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class UrlShortener {
	private Map<Long,String> shortenedUrlsMap;
	
	public static final String Alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";
    public static final int Base = Alphabet.length();
    
    private static final AtomicLong counter = new AtomicLong();

	public UrlShortener() {
		super();
		shortenedUrlsMap = new HashMap<>();
	}
	
	
	public String add(String url){
		long intId = counter.incrementAndGet();
		String id = encodeId(intId);
		shortenedUrlsMap.put(intId, url);
		return id;
	}
	
	public String remove(String id){
		long longId = decodeId(id);
		return shortenedUrlsMap.remove(longId);
	}
	
	public String update(String id, String newUrl){
		if(shortenedUrlsMap.containsKey(id)){
			long longId = decodeId(id);
			return shortenedUrlsMap.put(longId, newUrl);
		}
		return null;
	}
	
	public String get(String id){
		long longId = decodeId(id);
		return shortenedUrlsMap.get(longId);
	}
	
	
	
	private String encodeId(Long i)
    {
        if (i == 0){
        	return Alphabet.charAt(0) + "";
        }

        String shortenedId = "";

        while (i > 0)
        {  
        	shortenedId += Alphabet.charAt(i.intValue() % Base);
            i = i / Base;
        }

        return new StringBuilder(shortenedId).reverse().toString();
    }

    private Long decodeId(String shortenedId)
    {
        int i = 0;

        for(Character c : shortenedId.toCharArray())
        {
            i = (i * Base) + Alphabet.indexOf(c);
        }

        return new Long(i);
    }
}
