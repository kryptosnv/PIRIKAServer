package jp.ie.cs.pirika.prolog;

import java.util.List;

public interface Prol {
	/**
	 * Debug.
	 * <p>
	 * Present state is written out in standard output.
	 * </p>
	 */
	public void listing();
	/**
	 * clear the Prolog engine.
	 * @param name - engine name.
	 */
	public void clear(String name);
	/**
	 * Run optional command.
	 * @param com - command
	 * @param v - variable name.
	 * @return
	 */
	public String command(String com,String v);
	/**
	 * Run optional command.
	 * @param com - command
	 * @return if it ends normally, return true.
	 */
	public Boolean command(String com);
	/**
	 * get all result.
	 * @param arg - command.
	 * @param v - variable name.
	 * @return
	 */
	public List<String> allResult(String arg,String v);
}
