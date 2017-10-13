/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import java.util.stream.Stream;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public enum KeyType {

	BACKSPACE("BACKSPACE"), TAB("TAB"), ENTER("ENTER"), PAUSE("PAUSE"), CAPS(
			"CAPS"), ESC("ESC"), SPACE("SPACE"), PAGE_UP("PAGE_UP"), PAGE_DOWN(
			"PAGE_DOWN"), END("END"), HOME("HOME"), LEFT_ARROW("LEFT_ARROW"), UP_ARROW(
			"UP_ARROW"), RIGHT_ARROW("RIGHT_ARROW"), DOWN_ARROW("DOWN_ARROW"), INSERT(
			"INSERT"), DELETE("DELETE"), ZERO("0"), ONE("1"), TWO("2"), THREE(
			"3"), FOUR("4"), FIVE("5"), SIX("6"), SEVEN("7"), EIGHT("8"), NINE(
			"9"), A("A"), B("B"), C("C"), D("D"), E("E"), F("F"), G("G"), H("H"), I(
			"I"), J("J"), K("K"), L("L"), M("M"), N("N"), O("O"), P("P"), Q("Q"), R(
			"R"), S("S"), T("T"), U("U"), V("V"), W("W"), X("X"), Y("Y"), Z("Z"), NUMPAD_0(
			"NUMPAD_0"), NUMPAD_1("NUMPAD_1"), NUMPAD_2("NUMPAD_2"), NUMPAD_3(
			"NUMPAD_3"), NUMPAD_4("NUMPAD_4"), NUMPAD_5("NUMPAD_5"), NUMPAD_6(
			"NUMPAD_6"), NUMPAD_7("NUMPAD_7"), NUMPAD_8("NUMPAD_8"), NUMPAD_9(
			"NUMPAD_9"), MULTIPLY("MULTIPLY"), ADD("ADD"), SUBSTRACT(
			"SUBSTRACT"), DECIMAL("DECIMAL"), DIVIDE("DIVIDE"), F1("F1"), F2(
			"F2"), F3("F3"), F4("F4"), F5("F5"), F6("F6"), F7("F7"), F8("F8"), F9(
			"F9"), F10("F10"), F11("F11"), F12("F12"), SHIFT("SHIFT"), CTRL(
			"CTRL"), ALT("ALT"), PLUS("PLUS"), COMMA("COMMA"), MINUS("MINUS"), PERIOD(
			"PERIOD"), PULT_UP("PULT_UP"), PULT_DOWN("PULT_DOWN"), PULT_LEFT(
			"PULT_LEFT"), PULT_RIGHT("PULT_RIGHT");

	private final String value;

	private KeyType(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	public static String[] toStringArray() {
        return Stream.of(KeyType.values()).map(KeyType::value).toArray(String[]::new);
    }
}
