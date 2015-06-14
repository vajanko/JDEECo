/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.core;

import cz.cuni.mff.d3s.jdeeco.network.address.Address;
import cz.cuni.mff.d3s.jdeeco.network.address.IPAddress;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class AddressHelper {
	
	public static int encodeIP(int a1, int a2, int a3, int a4) {
		return encodeIP(new int[] { a1, a2, a3, a4 });
	}
	
	public static String encodeID(String component, int nodeId) {
		//int[] ip = decodeIP(nodeId);
		//return String.format("%s.%d.%d.%d.%d", component, ip[0], ip[1], ip[2], ip[3]);
		
		return String.format("%s.%d", component, nodeId);
	}
	public static int decodeID(String id) {
//		String[] parts = id.split("\\.");
//		int[] bytes = new int[4];
//		for (int i = 0; i < 4; ++i)
//			bytes[i] = Integer.parseInt(parts[i + 1]);
//		
//		return encodeIP(bytes);
		
		String[] parts = id.split("\\.");
		long nodeId = Long.parseLong(parts[1]);
		return (int)nodeId;
	}
	public static Address decodeAddress(String id) {
		int nodeId = decodeID(id);
		
		return createIP(nodeId);
	}
	public static IPAddress createIP(int nodeId) {
		return new IPAddress(formatIP(nodeId));
	}
	
	public static int encodeIP(int[] bytes) {
		long res = 0;
		for (int i = 0; i < 4; ++i) {
			res += bytes[i];
			res <<= 8;
		}
		res >>= 8;
		return (int)res;
	}
	public static int[] decodeIP(int address) {
		long tmp = toLong(address);
		int[] res = new int[4];
		for (int i = 3; i >= 0; i--) {
			res[i] = (int)(tmp & 0xff);
			tmp >>= 8;
		}
		
		return res;
	}
	
	public static String formatIP(int address) {
		int[] ip = decodeIP(address);
		return String.format("%d.%d.%d.%d", ip[0], ip[1], ip[2], ip[3]);
	}
	private static long toLong(int value) {
		return value & 0x00000000ffffffffL;
	}
}
