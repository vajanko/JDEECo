/*******************************************************************************
 * Copyright 2012 Charles University in Prague
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package cz.cuni.mff.d3s.deeco.invokable;

import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.d3s.deeco.exceptions.KMException;
import cz.cuni.mff.d3s.deeco.knowledge.ISession;
import cz.cuni.mff.d3s.deeco.knowledge.KMHelper;
import cz.cuni.mff.d3s.deeco.knowledge.KPBuilder;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.scheduling.ProcessPeriodicSchedule;
import cz.cuni.mff.d3s.deeco.scheduling.ProcessSchedule;
import cz.cuni.mff.d3s.deeco.scheduling.ProcessTriggeredSchedule;

/**
 * Base class defining common functionalities for all schedulable processes.
 * 
 * @author Michal Kit
 * 
 */
public abstract class SchedulableProcess {

	protected KnowledgeManager km;

	public ProcessSchedule scheduling;

	public SchedulableProcess(ProcessSchedule scheduling, KnowledgeManager km) {
		this.scheduling = scheduling;
		this.km = km;
	}

	protected Object[] getParameterMethodValues(List<Parameter> in,
			List<Parameter> inOut, List<Parameter> out, String root)
			throws KMException {
		return getParameterMethodValues(in, out, inOut, root, null);
	}
	
	protected Object[] getParameterMethodValues(List<Parameter> in,
			List<Parameter> inOut, List<Parameter> out, String root, Object [] target)
			throws KMException {
		return getParameterMethodValues(in, out, inOut, root, target, null);
	}

	protected Object[] getParameterMethodValues(List<Parameter> in,
			List<Parameter> inOut, List<Parameter> out, String root, Object [] target,
			ISession session) throws KMException {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.addAll(in);
		parameters.addAll(inOut);
		Object[] result;
		if (target == null)
			result = new Object[parameters.size()
				+ ((out != null) ? out.size() : 0)];
		else 
			result = target;
		ISession localSession;
		if (session == null) {
			localSession = km.createSession();
			localSession.begin();
		} else
			localSession = session;
		try {
			while (localSession.repeat()) {
				for (Parameter p: parameters) {
					result[p.index] = km.getKnowledge(
							KPBuilder.appendToRoot(root, p.name), p.type,
							localSession);

				}
				if (session == null)
					localSession.end();
				else
					break;
			}
			parameters = out;
			for (Parameter p : parameters) {
				result[p.index] = KMHelper.getInstance(p.type);
			}
			return result;
		} catch (KMException kme) {
			System.out.println("Parameter getting error!");
			throw kme;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	/**
	 * Function used to store computed values during the process method
	 * execution in the knowledge repository.
	 * 
	 * @param parameterValues
	 *            list of method all parameters
	 * @param out
	 *            list of output parameter descriptions
	 * @param inOut
	 *            list of both output and input parameter descriptions
	 * @param root
	 *            knowledge level for which parameters should stored.
	 */
	protected void putParameterMethodValues(Object[] parameterValues,
			List<Parameter> inOut, List<Parameter> out, String root) {
		putParameterMethodValues(parameterValues, out, inOut, root, null);
	}

	/**
	 * Function used to store computed values during the process method
	 * execution in the knowledge repository. This version is session oriented.
	 * 
	 * @param parameterValues
	 *            list of method all parameters
	 * @param out
	 *            list of output parameter descriptions
	 * @param inOut
	 *            list of both output and input parameter descriptions
	 * @param root
	 *            knowledge level for which parameters should stored
	 * @param session
	 *            session instance within which all the storing operations
	 *            should be performed.
	 */
	protected void putParameterMethodValues(Object[] parameterValues,
			List<Parameter> inOut, List<Parameter> out, String root,
			ISession session) {
		if (parameterValues != null) {
			List<Parameter> parameters = new ArrayList<Parameter>();
			parameters.addAll(out);
			parameters.addAll(inOut);
			Object value;
			String completeName;
			ISession localSession;
			if (session == null) {
				localSession = km.createSession();
				localSession.begin();
			} else
				localSession = session;
			try {
				while (localSession.repeat()) {
					for (Parameter p : parameters) {
						value = parameterValues[p.index];
						completeName = KPBuilder.appendToRoot(root, p.name);
						km.putKnowledge(completeName, value, p.type,
								localSession);
					}
					if (session == null)
						localSession.end();
					else
						break;
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	/**
	 * Checks if the process has periodic scheduling.
	 * 
	 * @return true or false depending on the process scheduling.
	 */
	public boolean isPeriodic() {
		return scheduling instanceof ProcessPeriodicSchedule;
	}
	
	/**
	 * Checks if the process has triggered scheduling.
	 * 
	 * @return true or false depending on the process scheduling.
	 */
	public boolean isTriggered() {
		return scheduling instanceof ProcessTriggeredSchedule;
	}

	/**
	 * Function invokes single process execution.
	 */
	public abstract void invoke();
}
