package com.chartographer.services.bmpImpl;

import com.chartographer.DemoApplication;
import com.chartographer.exceptions.ChartaNotFoundException;
import com.chartographer.exceptions.FragmentNotFoundException;
import com.chartographer.models.Charta;
import com.chartographer.models.Fragment;
import com.chartographer.services.FileService;
import com.chartographer.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

@Service
public class ImageServiceBMP implements ImageService {

	/** Максимальный размер возвращаемого изображения */
	public final static Integer MAX_IMG_SIZE = 5000;

	@Autowired
	private FileService fileServiceBMP;

	@Autowired
	private ArrayList<Charta> chartas;

	@Autowired
	volatile private Integer id = 0;

	/**
	 * Создание Харты заданного размера
	 * @param width Ширина Харты по координате x
	 * @param height Высота Харты по координате y
	 * @return ID созданной Харты
	 */
	@Override
	public Integer save(Integer width, Integer height) {
		int currentId;
		synchronized (this) {
			currentId = this.id;
			++this.id;
		}
		ArrayList<Fragment> fragments = new ArrayList<>();
		String fileName;
		for (int x = 0; x < width; x += Math.min(MAX_IMG_SIZE, width - x)) {
			for (int y = 0; y < height; y += Math.min(MAX_IMG_SIZE, height - y)) {
				fileName = currentId + "_" + x + "_" + y + "_" + width + "_" + height;
				BufferedImage bi = new BufferedImage(Math.min(MAX_IMG_SIZE, width - x),
						Math.min(MAX_IMG_SIZE, height - y),
						BufferedImage.TYPE_3BYTE_BGR);
				fileServiceBMP.save(fileName, bi);
				fragments.add(new Fragment(x, y, Math.min(MAX_IMG_SIZE, width - x),
						Math.min(MAX_IMG_SIZE, height - y),
						currentId, new File(DemoApplication.PATH + "/" + fileName + ".bmp")));
			}
		}
		chartas.add(new Charta(currentId, width, height, fragments));
		return currentId;
	}

	/**
	 * Обновление заданного отрезка Харты переданным изображением
	 * @param imageBytesArray Массив байтов изображения BMP формата
	 * @param id Харты
	 * @param x Начальное положение x относильной всей карты
	 * @param y Начальное положение y относильной всей карты
	 * @param width Ширина изображения по координате x
	 * @param height Высота изображения по координате y
	 * @return true при успешном обновлении отрезка
	 * @throws ChartaNotFoundException нет харты с заданным id
	 * @throws FragmentNotFoundException нет фрагмента заданного диапазона
	 * @throws IOException при любых других исключениях
	 */
	@Override
	public boolean update(byte[] imageBytesArray, Integer id, Integer x, Integer y, Integer width, Integer height) throws ChartaNotFoundException, FragmentNotFoundException, IOException {
		ArrayList<Fragment> fragmentArrayList = getChartaById(id).getFragmentsByPosition(x, y, width, height);
		try (ByteArrayInputStream stream = new ByteArrayInputStream(imageBytesArray)) {
			BufferedImage bufferedImage = ImageIO.read(stream);

			for (Fragment fragment : fragmentArrayList) {
				drawFragment(bufferedImage, fragment, x, y, width, height);
			}
		}
		return true;
	}

	/**
	 * Удаление харты с заданным id.
	 * @param inputId id харты
	 * @return true если удаление успешно
	 * @throws ChartaNotFoundException нет харты с заданным id
	 */
	@Override
	public boolean delete(Integer inputId) throws ChartaNotFoundException {
		Integer i = getPositionChartaInArray(inputId);
		for (Fragment fragment : this.chartas.get(i).getFragmentList()) {
			fileServiceBMP.delete(fragment.getFile().getPath());
		}
		this.chartas.remove(i);
		return true;
	}

	/**
	 * Получение изображения из Харты заданного диапазона.
	 * @param id Харты
	 * @param x Начальное положение x относильной всей карты
	 * @param y Начальное положение y относильной всей карты
	 * @param width Ширина изображения по координате x
	 * @param height Высота изображения по координате y
	 * @return Массив байтов изображения BMP формата
	 * @throws ChartaNotFoundException нет харты с заданным id
	 * @throws FragmentNotFoundException нет фрагмента заданного диапазона
	 * @throws IOException при любых других исключениях
	 */
	@Override
	public byte[] getImage(Integer id, Integer x, Integer y, Integer width, Integer height)
			throws ChartaNotFoundException, FragmentNotFoundException, IOException {
		ArrayList<Fragment> fragmentArrayList = getChartaById(id).getFragmentsByPosition(x, y, width, height);
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D graphics2D = bufferedImage.createGraphics();
		graphics2D.setColor(Color.BLACK);
		graphics2D.fillRect(0, 0, width, height);

		for (Fragment fragment : fragmentArrayList) {
			drawPart(bufferedImage, fragment, x, y, width, height);
		}

		byte[] returnBytes;
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			ImageIO.write(bufferedImage, "BMP", outputStream);
			returnBytes = outputStream.toByteArray();
		}
		return returnBytes;
	}

	private Charta getChartaById(Integer id) throws ChartaNotFoundException {
		for (Charta charta : this.chartas) {
			if (Objects.equals(charta.getId(), id)) {
				return charta;
			}
		}
		throw new ChartaNotFoundException("Charta not found");
	}

	private void drawPart(BufferedImage bufferedImage,
							Fragment fragment,
							Integer x,
							Integer y,
							Integer width,
							Integer height) throws IOException {
		int startX = Math.max(x, fragment.getX());
		int startY = Math.max(y, fragment.getY());
		int endX = Math.min(x + width, fragment.getX() + fragment.getWidth());
		int endY = Math.min(y + height, fragment.getY() + fragment.getHeight());

		BufferedImage image = ImageIO.read(fragment.getFile())
				.getSubimage(
				startX - fragment.getX(),
				startY - fragment.getY(),
				endX - startX,
				endY - startY);

		bufferedImage
				.createGraphics()
				.drawImage(image,
						startX - fragment.getX(),
						startY - fragment.getY(),
						endX - startX,
						endY - startY, null);

	}

	private void drawFragment(BufferedImage bufferedImage,
	                          Fragment fragment,
	                          Integer x,
	                          Integer y,
	                          Integer width,
	                          Integer height) throws IOException {
		int startX = Math.max(x, fragment.getX());
		int startY = Math.max(y, fragment.getY());
		int endX = Math.min(x + width, fragment.getX() + fragment.getWidth());
		int endY = Math.min(y + height, fragment.getY() + fragment.getHeight());

		BufferedImage fragmentImg = ImageIO.read(fragment.getFile());
		fragmentImg.getGraphics()
				.drawImage(bufferedImage,
						startX - fragment.getX(),
						startY - fragment.getY(),
						endX - startX,
						endY - startY, null);
		fileServiceBMP.update(fragment.getFile(), fragmentImg);
	}

	private Integer getPositionChartaInArray(Integer id) throws ChartaNotFoundException {
		for (int i = 0; i < this.chartas.size(); i++) {
			if (Objects.equals(this.chartas.get(i).getId(), id)) {
				return i;
			}
		}
		throw new ChartaNotFoundException("Charta not found");
	}
}
