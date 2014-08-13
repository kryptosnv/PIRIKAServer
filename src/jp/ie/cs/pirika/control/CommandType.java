package jp.ie.cs.pirika.control;

/**
 * コマンドラインで使うコマンドの種類を定義.
 * @author katsura
 *
 */
public interface CommandType {
	/**
	 * Shutdown demand for this program.
	 */
	public static final int HALT = 3000;
	/**
	 * Command line help.
	 */
	public static final int HELP = 3999;
	/**
	 * The room list is acquired.
	 */
	public static final int ROOM_LIST = 3001;
	/**
	 * The room member is acquired.
	 */
	public static final int ROOM_MEMBER = 3002;
	/**
	 * The present knowledge base is acquired.
	 */
	public static final int READ_EALP = 3003;
	/**
	 * The argument tree of the room is acquired.
	 */
	public static final int VIEW_TREE = 3004;
	/**
	 * The room is deleted.
	 */
	public static final int REMOVE_ROOM = 3005;
	/**
	 * The room is saved.
	 */
	public static final int SAVE_ROOM = 3006;
	/**
	 * The room is loaded.
	 */
	public static final int LOAD_ROOM = 3007;
	/**
	 * The argument framework of the room is acquired.
	 */
	public static final int VIEW_FRAME = 3008;
	/**
	 * The configure file is reloaded.
	 */
	public static final int RELOAD = 3100;
	
	/**
	 * コマンドの種類を判定.
	 * @param commandType
	 * @return
	 */
	abstract int getCommandType(String commandType);
}
