package com.activiti.extension.bean.service;

import com.activiti.service.runtime.ProcessInstanceService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * This Bean is responsible to perform OAF process related operation.
 * @Author Pradip Patel
 */
@Service(value="oafProcess")
public class OAFProcessService {
	
	private static Logger logger = LoggerFactory.getLogger(OAFProcessService.class);
	
	@Autowired
	ProcessInstanceService processInstanceService;
	
	@Autowired
	RuntimeService runtimeService;
	
	public void cancelProcess(DelegateExecution execution) {
		String processId = (String)execution.getProcessInstanceId();
		try {
			//processInstanceService.deleteProcessInstance(processId);
			runtimeService.deleteProcessInstance(processId,"Task Rejected");
			logger.debug("OAFCancelProcess.cancelProcess():: Process Canceled");
		}
		catch(Exception e) {
			logger.debug("OAFCancelProcess.cancelProcess():: Exception :: "+e);			
		}
		
	}

}
