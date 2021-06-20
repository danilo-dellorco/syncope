package org.apache.syncope.core.persistence.jpa.mytests;

import org.apache.bval.jsr.job.ConstraintValidatorContextImpl;
import org.apache.syncope.common.lib.SyncopeConstants;
import org.apache.syncope.common.lib.types.EntityViolationType;
import org.apache.syncope.core.persistence.api.entity.Realm;
import org.apache.syncope.core.persistence.api.entity.anyobject.ADynGroupMembership;
import org.apache.syncope.core.persistence.api.entity.group.Group;
import org.apache.syncope.core.persistence.api.entity.user.User;
import org.apache.syncope.core.persistence.jpa.entity.group.JPAGroup;
import org.apache.syncope.core.persistence.jpa.entity.user.JPAUser;
import org.apache.syncope.core.persistence.jpa.mytests.parameters.GroupValidatorTestParameters;
import org.apache.syncope.core.persistence.jpa.validation.entity.GroupValidator;
import org.apache.syncope.core.persistence.jpa.validation.entity.RealmValidator;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;

import groovyjarjarantlr4.v4.runtime.RuleDependencies;

import javax.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
	
	public static final String VALID_CONTEXT = "valid_context";
	public static final String NULL_CONTEXT = "null_context";

    private GroupValidator groupValidator;
    
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
    	return testInput;
    }
    
    //TODO finire membership (leggi report)

    @Before
    public void configure(){
        this.groupValidator = new GroupValidator();
    }

    @Before
    public void mockGroupSetup(){
        switch (groupType) {
        case (NULL_GROUP):
        	group = null;
        	break;
        	
        case (VALID_GROUP):
        	when(group.getName()).thenReturn(GROUP_NAME);
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
        }
        
    }
    
    
    @Before
    public void setupMockConstraintValidatorContext(){
        switch (this.contextType) {
        
        case (NULL_CONTEXT):
        	validatorContext = null;
        	break;
        
        case (VALID_CONTEXT):
	        //context --> builder --> node
	        
	        // Creo il mock del contesto
	        validatorContext =mock(ConstraintValidatorContext.class);
	        
	        // Creo il mock del builder
	        validatorContextBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
	        
	        // Creo il mock del nodo
	        node = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);
	        
	        /*
	         * Definisco a cascata il comportamento per 
	         * 
	         *             context.buildConstraintViolationWithTemplate(
	         *      getTemplate(EntityViolationType.InvalidGroupOwner,
	         *              "A group must either be owned by an user or a group, not both")).
	         *      addPropertyNode("owner").addConstraintViolation();
	         */
	        
	        // Ritorna il ConstraintViolationBuilder
	        // Any senza mock è il template della violazione
	        when (validatorContext.buildConstraintViolationWithTemplate(any())).thenReturn(validatorContextBuilder);
	        
	        // Aggiunge un nodo proprietà al path della ConstraintViolation
	        when (validatorContextBuilder.addPropertyNode(any())).thenReturn(node);
	        
	        // Aggiunge la violazione creata tramite builder se il validatore torna falso
	        when (node.addConstraintViolation()).thenReturn(validatorContext);
	        break;
        }

    }

    
    @Test
    public void isValidTest()  {
           boolean actualValue = groupValidator.isValid(group, validatorContext);
           Assert.assertEquals(expectedResult, actualValue);
    }

}