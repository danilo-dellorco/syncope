package org.apache.syncope.core.persistence.jpa.mytests;

import org.apache.syncope.common.lib.types.AnyTypeKind;
import org.apache.syncope.core.persistence.api.entity.AnyType;
import org.apache.syncope.core.persistence.api.entity.anyobject.ADynGroupMembership;
import org.apache.syncope.core.persistence.api.entity.group.Group;
import org.apache.syncope.core.persistence.api.entity.user.User;
import org.apache.syncope.core.persistence.jpa.mytests.parameters.GroupValidatorTestParameters;
import org.apache.syncope.core.persistence.jpa.validation.entity.GroupValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


import javax.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(Parameterized.class)
public class GroupValidatorTest {
	
	public static final String GROUP_NAME = "group_name";
	
	public static final String BOTH_OWNED_GROUP = "both_owned_group";
	public static final String GROUP_OWNED_GROUP = "group_owned_group";
	public static final String USER_OWNED_GROUP = "user_owned_group";
	public static final String NULL_GROUP = "null_group";
	public static final String VALID_GROUP = "valid_group";
	public static final String NOT_VALID_GROUP_NULL = "not_valid_group_null";
	public static final String NOT_VALID_GROUP_CHAR = "not_valid_group_char";
	
	public static final String NOT_VALID_GROUP_MEMB = "not_valid_group_memb";
	public static final String NOT_VALID_GROUP_SIZE = "not_valid_group_size";
	
	public static final String VALID_CONTEXT = "valid_context";
	public static final String NULL_CONTEXT = "null_context";

    private static GroupValidator groupValidator;
    
	String groupType;
	String contextType;
	boolean expectedResult;

    @Mock
    private Group group = mock (Group.class);
    
    @Mock
    private Group groupOwner = mock (Group.class);
    
    @Mock
    private User userOwner = mock (User.class);

    @Mock
    private ConstraintValidatorContext validatorContext;
    
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder validatorContextBuilder;
    
    @Mock
    ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext node;
    
    
    /*
     * Aggiunte dopo il miglioramento della TestSuite
     */
    
    @Mock
    private ADynGroupMembership membership = mock (ADynGroupMembership.class);
    List<ADynGroupMembership> membershipList = new ArrayList<>();
    private AnyType anytype = mock(AnyType.class);
    
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    

    public GroupValidatorTest(GroupValidatorTestParameters input) {
		this.groupType = input.getGroupType();
		this.contextType = input.getContextType();
		if (input.getExpectedException()!=null) { 
			expectedException.expect(input.getExpectedException());;
		}
		else {
			this.expectedResult = input.getExpectedResult();
		}
	}

    @Parameters
	public static List<GroupValidatorTestParameters> getParameters(){
    	
    	List<GroupValidatorTestParameters> testInput = new ArrayList<>();
    	
    	testInput.add (new GroupValidatorTestParameters(VALID_GROUP, VALID_CONTEXT, true));
    	testInput.add (new GroupValidatorTestParameters(NOT_VALID_GROUP_NULL, VALID_CONTEXT, false));
    	testInput.add (new GroupValidatorTestParameters(VALID_GROUP, NULL_CONTEXT, NullPointerException.class));
    	testInput.add (new GroupValidatorTestParameters(NULL_GROUP, VALID_CONTEXT, NullPointerException.class));

    	// Aggiunto dopo il miglioramento della TestSuite
    	testInput.add (new GroupValidatorTestParameters(BOTH_OWNED_GROUP, VALID_CONTEXT, false));
    	testInput.add (new GroupValidatorTestParameters(USER_OWNED_GROUP, VALID_CONTEXT, true));
    	testInput.add (new GroupValidatorTestParameters(GROUP_OWNED_GROUP, VALID_CONTEXT, true));
    	testInput.add (new GroupValidatorTestParameters(NOT_VALID_GROUP_CHAR, VALID_CONTEXT, false));
    	testInput.add (new GroupValidatorTestParameters(NOT_VALID_GROUP_MEMB, VALID_CONTEXT, false));
    	testInput.add (new GroupValidatorTestParameters(NOT_VALID_GROUP_SIZE, VALID_CONTEXT, false));
    	return testInput;
    }

    @BeforeClass
    public static void configure(){
        groupValidator = new GroupValidator();
    }

    @Before
    public void mockGroupSetup(){
        switch (groupType) {
        case (NULL_GROUP):
        	group = null;
        	break;
        	
        case (VALID_GROUP):
        	when(group.getName()).thenReturn(GROUP_NAME);
        	membershipSetup();
        	when(anytype.getKind()).thenReturn(AnyTypeKind.ANY_OBJECT); // aggiunto dopo miglioramento test suite
        	break;    	
        
        case (NOT_VALID_GROUP_NULL):
        	when(group.getName()).thenReturn(null);
        	break;
        	
        case (NOT_VALID_GROUP_CHAR):
        	when(group.getName()).thenReturn(GROUP_NAME+"&");
        	break;
        	
        case (GROUP_OWNED_GROUP):
        	when(group.getUserOwner()).thenReturn(null);
        	when(group.getGroupOwner()).thenReturn(groupOwner);
        	when(group.getName()).thenReturn(GROUP_NAME);
        	break;
        
        case (USER_OWNED_GROUP):
        	when(group.getUserOwner()).thenReturn(userOwner);
        	when(group.getGroupOwner()).thenReturn(null);
        	when(group.getName()).thenReturn(GROUP_NAME);
        	break;
        
        case (BOTH_OWNED_GROUP):
        	when(group.getUserOwner()).thenReturn(userOwner);
        	when(group.getGroupOwner()).thenReturn(groupOwner);
        	break;
        	
        case (NOT_VALID_GROUP_MEMB):
        	when(group.getName()).thenReturn(GROUP_NAME);
        	membershipSetup();
        	when(membership.getAnyType()).thenReturn(anytype);
        	when(anytype.getKind()).thenReturn(AnyTypeKind.USER); //instead of ANY_OBJECT
        	break;
        	
        case (NOT_VALID_GROUP_SIZE):
        	when(group.getName()).thenReturn(GROUP_NAME);
	        when(group.getADynMemberships()).thenAnswer(new Answer<Object>() {
	            private int count = 0;
	
	            public Object answer(InvocationOnMock invocation) {
	                if (count == 0) {
	                	count++;
	                	return membershipList;
	                }
	                membershipSetup();
	                return membershipList;
	            }
	        });
        }
    }
    
    
    @Before
    public void mockContextSetup(){
        switch (this.contextType) {
        
        case (NULL_CONTEXT):
        	validatorContext = null;
        	break;
        
        case (VALID_CONTEXT):
	        validatorContext =mock(ConstraintValidatorContext.class);
	        validatorContextBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
	        node = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);
	        when (validatorContext.buildConstraintViolationWithTemplate(any())).thenReturn(validatorContextBuilder);
	        when (validatorContextBuilder.addPropertyNode(any())).thenReturn(node);
	        when (node.addConstraintViolation()).thenReturn(validatorContext);
	        break;
        }

    }
    
    public void membershipSetup() {
    	membershipList.add(membership);
    	doReturn(membershipList).when(group).getADynMemberships();
    	when(membership.getAnyType()).thenReturn(anytype);
    }

    
    @Test
    public void isValidTest()  {
           boolean actualValue = groupValidator.isValid(group, validatorContext);
           Assert.assertEquals(expectedResult, actualValue);
    }

}