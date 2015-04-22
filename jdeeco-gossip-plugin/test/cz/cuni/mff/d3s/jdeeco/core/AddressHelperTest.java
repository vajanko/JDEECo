/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.core;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class AddressHelperTest {
	@Test
	public void encodeDecodeIP() {
		int[] bytes1 = { 192, 168, 1, 100 };
		int ip1 = AddressHelper.encodeIP(bytes1);
		int[] bytes2 = AddressHelper.decodeIP(ip1);
		int ip2 = AddressHelper.encodeIP(bytes2);
		
		assertArrayEquals(bytes1, bytes2);
		assertEquals(ip1, ip2);
	}
	
	@Test
	public void encodeDecodeID() {
		int ip1 = AddressHelper.encodeIP(192,168,0,100);
		String id = AddressHelper.encodeID("C", ip1);
		
		assertEquals(id, "C.192.168.0.100");
		
		int ip2 = AddressHelper.decodeID(id);
		assertEquals(ip2, ip1);
	}
}
