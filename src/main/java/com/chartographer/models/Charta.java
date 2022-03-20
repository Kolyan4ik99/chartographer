package com.chartographer.models;

import com.chartographer.exceptions.FragmentNotFoundException;

import java.util.ArrayList;
import java.util.Collections;

public class Charta {

	private final Integer height;

	private final Integer width;

	private final Integer id;

	private final ArrayList<Fragment> fragmentList;

	public Charta(ArrayList<Fragment> fragmentList, Integer id) {
		this.id = id;
		this.fragmentList = fragmentList;
		String[] fileName = fragmentList.get(0).getFile().getName().split("_");
		width = Integer.valueOf(fileName[3]);
		height = Integer.valueOf(fileName[4].split("\\.")[0]);
	}

	public Charta(Integer id, Integer width, Integer height, ArrayList<Fragment> fragmentList) {
		this.id = id;
		this.width = width;
		this.height = height;
		this.fragmentList = fragmentList;
	}

	public ArrayList<Fragment> getFragmentsByPosition(Integer x, Integer y, Integer width, Integer height) throws FragmentNotFoundException {
		ArrayList<Fragment> fragmentsArray = new ArrayList<>();
		for (Fragment fragment : fragmentList) {
			if ((fragment.getX() >= (x + width))
					|| (x >= (fragment.getX() + fragment.getWidth()))
					|| (fragment.getY() >= (y + height))
					|| (y >= (fragment.getY() + fragment.getHeight()))) {
				continue;
			}
			fragmentsArray.add(fragment);
		}
		if (fragmentsArray.isEmpty()) {
			throw new FragmentNotFoundException("Fragment not found");
		}
		Collections.sort(fragmentsArray);
		return fragmentsArray;
	}

	public Integer getHeight() {
		return height;
	}

	public Integer getWidth() {
		return width;
	}

	public Integer getId() {
		return id;
	}

	public ArrayList<Fragment> getFragmentList() {
		return fragmentList;
	}

	@Override
	public String toString() {
		return "Charta{" +
				"height=" + height +
				", width=" + width +
				", id=" + id +
				", fragmentList=" + fragmentList.toString() +
				'}';
	}
}
