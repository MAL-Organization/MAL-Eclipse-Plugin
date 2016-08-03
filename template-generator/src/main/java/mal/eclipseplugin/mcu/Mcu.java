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
package mal.eclipseplugin.mcu;

import java.io.File;
import java.util.List;

import mal.eclipseplugin.template.TemplateSourceFile;

public interface Mcu {
	
	public String getLabel();
	
	public String getValue();
	
	public McuFamily getFamily();
	
	public List<String> getSymbols();
	
	public List<String> getIncludePaths();
	
	public List<File> getLinkerScripts();
	
	/**
	 * @return A list of libraries to link but do not have a library to be
	 * copied into the project. Usually, these libraries come with the
	 * toolchain.
	 */
	public List<String> getInstalledLibraries();
	
	/**
	 * @return A list of libraries to link and copy into the project.
	 */
	public List<File> getLibraries();
	
	public List<String> getSourceFolders();
	
	public List<TemplateSourceFile> getSourceFiles();

}
