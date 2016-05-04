/*
 * Copyright (c) NASK, NCSC
 * 
 * This file is part of HoneySpider Network 2.1.
 * 
 * This is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jsontemplate;

/**
 * An interface for objects that can get an {@link IFormatter} for a given name.
 * 
 */
public interface IFormatterResolver {
	/**
	 * Get a formatter with the given name.
	 * 
	 * @param formatterName
	 *            the name of the formatter.
	 * @return the appropriate IFormatter implementation, or null if the implementation cannot be found using this
	 *         resolver.
	 */
	IFormatter getFormatter(String formatterName);
}
