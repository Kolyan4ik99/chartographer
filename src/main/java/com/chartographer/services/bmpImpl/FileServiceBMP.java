package com.chartographer.services.bmpImpl;

import com.chartographer.DemoApplication;
import com.chartographer.services.FileService;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

@Service
public class FileServiceBMP implements FileService {

	public String path = DemoApplication.PATH;

	public String getPath() {
		return path;
	}

	/**
	 * Создание заданного изображения/файла формата BMP, с заданным названием
	 * @param fileName Название файла
	 * @param image Изображение
	 * @return true если изображение/файл успешно создан
	 */
	@Override
	public boolean save(String fileName, RenderedImage image) {
		File bmpFile = new File(path + "/" + fileName + ".bmp");
		try {
			bmpFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		try {
			ImageIO.write(image, "BMP", bmpFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Удаление изображения/файла с заданным названием
	 * @param pathName Название файла
	 * @return true если изображение/файл успешно удален
	 */
	@Override
	public boolean delete(String pathName) {
		File bmpFile = new File(pathName);
		bmpFile.delete();
		return true;
	}

	/**
	 * Обновление существущего изображения/файла на заданное изображение с заданным названием изображения/файла
	 * @param fileName Название файла
	 * @param image Изображение
	 * @return true если изображение/файл успешно удалён
	 * @throws IOException
	 */
	@Override
	public boolean update(File fileName, RenderedImage image) throws IOException {
		ImageIO.write(image, "BMP", fileName);
		return true;
	}

}
