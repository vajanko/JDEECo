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
public class MessageBufferTest {
	@Test
	public void putAndGet() {
		MessageBuffer buff = new MessageBuffer();
		
		Integer data = 123;
		String id = "xxx";
		buff.putData(Integer.class, id, data);
		
		assertEquals(buff.getData(Integer.class, id), data); 
	}
}
