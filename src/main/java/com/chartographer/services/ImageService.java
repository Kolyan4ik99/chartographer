package com.chartographer.services;

import com.chartographer.exceptions.ChartaNotFoundException;
import com.chartographer.exceptions.FragmentNotFoundException;

import java.io.IOException;

public interface ImageService {

	Integer save(Integer width, Integer height);

	boolean update(byte[] imageBytesArray, Integer id, Integer x, Integer y, Integer width, Integer height)
			throws ChartaNotFoundException, FragmentNotFoundException, IOException;

	boolean delete(Integer id) throws ChartaNotFoundException;

	byte[] getImage(Integer id, Integer x, Integer y, Integer width, Integer height)
			throws ChartaNotFoundException, FragmentNotFoundException, IOException;
}
