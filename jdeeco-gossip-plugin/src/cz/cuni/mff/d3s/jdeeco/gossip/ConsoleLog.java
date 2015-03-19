/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip;

/**
 * This class is intended for testing purpose only.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class ConsoleLog {
	public enum MsgType {
		KN,
		HD,
		PL
	}
	public enum ActType {
		SEND,
		RECV
	}
	
	public static void printRequest(int node, long time, MsgType msgType, ActType actType, Object data) {
		System.out.println(String.format("[%d] %4d %s %s %s", node, time, msgType, actType, data));
	}
}


