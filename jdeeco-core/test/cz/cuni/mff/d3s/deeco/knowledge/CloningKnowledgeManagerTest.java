package cz.cuni.mff.d3s.deeco.knowledge;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.d3s.deeco.model.runtime.RuntimeModelHelper;
import cz.cuni.mff.d3s.deeco.model.runtime.api.KnowledgePath;

/**
 * @author Michal Kit <kit@d3s.mff.cuni.cz>
 * 
 */
public class CloningKnowledgeManagerTest {

	private CloningKnowledgeManager tested;

	@Before
	public void setUp() throws Exception {
		tested = new CloningKnowledgeManager("TEST", null);
		tested.update(BaseKnowledgeManagerTest.createKnowledge());
	}

	@Test
	public void testImmutabilityOfGet() throws Exception {
		// WHEN a list is accessed from the ClonningKnowledgeManager
		KnowledgePath kp = RuntimeModelHelper.createKnowledgePath("list");
		List<KnowledgePath> knowledgePaths = new LinkedList<>();
		knowledgePaths.add(kp);
		ValueSet result = tested.get(knowledgePaths);
		List<Integer> firstList = (List<Integer>) result.getValue(kp);
		// AND WHEN it is modified
		firstList.clear();

		// THEN the list stored in the knowledge manager is not affected.
		result = tested.get(knowledgePaths);
		List<Integer> secondList = (List<Integer>) result.getValue(kp);

		assertTrue(firstList.size() != secondList.size());
	}

	@Test
	public void testImmutabilityOfUpdate() throws Exception {
		// WHEN a list is inserted into the ClonningKnowledgeManager
		KnowledgePath kp = RuntimeModelHelper.createKnowledgePath("list");
		List<KnowledgePath> knowledgePaths = new LinkedList<>();
		knowledgePaths.add(kp);

		List<Integer> firstList = new LinkedList<>();
		firstList.add(10);

		ChangeSet changeSet = new ChangeSet();
		changeSet.setValue(kp, firstList);
		// AND WHEN the list is modified
		firstList.clear();

		// THEN the list stored in the knowledge manager is not affected.
		ValueSet result = tested.get(knowledgePaths);
		List<Integer> secondList = (List<Integer>) result.getValue(kp);

		assertTrue(firstList.size() != secondList.size());
	}
}
