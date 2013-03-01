/*
 * Copyright (c) NASK, NCSC
 * 
 * This file is part of HoneySpider Network 2.0.
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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

class ScopedContext {
	private List<Object> stack;

	public ScopedContext(Object dataDictionary) {
		this.stack = new ArrayList<Object>();
		this.stack.add(dataDictionary);
	}

	public Object pushSection(String sectionName) {
		Object cursorValue = this.getCursorValue();
		if (isNonLookupable(cursorValue)) {
			throw new EvaluationError(
				String
						.format(
								"pushSection called when current cursor value is not a map (is: %s)",
								cursorValue));

		}
		Object newContext = lookup(cursorValue, sectionName);
		this.stack.add(newContext);
		return newContext;
	}

	public void pop() {
		this.stack.remove(this.stack.size() - 1);
	}

	public Object getCursorValue() {
		return this.stack.get(this.stack.size() - 1);
	}

	public boolean isEmptyContext(Object cursorPosition) {
		if (cursorPosition == null) {
			return true;
		}
		if (cursorPosition instanceof Boolean) {
			return ((Boolean) cursorPosition).booleanValue();
		}
		if (cursorPosition instanceof Collection) {
			return ((Collection) cursorPosition).size() == 0;
		}
		return false;
	}

	/**
	 * Get the value associated with a name in the current context. The current
	 * context could be a map/bean in a list, or a map/bean outside a list.
	 *
	 * @param name
	 *            the name to look up
	 * @return the value associated with the name
	 */
	public Object lookup(String name) {
		// start looking at the top of the stack
		int i = this.stack.size() - 1;
		while (true) {
			Object context = this.stack.get(i);
			if (isNonLookupable(context)) {
				// can't look it up here
				i -= 1;
			} else {
				Object value = lookup(context, name);
				if (value == null) {
					i -= 1;
				} else {
					return value;
				}
			}
			if (i < 0) {
				throw new UndefinedVariable(String.format("'%s' is not defined", name));
			}
		}
	}

	private static Object lookup(Object context, String name) {
		if (context instanceof Map) {
			return ((Map) context).get(name);
		} else {
			// bean?
			Class<? extends Object> contextClass = context.getClass();
			Object value = null;
			String nameCapFirst = name.substring(0, 1).toUpperCase() + name.substring(1);
			String[] prefixes = new String[] { "get", "is" };
			for (String prefix : prefixes) {
				try {
					Method getter = contextClass.getMethod(prefix + nameCapFirst, new Class[]{});
					getter.setAccessible(true);
					value = getter.invoke(context, new Object[] {});
				} catch (SecurityException e) {
					// swallow
				} catch (NoSuchMethodException e) {
					// swallow
				} catch (IllegalArgumentException e) {
					// swallow
				} catch (IllegalAccessException e) {
					// swallow
				} catch (InvocationTargetException e) {
					// swallow
				}
				if (value != null) {
					return value;
				}
			}
			// try field access
			try {
				Field field = contextClass.getField(name);
				field.setAccessible(true);
				value = field.get(context);
			} catch (SecurityException e) {
				// swallow
			} catch (NoSuchFieldException e) {
				// swallow
			} catch (IllegalArgumentException e) {
				// swallow
			} catch (IllegalAccessException e) {
				// swallow
			}
			return value;
		}
	}

	private static boolean isNonLookupable(Object context) {
		if (context == null) {
			return true;
		}
		Class<? extends Object> contextClass = context.getClass();
		// primitives are non lookup-able, so are non-Map collections
		if (Map.class.isAssignableFrom(contextClass)) {
			return false;
		}
		return contextClass.isPrimitive()
				|| contextClass.isAssignableFrom(Byte.class)
				|| contextClass.isAssignableFrom(Short.class)
				|| contextClass.isAssignableFrom(Integer.class)
				|| contextClass.isAssignableFrom(Long.class)
				|| contextClass.isAssignableFrom(Float.class)
				|| contextClass.isAssignableFrom(Double.class)
				|| contextClass.isAssignableFrom(Character.class)
				|| contextClass.isAssignableFrom(String.class)
				|| contextClass.isAssignableFrom(Boolean.class)
				|| Collection.class.isAssignableFrom(contextClass);
	}

	void pushObject(Object item) {
		this.stack.add(item);
	}
}
