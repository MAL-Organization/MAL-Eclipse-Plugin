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
package mal.eclipseplugin.template;

public enum EclipseOptionType {
	
	STRING("org.eclipse.cdt.managedbuilder.core.SetMBSStringOptionValue"),
	STRING_LIST("org.eclipse.cdt.managedbuilder.core.SetMBSStringListOptionValues"),
	BOOLEAN("org.eclipse.cdt.managedbuilder.core.SetMBSBooleanOptionValue"),
	APPEND_STRING("org.eclipse.cdt.managedbuilder.core.AppendToMBSStringOptionValue"),
	APPEND_STRING_LIST("org.eclipse.cdt.managedbuilder.core.AppendToMBSStringListOptionValues"),
	CREATE_SOURCE_FOLDER("org.eclipse.cdt.core.CreateSourceFolder"),
	ADD_FILES("org.eclipse.cdt.core.AddFiles");
	
	
	private final String type;
	
	private EclipseOptionType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}

}
