/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.gossip.buffer;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class ExtensibleStorageTest {
	
	@Test
	public void putAndGet() {
		ExtensibleStorage st = new ExtensibleStorage();
		Integer in = 123;
		st.put(Integer.class, in);
		
		assertEquals(st.get(Integer.class), in);
	}
}
