/**
 * 
 */
package cz.cuni.mff.d3s.jdeeco.core;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.cuni.mff.d3s.jdeeco.core.ExtensibleStorage;

/**
 * 
 * @author Ondrej Kovac <info@vajanko.me>
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
