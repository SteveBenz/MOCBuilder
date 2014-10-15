package Bricklink.org.kleini.bricklink.data;

public enum AppearsT {
	Alternate("A"), Counterpart("C"), Extra("E"), Regular("R"), Unknown("U");

	private String code;

	private AppearsT(String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}

	public static AppearsT byCode(String code) throws Exception {
		for (AppearsT type : values()) {
			if (type.getCode().equals(code)) {
				return type;
			}
		}
		throw new Exception("Unknown Code " + code);
	}
}
