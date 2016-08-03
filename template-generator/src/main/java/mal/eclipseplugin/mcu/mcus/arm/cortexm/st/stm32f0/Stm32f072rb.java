/**
 * @author Olivier Allaire
 * @copyright Copyright (c) 2015 Olivier Allaire
 * @par This file is part of MAL.
 *
 * MAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MAL.  If not, see <http://www.gnu.org/licenses/>.
 */
package mal.eclipseplugin.mcu.mcus.arm.cortexm.st.stm32f0;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Stm32f072rb extends Stm32f072 {

	public String getLabel() {
		return getValue();
	}

	public String getValue() {
		return "STM32F072RB";
	}
	
	@Override
	public List<String> getSymbols() {
		List<String> symbols = super.getSymbols();
		// Add MAL MCU specific symbol
		symbols.add("MAL_STM32F072RB");
		return symbols;
	}
	
	public List<File> getLinkerScripts() {
		List<File> scripts = new ArrayList<File>();
		ClassLoader classLoader = getClass().getClassLoader();
		String fileLocations = "mal/eclipseplugin/mcu/mcus/arm/cortexm/st/stm32f0/stm32f072rb/";
		// Load memory load script
		scripts.add(new File(classLoader.getResource(fileLocations + "mem.ld").getFile()));
		// Load sections script
		scripts.add(new File(classLoader.getResource(fileLocations + "sections.ld").getFile()));
		
		return scripts;
	}
	
	public List<File> getLibraries() {
		// No additional libraries
		return new ArrayList<File>();
	}

}
