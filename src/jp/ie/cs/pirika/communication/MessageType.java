package jp.ie.cs.pirika.communication;

/**
 * Definition of Message Protocol.
 * @author katsura
 *
 */
public interface MessageType {
	/**
	 * Request for the user ID.
	 */
	public static final int REQ_FOR_ID = 1000;
	/**
	 * My image is updated.
	 */
	public static final int UPDATE_PICTURE = 1001;
	/**
	 * Acquisition demand for image.
	 */
	public static final int GET_PICTURE = 1002;
	/**
	 * Acquisition demand for EALP.
	 */
	public static final int GET_EALP = 1003;
	/**
	 * Acquisition demand for EALP file list.
	 */
	public static final int GET_EALP_FILE_LIST = 1004;
	/**
	 * Request to send of truth value file.
	 */
	public static final int SEND_TRUTH_VALUE_FILE = 1005;
	/**
	 * Acquisition demand for truth value file.
	 */
	public static final int GET_TRUTH_VALUE_FILE = 1006;
	/**
	 * Acquisition demand for truth value file list.
	 */
	public static final int GET_TRUTH_VALUE_FILE_LIST = 1007;
	/**
	 * Grammatical check of the ealp file.
	 */
	public static final int CHECK_EALP = 1008;
	/**
	 * Request for the calculation of argumentation in 01value.
	 */
	public static final int CALCULATE_01VALUE = 2001;
	/**
	 * Request for the calculation of argumentation in 2value.
	 */
	public static final int CALCULATE_2VALUE = 2002;
	/**
	 * Request for the calculation of argumentation in 4value.
	 */
	public static final int CALCULATE_4VALUE = 2003;
	/**
	 * Request for the calculation of argumentation in 7value.
	 */
	public static final int CALCULATE_7VALUE = 2004;
	/**
	 * Request for the calculation of argumentation in intvalue.
	 */
	public static final int CALCULATE_INTVALUE = 2005;
	/**
	 * Request for the calculation of argumentation in user define truth value.
	 */
	public static final int CALCULATE_USER_DEFINE = 2006;
	/**
	 * The new room is created.
	 */
	public static final int CREATE_ROOM = 3001;
	/**
	 * The user ealp data is updated.
	 */
	public static final int UPDATE_ROOM = 3002;
	/**
	 *　Acquisition of argument result.
	 */
	public static final int GET_ROOM_RESULT = 3003;
	/**
	 * Acquisition of argument room list.
	 */
	public static final int GET_ROOM_LIST = 3004;
	/**
	 * Acquisition of member list in room.
	 */
	public static final int GET_ROOM_MEMBER_LIST = 3005;
	/**
	 * Acquisition of node information.(demonstration and attack relation)
	 */
	public static final int GET_ROOM_NODE_INFO = 3006;
	/**
	 * The room is deleted.
	 */
	public static final int DELETE_ROOM = 3007;
	/**
	 * Acquiring the result of grounded semantics.
	 */
	public static final int GET_GROUNDED = 4001;
	/**
	 * Acquiring the result of stable semantics.
	 */
	public static final int GET_STABLE = 4002;
	/**
	 * Acquiring the result of preferred semantics.
	 */
	public static final int GET_PREFERRED = 4003;
	/**
	 * Acquiring the result of complete semantics.
	 */
	public static final int GET_COMPLETE = 4004;
}
