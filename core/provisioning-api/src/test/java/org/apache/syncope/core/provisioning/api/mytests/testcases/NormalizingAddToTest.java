package org.apache.syncope.core.provisioning.api.mytests.testcases;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
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
public class NormalizingAddToTest {
	String realmContext;
	Set<String> realmsSet;
	String newRealm;
	
	public static final String VALID_NEW_REALM = "vnr";
	public static final String CONTAINED_NEW_REALM = "cnr";
	public static final String CONTAINED_REALM = "cr";
	public static final String NULL_REALM_SET = "nrs";
	public static final String NULL_NEW_REALM = "nnr";
	
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void configureParameters() {
		realmsSet = new HashSet<>();
		switch (realmContext) {
		
		case (VALID_NEW_REALM):
			realmsSet.add("RealmA");
			realmsSet.add("RealmB");
			newRealm = "AnotherRealm";
			break;
			
		case (CONTAINED_NEW_REALM):
			realmsSet.add("RealmA");
			realmsSet.add("RealmB");
			newRealm = "RealmA_something";
			break;
			
		case (CONTAINED_REALM):
			realmsSet.add("RealmA_something");
			realmsSet.add("RealmA_somethingElse");
			newRealm = "RealmA";
			break;
		}
	}
	
	@Parameters
	public static List<TestParameters> getData() {
		List<TestParameters> testInputs = new ArrayList<>();
		
		testInputs.add(new TestParameters(VALID_NEW_REALM));
		testInputs.add(new TestParameters(CONTAINED_NEW_REALM));
		testInputs.add(new TestParameters(CONTAINED_REALM));
		
		return testInputs;
	}
	
	/*
	 * Se newRealm inizia per realm ==> non aggiungere newRealm a realmsSet
	 * Se realm inizia per newRealm ==> aggiungi newRealm e rimuovi realm da realmsSet
	 */
    public NormalizingAddToTest(TestParameters input) {
    	this.realmContext = input.getRealmContext();
	}


	@Test
    public void test() {
		switch (realmContext) {
			case (VALID_NEW_REALM):
				assertTrue(RealmUtils.normalizingAddTo(realmsSet, newRealm));
				assertEquals(3, realmsSet.size());
				break;
			
			case (CONTAINED_NEW_REALM):
				assertFalse(RealmUtils.normalizingAddTo(realmsSet, newRealm));
				assertEquals(2, realmsSet.size());
				break;
		
			case (CONTAINED_REALM):
				assertTrue(RealmUtils.normalizingAddTo(realmsSet, newRealm));
				assertEquals(1, realmsSet.size());
				break;
		}
    }
	
	private static class TestParameters {
		String realmContext;

		public TestParameters(String realmContext) {
			this.realmContext = realmContext;
		}
		
		public String getRealmContext() {
			return realmContext;
		}
	}
}
