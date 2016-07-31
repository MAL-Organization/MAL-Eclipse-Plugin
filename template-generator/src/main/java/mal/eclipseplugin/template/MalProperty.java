package mal.eclipseplugin.template;

public enum MalProperty {
	
	MCU("mcuProperty"),
	SYSTEM_CLOCK("sysClkProperty"),
	SYSTEM_CLOCK_SOURCE("sysClkSrcProperty"),
	EXTERNAL_CLOCK("extClkProperty");
	
	private final String id;
	
	private MalProperty(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

}
