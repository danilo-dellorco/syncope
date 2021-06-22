package org.apache.syncope.core.provisioning.api.mytests.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.syncope.core.provisioning.api.utils.RealmUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


@RunWith(Parameterized.class)
public class NormalizeTest {
	final Collection<String> realms;
	String realmsType;
	
	
	public static final String NULL_REALMS = "nr";
	public static final String WITH_OWNER_REALMS = "wr";
	public static final String WITHOUT_OWNER_REALMS = "wor";
	
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Parameters
	public static List<TestParameters> getData() {
		List<TestParameters> testInputs = new ArrayList<>();
		testInputs.add(new TestParameters(WITH_OWNER_REALMS));
		testInputs.add(new TestParameters(WITHOUT_OWNER_REALMS));
		testInputs.add(new TestParameters(NULL_REALMS));
		
		return testInputs;
	}
	
	
    public NormalizeTest(TestParameters input) {
    	this.realms = input.getRealms();
    	this.realmsType = input.getRealmsType();
	}

	@Test
    public void test() {
		Set<String> expectedRealms = new HashSet<>();
			expectedRealms.add("realmA");
			expectedRealms.add("realmB");
		Set<String> expectedOwners = new HashSet<>();
		if (realmsType.equals(WITH_OWNER_REALMS)) {
			expectedOwners.add("realm1@owner1");
			expectedOwners.add("realm2@owner2");
		}
		else if (realmsType.equals(NULL_REALMS)) {
			expectedRealms.clear();
		}
		
		Pair<Set<String>, Set<String>> expected = Pair.of(expectedRealms,expectedOwners);
		Pair<Set<String>, Set<String>> actual = RealmUtils.normalize(realms);
		
		assertEquals(expected, actual);
	}
	
	private static class TestParameters {
		Collection<String> realms;
		String realmsType;
		
		public TestParameters(String realmsType) {
			this.realms = new HashSet<>();
			this.realmsType = realmsType;
			realms.add("realmA");
			realms.add("realmB");
			
			if (realmsType.equals(WITH_OWNER_REALMS)) {
				realms.add("realm1@owner1");
				realms.add("realm2@owner2");
			}
			else if (realmsType.equals(NULL_REALMS)) {
				realms = null;
			}
		}

		public Collection<String> getRealms() {
			return realms;
		}

		public String getRealmsType() {
			return realmsType;
		}
	}
	
}

