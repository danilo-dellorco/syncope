package org.apache.syncope.core.provisioning.api.mytests.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.syncope.core.provisioning.api.utils.RealmUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


@RunWith(Parameterized.class)
public class GetEffectiveTest {
	Set<String> allowedRealms;
	String requestedRealm;
	
	public static final String VALID_ALLOWED = "va";
	public static final String NULL_ALLOWED = "na";
	
	public static final String VALID_REQUESTED = "requestedRealm";
	public static final String NULL_REQUESTED = null;
	
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Parameters
	public static List<TestParameters> getData() {
		List<TestParameters> testInputs = new ArrayList<>();
		
		testInputs.add(new TestParameters(VALID_ALLOWED,VALID_REQUESTED,null));
		testInputs.add(new TestParameters(VALID_ALLOWED,NULL_REQUESTED,NullPointerException.class));
		testInputs.add(new TestParameters(NULL_ALLOWED,VALID_REQUESTED,null));
		
		return testInputs;
	}
	
    public GetEffectiveTest(TestParameters input) {
    	this.allowedRealms = input.getAllowedRealms();
    	this.requestedRealm = input.getRequestedRealm();
    	if (input.getExpectedException()!=null) {
    		this.expectedException.expect(input.getExpectedException());
    	}
	}


	@Test
    public void test() {
        Set<String> effective = RealmUtils.getEffective(allowedRealms, requestedRealm);
        if (allowedRealms == null) {
        	int expectedSize = 0;
        	assertEquals(expectedSize, effective.size());
        }
        else {
        	assertEquals(allowedRealms, effective);
        }
	}
	
	private static class TestParameters {
		Set<String> allowedRealms;
		String requestedRealm;
		Class<? extends Exception> expectedException;
		
		public TestParameters(String allowedRealmsType, String requestedRealm, Class<? extends Exception> expectedException) {
			this.allowedRealms = new HashSet<>();
			this.requestedRealm = requestedRealm;
			this.expectedException = expectedException;
			
			if (allowedRealmsType.equals(VALID_ALLOWED)) {
		        allowedRealms.add("realmA");
		        allowedRealms.add("realmB");
		        allowedRealms.add(requestedRealm);
			}
			else {
				allowedRealms = null;
			}
		}

		public Set<String> getAllowedRealms() {
			return allowedRealms;
		}

		public String getRequestedRealm() {
			return requestedRealm;
		}
		
		public Class<? extends Exception> getExpectedException() {
			return expectedException;
		}
	}
	
}

