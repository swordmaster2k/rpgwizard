/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import java.util.Objects;

/**
 * Basic event to be used in assets that can trigger events.
 *
 * @author Joshua Michael Daly
 */
public class Event {

	private EventType type;
	private String program;

	public Event(EventType type, String program) {
		this.type = type;
		this.program = program;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + Objects.hashCode(this.type);
		hash = 29 * hash + Objects.hashCode(this.program);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Event other = (Event) obj;
		if (!Objects.equals(this.program, other.program)) {
			return false;
		}
		if (this.type != other.type) {
			return false;
		}
		return true;
	}

}
