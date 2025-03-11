package org.openmrs.module.consultationworkflow;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Workflow {
	
	private String uuid;
	
	private String name;
	
	private String version;
	
	private Boolean retired;
	
	private Boolean active;
}
