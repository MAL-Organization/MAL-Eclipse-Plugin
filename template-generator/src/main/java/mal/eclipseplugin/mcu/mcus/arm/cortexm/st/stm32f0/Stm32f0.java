package mal.eclipseplugin.mcu.mcus.arm.cortexm.st.stm32f0;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mal.eclipseplugin.mcu.McuFamily;
import mal.eclipseplugin.mcu.mcus.arm.cortexm.Cortexm;
import mal.eclipseplugin.template.MalProperty;
import mal.eclipseplugin.template.TemplateSourceFile;

public abstract class Stm32f0 extends Cortexm {

	public McuFamily getFamily() {
		return McuFamily.CORTEX_M0;
	}
	
	public List<String> getSymbols() {
		List<String> symbols = new ArrayList<String>();
		// Add MAL family
		symbols.add("MAL_STM32F0");
		// Add HSE symbol
		String hse = String.format("HSE_VALUE=$(%s)", MalProperty.EXTERNAL_CLOCK.getId());
		symbols.add(hse);
		return symbols;
	}
	
	@Override
	public List<String> getIncludePaths() {
		List<String> paths = super.getIncludePaths();
		// Add stm headers
		paths.add("\"${workspace_loc:/$(projectName)/mal/stm}\"");
		return paths;
	}
	
	public List<String> getSourceFolders() {
		return Arrays.asList("stm/");
	}
	
	public List<TemplateSourceFile> getSourceFiles() {
		ClassLoader classLoader = getClass().getClassLoader();
		String fileLocations = "mal/eclipseplugin/mcu/mcus/arm/cortexm/st/stm32f0/";
		// Vectors file
		TemplateSourceFile vectorFile = new TemplateSourceFile();
		vectorFile.sourceFile = new File(classLoader.getResource(fileLocations + "vectors_stm32f0xx.c").getFile());
		vectorFile.destinationFolder = "stm/";
		return Arrays.asList(vectorFile);
	}

}
