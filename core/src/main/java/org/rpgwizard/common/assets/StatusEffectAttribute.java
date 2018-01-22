/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

public class StatusEffectAttribute {

    private StatusEffectAttributeKind kind;
    private String name;
    private int duration;
    private float magnitude;

    public StatusEffectAttribute(String name) {
        this.kind = StatusEffectAttributeKind.CUSTOM;
        this.name = name;
        this.duration = 0;
        this.magnitude = 0.0f;
    }

    public StatusEffectAttribute(StatusEffectAttributeKind kind) {
        this.kind = kind;
        this.duration = 0;
        this.magnitude = 0.0f;
    }

    public StatusEffectAttribute(StatusEffectAttributeKind kind, int duration, float magnitude) {
        this.kind = kind;
        this.duration = duration;
        this.magnitude = magnitude;
    }

    /**
     * Returns the name of the status effect attribute. The name is determined by the attribute's
     * {@link StatusEffectAttributeKind}. Custom attributes have user-defined names while all other attribute kinds have
     * predefined names.
     *
     * @return status effect attribute name. Can be null.
     */
    public String getName() {
        switch (this.kind) {
        case CUSTOM:
            return this.name;
        default:
            return this.kind.name();
        }
    }

    /**
     * Sets the user-defined name for the status effect attribute.
     *
     * @param value
     *            user-defined name.
     */
    public void setName(String value) {
        this.name = value;
    }

    public StatusEffectAttributeKind getKind() {
        return this.kind;
    }

    public void setKind(StatusEffectAttributeKind kind) {
        this.kind = kind;
    }

    /**
     * Gets the duration of the status effect (# of rounds).
     * 
     * @return duration # of rounds). A duration less than or equal to 0 implies the effect remains until explicitly
     *         removed.
     */
    public int getDuration() {
        return this.duration;
    }

    /**
     * Sets the duration of the status effect (# of rounds).
     *
     * @param value
     *            (# of rounds). A duration less than or equal to 0 implies the effect remains until explicitly removed.
     */
    public void setDuration(int value) {
        this.duration = value;
    }

    /**
     * Gets the magnitude of the status effect attribute. The magnitude is the strength or impact of the effect whose
     * behavior is dependent on the {@link StatusEffectAttributeKind} of the attribute.
     *
     * @return magnitude (in units)
     */
    public float getMagnitude() {
        return this.magnitude;
    }

    /**
     * Sets the magnitude of the status effect attribute.
     *
     * @see #getMagnitude
     * @param value
     *            magnitude (in units)
     */
    public void setMagnitude(float value) {
        this.magnitude = value;
    }

}
