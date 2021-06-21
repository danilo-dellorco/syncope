package org.apache.syncope.core.provisioning.api.mytests.testcases;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.syncope.core.provisioning.api.utils.RealmUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


@RunWith(Parameterized.class)
public class ParseGroupOwnerRealmTest {
	String groupOwner;
	String realmPath;
	String groupKey;
	boolean expectedResult;
	
	public static final String REALM_PATH = "path";
	public static final String GROUP_KEY = "key";
	
	public static final String VALID_GROUP = REALM_PATH+"@"+GROUP_KEY;
	public static final String NOT_VALID_GROUP = REALM_PATH+"_"+GROUP_KEY;
	public static final String NULL_GROUP = null;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Parameters
	public static List<TestParameters> getData() {
		List<TestParameters> testInputs = new ArrayList<>();
		
		testInputs.add(new TestParameters(VALID_GROUP,true));
		testInputs.add(new TestParameters(NOT_VALID_GROUP,false));
		testInputs.add(new TestParameters(NULL_GROUP,NullPointerException.class));
		
		return testInputs;
				
	}
	

    public ParseGroupOwnerRealmTest(TestParameters input) {
		this.groupOwner = input.getGroupOwner();
		if (input.getExpectedException()!=null) {
			expectedException.expect(input.getExpectedException());
		}
		else {
			this.expectedResult = input.getExpectedResult();
		}
	}


	@Test
    public void test() {
		Optional<Pair<String,String>> expectedValue = Optional.of(Pair.of(REALM_PATH,GROUP_KEY));
		Optional<Pair<String, String>> actualValue = RealmUtils.parseGroupOwnerRealm(this.groupOwner);
		assertEquals(expectedValue.equals(actualValue),this.expectedResult);
    }
	
	private static class TestParameters {
		String groupOwner;
		Class<? extends Exception> expectedException;
		boolean expectedResult;
		
		public TestParameters(String groupOwner, Class<? extends Exception> expectedException) {
			this.expectedException = expectedException;
			this.groupOwner = groupOwner;
		}

		public TestParameters(String groupOwner, boolean expectedResult) {
			this.expectedResult = expectedResult;
			this.groupOwner = groupOwner;
		}
		
		public String getGroupOwner() {
			return groupOwner;
		}

		public Class<? extends Exception> getExpectedException() {
			return expectedException;
		}
		
		public boolean getExpectedResult() {
			return this.expectedResult;
		}
	}
}
