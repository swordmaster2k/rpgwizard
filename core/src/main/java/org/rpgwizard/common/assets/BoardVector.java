/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Objects;
import org.rpgwizard.common.Selectable;

/**
 *
 * TT_NULL = -1 'To denote empty slot in editor. TT_NORMAL = 0 'See TILE_TYPE
 * enumeration, board conversion.h TT_SOLID = 1 TT_UNDER = 2 TT_UNIDIRECTIONAL =
 * 4 'Incomplete / unnecessary. TT_STAIRS = 8 TT_WAYPOINT = 16
 *
 * @author Geoff Wilson
 * @author Joshua Michael Daly
 */
public class BoardVector implements Cloneable, Selectable {

	private String handle;
	private BoardVectorType type;
	private ArrayList<Point> points;
	private boolean isClosed;
	private ArrayList<Event> events;

	// Non-IO.
	private int layer;
	private Polygon polygon;
	private boolean selected;

	/**
     *
     */
	public BoardVector() {
        handle = "";
        type = BoardVectorType.SOLID;
        isClosed = false;
        points = new ArrayList<>();
        events = new ArrayList<>();
        events.add(new Event(EventType.OVERLAP, ""));

        layer = 0;
        polygon = new Polygon();
        selected = false;
    }
	/**
	 *
	 * @return
	 */
	public ArrayList<Point> getPoints() {
		return points;
	}

	/**
	 *
	 * @return
	 */
	public BoardVectorType getType() {
		return type;
	}

	/**
	 *
	 * @return
	 */
	public int getPointCount() {
		return points.size();
	}

	/**
	 *
	 * @param index
	 * @return
	 */
	public int getPointX(int index) {
		return (int) points.get(index).getX();
	}

	/**
	 *
	 * @param index
	 * @return
	 */
	public int getPointY(int index) {
		return (int) points.get(index).getY();
	}

	/**
	 *
	 * @return
	 */
	public int getLayer() {
		return (layer);
	}

	/**
	 *
	 * @return
	 */
	public String getHandle() {
		return (handle);
	}

	/**
	 *
	 * @return
	 */
	public double getWidth() {
		return polygon.getBounds().getWidth();
	}

	public double getHeight() {
		return polygon.getBounds().getHeight();
	}

	/**
	 *
	 * @return
	 */
	public boolean isClosed() {
		return (isClosed);
	}

	/**
	 *
	 * @param xVal
	 * @param yVal
	 */
	public void addPoint(long xVal, long yVal) {
		points.add(new Point((int) xVal, (int) yVal));
		polygon.addPoint((int) xVal, (int) yVal);
	}

	/**
	 *
	 * @param layer
	 */
	public void setLayer(int layer) {
		this.layer = layer;
	}

	/**
	 *
	 * @param closed
	 */
	public void setClosed(boolean closed) {
		isClosed = closed;
	}

	/**
	 *
	 * @param handle
	 */
	public void setHandle(String handle) {
		this.handle = handle;
	}

	/**
	 *
	 * @param type
	 */
	public void setType(BoardVectorType type) {
		this.type = type;
	}

	/**
	 *
	 * @return
	 */
	public Polygon getPolygon() {
		return polygon;
	}

	/**
	 *
	 * @param isClosed
	 */
	public void setIsClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	/**
	 *
	 * @param points
	 */
	public void setPoints(ArrayList<Point> points) {
		this.points = points;

		this.polygon = new Polygon();
		for (Point point : this.points) {
			this.polygon.addPoint((int) point.getX(), (int) point.getY());
		}
	}

	/**
	 *
	 * @param polygon
	 */
	public void setPolygon(Polygon polygon) {
		this.polygon = polygon;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelectedState(boolean state) {
		selected = state;
	}

	public ArrayList<Event> getEvents() {
		return events;
	}

	public void setEvents(ArrayList<Event> events) {
		this.events = events;
	}

	public void addEvent(Event event) {
		events.add(event);
	}

	/**
	 *
	 * @return @throws CloneNotSupportedException
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		super.clone();

		BoardVector clone = new BoardVector();
		clone.layer = layer;
		clone.handle = handle;
		clone.isClosed = isClosed;
		clone.points = (ArrayList<Point>) points.clone();
		clone.polygon = polygon;
		clone.type = type;
		clone.events = events;

		return clone;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 79 * hash + Objects.hashCode(this.handle);
		hash = 79 * hash + Objects.hashCode(this.type);
		hash = 79 * hash + Objects.hashCode(this.points);
		hash = 79 * hash + (this.isClosed ? 1 : 0);
		hash = 79 * hash + Objects.hashCode(this.events);
		hash = 79 * hash + this.layer;
		hash = 79 * hash + Objects.hashCode(this.polygon);
		hash = 79 * hash + (this.selected ? 1 : 0);
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
		final BoardVector other = (BoardVector) obj;
		if (this.isClosed != other.isClosed) {
			return false;
		}
		if (this.layer != other.layer) {
			return false;
		}
		if (this.selected != other.selected) {
			return false;
		}
		if (!Objects.equals(this.handle, other.handle)) {
			return false;
		}
		if (this.type != other.type) {
			return false;
		}
		if (!Objects.equals(this.points, other.points)) {
			return false;
		}
		if (!Objects.equals(this.events, other.events)) {
			return false;
		}
		return true;
	}

}
