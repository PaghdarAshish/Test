package com.activiti.oaf.adapter;

import com.activiti.oaf.adapter.model.runtime.CreateProcessInstanceRepresentation;
import com.activiti.oaf.adapter.scheduler.OAFSchedulerOperation;
import com.activiti.oaf.adapter.util.OAFOperation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;
/**
 * @author Pradip Patel
 */
@SpringBootApplication
@EnableScheduling
public class OafAdapterApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(OafAdapterApplication.class, args);
    }

    @Bean
    @Scope(value = "prototype")
    public CreateProcessInstanceRepresentation getCreateProcessInstanceRepresentation() {
        return new CreateProcessInstanceRepresentation();
    }

    @Bean
    @Scope(value = "prototype")
    public OAFOperation oafOperation() {
        return new OAFOperation();
    }

    @Bean
    @Scope(value = "prototype")
    public OAFSchedulerOperation schedulerOperation(){
        return new OAFSchedulerOperation();
    }
}
