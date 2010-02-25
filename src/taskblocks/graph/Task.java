/*
 * Copyright (C) Jakub Neubauer, 2007
 *
 * This file is part of TaskBlocks
 *
 * TaskBlocks is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * TaskBlocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package taskblocks.graph;

import java.awt.Rectangle;

import taskblocks.utils.ArrayUtils;
import taskblocks.utils.Utils;

/**
 * Representation of task displayed in TaskGraphComponent. Used internally by TaskGraphComponent.
 */
class Task extends GraphObject {
	
	/** User object (from the model) */
	Object _userObject;
	
	/** Row (man) that contains this task */
	TaskRow _row;
	
	/** Connections that points TO this task */
	Connection[] _incommingConnections;
	
	/** Connections going from this task */
	Connection[] _outgoingConnections;
	
	/** How many working days the task takes */
	private long _duration;
	
	/** How many working days the task takes */
	private long _actualDuration;
	
	/** When the task starts */
	private long _startTime;
	
	/** Finish time (counted mathematically. So, for example 1 day task finishes at start-time + 1) . Is automatically counted from startTime and duration */
	private long _finishTime;
	
	/** Finish time (counted for human. So, for example 1 day task finishes at the same day as it starts) */
	private long _finishTimeForTooltip;
	
	private String _comment;
	
	/** Bouds of the displayed task rectangle (in pixels, on the TaskGraphComponent) */
	Rectangle _bounds = new Rectangle();
	
	/** 1 bit information used in algorithms traversing through the task dependency graph */
	boolean _flag;
	
	TaskGraphRepresentation _builder;
	
	Task(Object userObject, TaskGraphRepresentation builder) {
		_userObject = userObject;
		_builder = builder;
	}
	
	public void addIncommingConnection(Connection c) {
		_incommingConnections = (Connection[])ArrayUtils.addToArray(_incommingConnections, c);
	}
	
	public void addOutgoingConnection(Connection c) {
		_outgoingConnections = (Connection[])ArrayUtils.addToArray(_outgoingConnections, c);
	}
	
	private void repairStartTime() {
		_startTime = Utils.repairStartTime(_startTime);
	}

	/** Returns the netto duration of a task */
	public long getDuration() {
		return _duration;
	}
	
	/**
	 * Gets actual duration, which means how much was already done
	 */
	public long getActualDuration() {
		return _actualDuration;
	}

	/**
	 * Sets task duration.
	 * 
	 * @param duration The tasks duration
	 * @param actual How much was already done.
	 */
	public void setDuration(long duration, long actual) {
		long oldDuration = _duration;
		this._duration = duration;
		long oldActualDuration = _actualDuration;
		this._actualDuration = actual;
		_finishTime = Utils.countFinishTime(_startTime, _duration);
		_finishTimeForTooltip = _finishTime-1;
		if( (oldDuration != _duration) || (oldActualDuration != _actualDuration) ){
			_builder.setDirty();
		}
	}
	
	public void setDuration(long duration) {
		long oldDuration = _duration;
		this._duration = duration;
		_finishTime = Utils.countFinishTime(_startTime, _duration);
		_finishTimeForTooltip = _finishTime-1;
		if(oldDuration != _duration) {
			_builder.setDirty();
		}
	}

	public long getFinishTime() {
		return _finishTime;
	}
	public long getFinishTimeForTooltip() {
		return _finishTimeForTooltip;
	}

	public long getStartTime() {
		return _startTime;
	}

	public void setStartTime(long time) {
		long oldTime = _startTime;
		_startTime = time;
		repairStartTime();
		_finishTime = Utils.countFinishTime(_startTime, _duration);
		_finishTimeForTooltip = _finishTime-1;
		if(_startTime != oldTime) {
			_builder.setDirty();
		}
	}

	// TODO: multiple with workers duration/effort ratio?
	public long getRealDuration() {
		return _finishTime - _startTime;
	}
	
	public TaskRow getRow() {
		return _row;
	}

	public void setComment( String comment ){
		_comment = comment;
	}
	
	public String getComment() {
		return _comment;
	}

}
