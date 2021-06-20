package org.apache.syncope.core.persistence.jpa.mytests.parameters;

public class GroupValidatorTestParameters {
	String groupType;
	String contextType;
	boolean constraintNotNull;
	boolean expectedResult;
	Class<? extends Exception> expectedException;
	

	public GroupValidatorTestParameters(String groupType, String contextType, Class<? extends Exception> expectedException) {
		this.groupType = groupType;
		this.contextType = contextType;
		this.expectedException = expectedException;
	}
	
	public GroupValidatorTestParameters(String groupType, String contextType, boolean expectedResult) {
		this.groupType = groupType;
		this.contextType = contextType;
		this.expectedResult = expectedResult;
		this.expectedException = null;
	}


	public String getGroupType() {
		return groupType;
	}


	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}


	public String getContextType() {
		return contextType;
	}


	public void setContextType(String contextType) {
		this.contextType = contextType;
	}


	public boolean isConstraintNotNull() {
		return constraintNotNull;
	}


	public void setConstraintNotNull(boolean constraintNotNull) {
		this.constraintNotNull = constraintNotNull;
	}


	public Class<? extends Exception> getExpectedException() {
		return expectedException;
	}


	public void setExpectedException(Class<? extends Exception> expectedException) {
		this.expectedException = expectedException;
	}

	public boolean getExpectedResult() {
		return expectedResult;
	}

	public void setExpectedResult(boolean expectedResult) {
		this.expectedResult = expectedResult;
	}
	
	

}
