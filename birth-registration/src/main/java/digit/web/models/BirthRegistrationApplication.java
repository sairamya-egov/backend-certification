package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * A Object holds the basic data for a Birth Registration Application
 */
@ApiModel(description = "A Object holds the basic data for a Birth Registration Application")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-01-08T11:44:14.667+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BirthRegistrationApplication {
        @JsonProperty("id")
        private String id = null;

        @JsonProperty("tenantId")
        private String tenantId = null;

        @JsonProperty("applicationNumber")
        private String applicationNumber = null;

        @JsonProperty("babyFirstName")
        private String babyFirstName = null;

        @JsonProperty("babyLastName")
        private String babyLastName = null;

        @JsonProperty("fatherOfApplicant")
        private FatherApplicant father= null;

        @JsonProperty("motherOfApplicant")
        private  MotherApplicant mother= null;

        @JsonProperty("fatherMobileNumber")
        private String fatherMobileNumber = null;

        @JsonProperty("motherMobileNumber")
        private String motherMobileNumber = null;

        @JsonProperty("doctorName")
        private String doctorName = null;

        @JsonProperty("hospitalName")
        private String hospitalName = null;

        @JsonProperty("placeOfBirth")
        private String placeOfBirth = null;

        @JsonProperty("timeOfBirth")
        private Integer timeOfBirth = null;

        @JsonProperty("address")
        private Address address = null;

        @JsonProperty("applicant")
        private Applicant applicant = null;

        @JsonProperty("auditDetails")
        private AuditDetails auditDetails = null;

        @Valid
        @JsonProperty("workflow")
        private Workflow workflow = null;




}
