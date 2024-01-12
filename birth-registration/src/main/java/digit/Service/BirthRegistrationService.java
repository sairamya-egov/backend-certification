package digit.Service;


import digit.config.BTRConfiguration;
import digit.enrichment.BirthApplicationEnrichment;
import digit.kafka.Producer;
import digit.repository.BirthRegistrationRepository;
import digit.validators.BirthApplicationValidator;
import digit.web.models.BirthApplicationSearchCriteria;
import digit.web.models.BirthRegistrationApplication;
import digit.web.models.BirthRegistrationRequest;
import digit.web.models.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class BirthRegistrationService {

    @Autowired
    private BirthApplicationValidator birthApplicationValidator;

    @Autowired
    private BirthApplicationEnrichment birthApplicationEnrichment;

    @Autowired
    private BTRConfiguration configuration;
    @Autowired
    private BirthRegistrationRepository birthRegistrationRepository;

    @Autowired
    private digit.Service.UserService userService;

    @Autowired
    private digit.Service.WorkflowService workflowService;


    @Autowired
    private Producer producer;

    public List<BirthRegistrationApplication> registerBtRequest(BirthRegistrationRequest birthRegistrationRequest) {
        // Validate applications
        birthApplicationValidator.validateBirthApplication(birthRegistrationRequest);

        // Enrich applications
        birthApplicationEnrichment.enrichBirthApplication(birthRegistrationRequest);

        // Enrich/Upsert user in upon birth registration
        userService.callUserService(birthRegistrationRequest);

        // Initiate workflow for the new application
        workflowService.updateWorkflowStatus(birthRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
        producer.push(configuration.getCreateTopic(), birthRegistrationRequest);

        // Return the response back to user
        return birthRegistrationRequest.getBirthRegistrationApplications();
    }

    public List<BirthRegistrationApplication> searchBtApplications(RequestInfo requestInfo, BirthApplicationSearchCriteria birthApplicationSearchCriteria) {
        // Fetch applications from database according to the given search criteria
        List<BirthRegistrationApplication> applications = birthRegistrationRepository.getApplications(birthApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        // Enrich mother and father of applicant objects
        applications.forEach(application -> {
            birthApplicationEnrichment.enrichFatherApplicantOnSearch(application);
            birthApplicationEnrichment.enrichMotherApplicantOnSearch(application);
        });


        //WORKFLOW INTEGRATION
        applications.forEach(application -> {
            application.setWorkflow(Workflow.builder().status(workflowService.getCurrentWorkflow(requestInfo, application.getTenantId(), application.getApplicationNumber()).getState().toString()).build());
        });

        // Otherwise return the found applications
        return applications;
    }

    public BirthRegistrationApplication updateBtApplication(BirthRegistrationRequest birthRegistrationRequest) {
        // Validate whether the application that is being requested for update indeed exists
        BirthRegistrationApplication existingApplication = birthApplicationValidator.validateApplicationExistence(birthRegistrationRequest.getBirthRegistrationApplications().get(0));
        existingApplication.setWorkflow(birthRegistrationRequest.getBirthRegistrationApplications().get(0).getWorkflow());
        log.info(existingApplication.toString());
        birthRegistrationRequest.setBirthRegistrationApplications(Collections.singletonList(existingApplication));

        // Enrich application upon update
        birthApplicationEnrichment.enrichBirthApplicationUponUpdate(birthRegistrationRequest);

        workflowService.updateWorkflowStatus(birthRegistrationRequest);

        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(configuration.getUpdateTopic(), birthRegistrationRequest);

        return birthRegistrationRequest.getBirthRegistrationApplications().get(0);
    }
}
