package org.apache.syncope.core.provisioning.api.mytests.testcases;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.syncope.core.provisioning.api.utils.RealmUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


@RunWith(Parameterized.class)
public class GetGroupOwnerTest {
	String realmPath;
	String groupKey;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Parameters
	public static List<TestParameters> getData() {
		List<TestParameters> testInputs = new ArrayList<>();
		
		testInputs.add(new TestParameters("path","key",null));
		
		/* Test non considerati in quanto il comportamento è identico in tutti i casi e non viene lanciata nessuna eccezione
		 * 
		 * testInputs.add(new TestParameters("path","",IllegalArgumentException.class));
		 * testInputs.add(new TestParameters("","key",IllegalArgumentException.class));
		 * testInputs.add(new TestParameters("path",null,NullPointerException.class));
		 * testInputs.add(new TestParameters(null,"path",NullPointerException.class));
		*/
		return testInputs;
				
	}
	

    public GetGroupOwnerTest(TestParameters input) {
		this.realmPath = input.getRealmPath();
		this.groupKey = input.getGroupKey();
		if (input.getExpectedException()!=null) {
			expectedException.expect(input.getExpectedException());
		}
	}


	@Test
    public void test() {
		String expectedResult = String.format("%s@%s",this.realmPath,this.groupKey);
		String actualValue = RealmUtils.getGroupOwnerRealm(this.realmPath, this.groupKey);
        assertEquals(expectedResult, actualValue);
    }
	
	private static class TestParameters {
		String realmPath;
		String groupKey;
		Class<? extends Exception> expectedException;
		
		public TestParameters(String realmPath,String groupKey,Class<? extends Exception> expectedException) {
			this.realmPath = realmPath;
			this.groupKey = groupKey;
			this.expectedException = expectedException;
		}

		public String getRealmPath() {
			return realmPath;
		}

		public String getGroupKey() {
			return groupKey;
		}

		public Class<? extends Exception> getExpectedException() {
			return expectedException;
		}
	}
}
