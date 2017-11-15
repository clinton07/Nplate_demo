package com.framework.utility;

import java.util.Random;
import java.util.UUID;

import org.testng.annotations.Test;

public class RandomEmailId {
	
//**********Method 1************************	
	/* private static String randomEmail() {
	        return "random-" + UUID.randomUUID().toString() + "@example.com";
	    }
	 
	 @Test
	 public void Email(){
		  
		 System.out.println(randomEmail());
		 System.out.println(randomEmail()); 
	 }*/
	 
	 public String generateRandomChars(String candidateChars, int length) {
		    StringBuilder sb = new StringBuilder();
		    Random random = new Random();
		    for (int i = 0; i < length; i++) {
		        sb.append(candidateChars.charAt(random.nextInt(candidateChars.length())));
		    }

		    return sb.toString();
		}
	public void Email(){
		
		//17 represents number of characters in random number
		String ramdomemailid = generateRandomChars("abcdefghijklmnopqrstuvwxyz1234567890", 10);
		String testEmailId = "test"+ramdomemailid+"@mailinator.com";
		System.out.println(testEmailId);
	}

}
