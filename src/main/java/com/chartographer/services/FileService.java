package com.chartographer.services;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public interface FileService {

	boolean save(String fileName, RenderedImage image);

	boolean delete(String pathName);

	boolean update(File fileName, RenderedImage image) throws IOException;
}
