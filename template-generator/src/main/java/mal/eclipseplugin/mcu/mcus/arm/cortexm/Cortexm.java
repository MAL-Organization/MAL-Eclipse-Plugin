package mal.eclipseplugin.mcu.mcus.arm.cortexm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mal.eclipseplugin.mcu.Mcu;

public abstract class Cortexm implements Mcu {

	public List<String> getIncludePaths() {
		List<String> paths = new ArrayList<String>();
		// Add mal headers
		paths.add("\"${workspace_loc:/$(projectName)/mal/cmsis}\"");
		paths.add("\"${workspace_loc:/$(projectName)/mal/headers/cortexm}\"");
		return paths;
	}
	
	public List<String> getInstalledLibraries() {
		// This is libm, the math library.
		return Arrays.asList("m");
	}

}
