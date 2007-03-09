package org.iana.rzm.trans;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.testng.annotations.Test;

/**
 * @author Jakub Laszkiewicz
 */
public class JbpmTest {
    @Test
    public void testSimpleProcess() {
        ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
                "<process-definition>" +
                        "  <start-state>" +
                        "    <transition to='s' />" +
                        "  </start-state>" +
                        "  <state name='s'>" +
                        "    <transition to='end' />" +
                        "  </state>" +
                        "  <end-state name='end' />" +
                        "</process-definition>"
        );

        ProcessInstance processInstance = new ProcessInstance(processDefinition);

        Token token = processInstance.getRootToken();
        assert processDefinition.getStartState() == token.getNode();

        token.signal();
        assert processDefinition.getNode("s") == token.getNode();

        token.signal();
        assert processDefinition.getNode("end") == token.getNode();
    }

    @Test
    public void testThreeStateProcess() {
        ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
                "<process-definition>" +
                        "  <start-state>" +
                        "    <transition to='phase one' />" +
                        "  </start-state>" +
                        "  <state name='phase one'>" +
                        "    <transition to='phase two' />" +
                        "  </state>" +
                        "  <state name='phase two'>" +
                        "    <transition to='phase three' />" +
                        "  </state>" +
                        "  <state name='phase three'>" +
                        "    <transition to='end' />" +
                        "  </state>" +
                        "  <end-state name='end' />" +
                        "</process-definition>"
        );

        ProcessInstance processInstance = new ProcessInstance(processDefinition);

        Token token = processInstance.getRootToken();
        assert processDefinition.getStartState() == token.getNode();

        token.signal();
        assert processDefinition.getNode("phase one") == token.getNode();

        token.signal();
        assert processDefinition.getNode("phase two") == token.getNode();

        token.signal();
        assert processDefinition.getNode("phase three") == token.getNode();

        token.signal();
        assert processDefinition.getNode("end") == token.getNode();
    }
}
