package com.activiti.extension.bean;

import com.activiti.domain.idm.User;
import com.activiti.service.api.UserService;
import com.activiti.service.runtime.ActivitiService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.identity.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ActiveMQListener {

	@Autowired
	ActivitiService activitiService;

	@Autowired
	RepositoryService repositoryService;

	@Autowired
	RuntimeService runtimeService;

	@Autowired
	UserService userService;

	@JmsListener(destination = "aps-inbound")
	public void processMessage(String payload) {
		// Hard code the initiating user for now
		User user = userService.findActiveUserByEmail("admin@app.activiti.com");
		Long tenantId = user.getTenantId();
		Authentication.setAuthenticatedUserId(Long.toString(user.getId()));
//		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionKey("amq-start")
//				.latestVersion().singleResult();
//		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
//		      .messageEventSubscription("amq-start-message").latestVersion().singleResult();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("initiator", user.getId());
		map.put("message", payload);
//		activitiService.startProcessInstance (pd.getId(), map, "amq-start");
//		runtimeService.startProcessInstanceByMessage("amq-start-message", "", map);
		runtimeService.startProcessInstanceByMessageAndTenantId("amq-start-message", map, "tenant_1");
	}
}