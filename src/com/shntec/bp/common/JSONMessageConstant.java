package com.shntec.bp.common;

public interface JSONMessageConstant {

	// JSON message key name definition
	
	// Action
	static final public String JSON_KEY_ACTION_HEADER = "Action";
	
	static final public String JSON_KEY_ACTION_CODE = "Code";

	static final public String JSON_KEY_AUTHENTICATION = "Authentication";
	
	static final public String JSON_KEY_PARAMETER = "Parameter";
	
	static final public String JSON_KEY_RESPONSE_RESULT = "Result";

	static final public String JSON_KEY_PAYLOAD = "Parameter";
	
	static final public String JSON_KEY_ERROR_CODE = "ErrorCode";
	
	static final public String JSON_KEY_ERROR_MESSAGE = "Msg";
	
	static final public String JSON_KEY_MORE_INFO = "More";

	// Shntec base platform action code definition
	static final public int ACTION_CODE_UNKNOWN_ACTION = -1;
	static final public String ACTION_NAME_UNKNOWN_ACTION = "UnknownAction";
	static final public String ACTION_DESCRIPTION_UNKNOWN_ACTION = "This action is not known, may be not initialized.";

	static final public int ACTION_CODE_GET_SERVER_INFO = 1000;
	static final public String ACTION_NAME_GET_SERVER_INFO = "GetServerInfoAction";
	static final public String ACTION_DESCRIPTION_GET_SERVER_INFO = "Get basic server information.";
	
	static final public int ACTION_CODE_GET_ACTION_LIST = 1001;
	static final public String ACTION_NAME_GET_ACTION_LIST = "GetActionListAction";
	static final public String ACTION_DESCRIPTION_GET_ACTION_LIST = "Retrieve all available action of server.";
	
	static final public int ACTION_CODE_TEST_SERVER_PUSH = 1002;
	static final public String ACTION_NAME_TEST_SERVER_PUSH = "TestServerPushAction";
	static final public String ACTION_DESCRIPTION_TEST_SERVER_PUSH = "Simulate server push ,broadcast server push content to client.";
	
	static final public int ACTION_CODE_FILE_UPLOAD_REQUEST = 1020;
	static final public String ACTION_NAME_FILE_UPLOAD_REQUEST = "FileUploadRequestAction";
	static final public String ACTION_DESCRIPTION_FILE_UPLOAD_REQUEST = "Upload file request.";
	
	static final public int ACTION_CODE_FILE_UPLOAD_CONTENT = 1021;
	
	static final public int ACTION_CODE_GET_FILE = 1022;
	static final public String ACTION_NAME_GET_FILE = "GetFileAction";
	static final public String ACTION_DESCRIPTION_GET_FILE = "To be added";
	
	static final public int ACTION_CODE_START_SERVER_PUSH = 1100;
	
	static final public int ACTION_CODE_FINISH_SERVER_PUSH = 1101;
	
	static final public int ACTION_CODE_SERVER_PUSH_CONTENT = 1102;
	
	static final public int ACTION_CODE_GET_SERVER_PUSH_STATUS = 1103;
	static final public String ACTION_NAME_GET_SERVER_PUSH_STATUS = "GetServerPushStatusAction";
	static final public String ACTION_DESCRIPTION_GET_SERVER_PUSH_STATUS = "Get current server push status.";
	

	
}
