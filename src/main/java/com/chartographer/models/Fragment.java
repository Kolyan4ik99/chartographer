package com.chartographer.models;

import java.io.File;

/**
 * Класс описывающий Фрагмент Харты.
 */
public class Fragment implements Comparable<Fragment> {

	public Fragment(Integer x, Integer y, Integer width, Integer height, Integer id, File file) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.id = id;
		this.file = file;
	}

	private final Integer x;

	private final Integer y;

	private final Integer width;

	private final Integer height;

	private final Integer id;

	private final File file;

	public Integer getX() {
		return x;
	}

	public Integer getY() {
		return y;
	}

	public Integer getWidth() {
		return width;
	}

	public Integer getHeight() {
		return height;
	}

	public Integer getId() {
		return id;
	}

	public File getFile() {
		return file;
	}

	@Override
	public String toString() {
		return "Fragment{" +
				"x=" + x +
				", y=" + y +
				", width=" + width +
				", height=" + height +
				", id=" + id +
				", file=" + file +
				'}';
	}

	@Override
	public int compareTo(Fragment o) {
		return this.x - o.x;
	}
}
