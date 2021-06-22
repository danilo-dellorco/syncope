package org.apache.syncope.core.provisioning.api.mytests;

import org.apache.syncope.core.provisioning.api.mytests.testcases.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
	GetGroupOwnerTest.class,
	ParseGroupOwnerRealmTest.class,
	NormalizingAddToTest.class,
	NormalizeTest.class,
	GetEffectiveTest.class
	
})

public class RealmUtilsTest {   
}  