package com.chartographer.services.bmpImpl;

import com.chartographer.services.ImageService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ImageServiceBMPTest {

	private final String PATH = "test-folderTorT";

	private final int MAX_IMG_SIZE = 5_000;

	@Autowired
	ImageService imageServiceBMP;

	private final File directory;

	@BeforeAll
	void initDir() {
		new File(PATH).mkdir();
		log.info("Директория {} создана", PATH);
	}

	@AfterAll
	void deleteDir() {
		new File(PATH).delete();
		log.info("Директория {} удалена", PATH);
	}

	public ImageServiceBMPTest() {
		directory = new File(PATH);
	}

	public static Stream<Arguments> testsImages() {
		return Stream.of(
				Arguments.of(500, 500, 1),
				Arguments.of(5_000, 5_000, 1),
				Arguments.of(10_000, 5_000, 2),
				Arguments.of(10_000, 10_000, 4)
		);
	}

	@SneakyThrows
	@MethodSource("testsImages")
	@DisplayName("Сохранение изображения")
	@ParameterizedTest(name = "Сохранение {2} изображений размером width = {0}, height = {1}")
	void saveImages(int width, int height, int countImages) {
		int expectedWidth = Math.min(width, MAX_IMG_SIZE);
		int expectedHeight = Math.min(height, MAX_IMG_SIZE);

		Integer idFile = imageServiceBMP.save(width, height);
		log.info("Изображение сохранилось с id {}", idFile);

		File[] files = directory.listFiles(
				file -> file.getName().startsWith(idFile + "_")
		);
		for (File file : files) {
			log.info("Создался фрагмент изображения {}", file.getPath());
		}

		Arrays.stream(files).peek(file ->
				log.info("Создался фрагмент изображения {}", file.getPath())
		);

		assertEquals(countImages,
				files.length,
				"Создался лишний файл");

		for (File file : files) {

			log.info("Проверка размера изображения {}", file.getPath());
			BufferedImage image = ImageIO.read(file);
			assertThat("Ширина изображения не корректная",
					expectedWidth,
					equalTo(image.getWidth()));

			assertThat("Высоты изображения не корректная",
					expectedHeight,
					equalTo(image.getHeight()));
		}

		imageServiceBMP.delete(idFile);
	}

	@SneakyThrows
	@MethodSource("testsImages")
	@DisplayName("Удаление изображения")
	@ParameterizedTest(name = "Удаление изображения размером width = {0}, height = {1}")
	void delete(int width, int height, int countImages) {
		Integer idFile = imageServiceBMP.save(width, height);
		log.info("Изображение сохранилось с id {}", idFile);

		File[] filesBeforeDelete = directory.listFiles(
				file -> file.getName().startsWith(idFile + "_")
		);
		for (File file : filesBeforeDelete) {
			log.info("Создался фрагмент изображения {}", file.getPath());
		}

		assertEquals(countImages,
				filesBeforeDelete.length,
				"Создался лишний файл");

		log.info("Удаления изображения");
		imageServiceBMP.delete(idFile);

		File[] filesAfterDelete = directory.listFiles(
				file -> file.getName().startsWith(idFile + "_")
		);

		assertEquals(0,
				filesAfterDelete.length,
				"Фрагменты не удалились");
	}

	@Test
	@SneakyThrows
	@DisplayName("Обновление существующего изображения")
	void updateImage() {
		int width = 5_000;
		int height = 5_000;

		Integer idFile = imageServiceBMP.save(width, height);
		log.info("Изображение сохранилось с id {}", idFile);

		BufferedImage bufferedImage = new BufferedImage(
				width,
				height,
				BufferedImage.TYPE_3BYTE_BGR);

		Color testColor = new Color(250, 0, 0);

		Graphics2D graphics2D = bufferedImage.createGraphics();
		graphics2D.setColor(testColor);
		graphics2D.fillRect(0, 0, width, height);
		log.info("Создан новый фрагмент изображения");

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		ImageIO.write(bufferedImage, "BMP", outputStream);
		byte[] returnBytes = outputStream.toByteArray();

		imageServiceBMP.update(returnBytes, idFile, 10, 10, 10, 10);
		log.info("Обновления фрагмент изображения");

		File[] files = directory.listFiles(
				file -> file.getName().startsWith(idFile + "_")
		);

		assertEquals(1,
				files.length,
				"Создался лишний файл");

		BufferedImage newImage = ImageIO.read(files[0]);

		log.info("Проверка изображения");
		assertThat("Цвет не поменялся на ожидаемый", testColor,
				equalTo(new Color(newImage.getRGB(15, 15))));

		imageServiceBMP.delete(idFile);
	}

	@Test
	@SneakyThrows
	@DisplayName("Получения изображения")
	void getImage() {
		int width = 5_000;
		int height = 5_000;

		Integer idFile = imageServiceBMP.save(width, height);
		log.info("Изображение сохранилось с id {}", idFile);

		File[] files = directory.listFiles(
				file -> file.getName().startsWith(idFile + "_")
		);
		for (File file : files) {
			log.info("Создался фрагмент изображения {}", file.getPath());
		}

		Color testColor = new Color(120, 50, 20);

		BufferedImage bufferedImage = ImageIO.read(files[0]);
		Graphics2D graphics2D = bufferedImage.createGraphics();
		graphics2D.setColor(testColor);
		graphics2D.fillRect(10, 10, 10, 10);

		ImageIO.write(bufferedImage, "BMP", files[0]);

		byte[] bytes = imageServiceBMP.getImage(idFile, 0, 0, 17, 16);

		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

		BufferedImage newImage = ImageIO.read(inputStream);

		assertThat("Цвет не поменялся на ожидаемый", testColor,
				equalTo(new Color(newImage.getRGB(15, 15))));

		imageServiceBMP.delete(idFile);
	}
}