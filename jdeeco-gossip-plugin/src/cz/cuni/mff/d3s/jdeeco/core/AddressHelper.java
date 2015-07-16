/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.core;

import cz.cuni.mff.d3s.jdeeco.network.address.Address;
import cz.cuni.mff.d3s.jdeeco.network.address.IPAddress;

/**
 * Helper class providing ability to work with IP addresses.
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class AddressHelper {
	
	/**
	 * Encodes individual four bytes of an IP address into single integer.
	 * 
	 * @param a1 First part of IP address.
	 * @param a2 Second part of IP address.
	 * @param a3 Third part of IP address.
	 * @param a4 Fourth part of IP address.
	 * @return IP address encoded into single integer.
	 */
	public static int encodeIP(int a1, int a2, int a3, int a4) {
		return encodeIP(new int[] { a1, a2, a3, a4 });
	}
	/**
	 * Encodes given component ID and unique node ID into a common unique identification.
	 * The node ID could be an encoded IP address.
	 *  
	 * @param component Component ID which should not be unique. 
	 * @param nodeId Unique node ID such as IP address.
	 * @return Common unique identified of a component including the node.
	 */
	public static String encodeID(String component, int nodeId) {		
		return String.format("%s.%d", component, nodeId);
	}
	/**
	 * Decodes given common unique identifier of a component and return node ID.
	 * This is a reverse operation to encodeID.
	 * 
	 * @param id Common unique identified of a component including the node.
	 * @return Node Id.
	 */
	public static int decodeID(String id) {
		String[] parts = id.split("\\.");
		long nodeId = Long.parseLong(parts[1]);
		return (int)nodeId;
	}
	/**
	 * Decodes given common unique identifier of a component and return node IP address.
	 * 
	 * @param id Common unique identified of a component including the node.
	 * @return Node IP address.
	 */
	public static Address decodeAddress(String id) {
		int nodeId = decodeID(id);
		
		return createIP(nodeId);
	}
	/**
	 * From given unique ID of a node creates an IP address.
	 * 
	 * @param nodeId Unique node ID.
	 * @return Node IP address.
	 */
	public static IPAddress createIP(int nodeId) {
		return new IPAddress(formatIP(encodeIP(10, 0, 0, 0) + nodeId));
	}
	/**
	 * Encodes given array of bytes holding individual bytes of an IP address into
	 * single integer value.
	 * 
	 * @param bytes Byte array holding individual parts of an IP address.
	 * @return IP address encoded into single integer.
	 */
	public static int encodeIP(int[] bytes) {
		long res = 0;
		for (int i = 0; i < 4; ++i) {
			res += bytes[i];
			res <<= 8;
		}
		res >>= 8;
		return (int)res;
	}
	/**
	 * Decodes given integer value holding an encoded IP address into individual bytes
	 * of the address.
	 * 
	 * @param address IP address encoded in a single integer value.
	 * @return A byte array holding individual IP address bytes.
	 */
	public static int[] decodeIP(int address) {
		long tmp = toLong(address);
		int[] res = new int[4];
		for (int i = 3; i >= 0; i--) {
			res[i] = (int)(tmp & 0xff);
			tmp >>= 8;
		}
		
		return res;
	}
	/**
	 * Formats given IP address encoded as a single integer value into IPv4 format.
	 * 
	 * @param address IP address encoded in a single integer value.
	 * @return IPv4 string representation of the IP address.
	 */
	public static String formatIP(int address) {
		int[] ip = decodeIP(address);
		return String.format("%d.%d.%d.%d", ip[0], ip[1], ip[2], ip[3]);
	}
	/**
	 * Converts given integer into a long setting the upper bytes to zero.
	 *  
	 * @param value Integer value to be converted.
	 * @return Unsigned long value.
	 */
	private static long toLong(int value) {
		return value & 0x00000000ffffffffL;
	}
}
