package com.chartographer.services.bmpImpl;

import com.chartographer.services.FileService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileServiceBMPTest {

	FileService fileServiceBMP;

	private final String PATH;

	private File testFile;

	public FileServiceBMPTest(@Autowired FileServiceBMP fileServiceBMP) {
		this.fileServiceBMP = fileServiceBMP;
		PATH = fileServiceBMP.getPath();
	}

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

	@BeforeEach
	void createFile() {
		String fileName = RandomString.make(5);
		BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR);
		fileServiceBMP.save(fileName, bufferedImage);

		testFile = new File(PATH + "/" + fileName + ".bmp");
		log.info("Создан тестовый файл {}", testFile.getPath());
		assertTrue(testFile.exists(), "Файл не найден");
	}

	@Test
	@DisplayName("Сохранение файла")
	void saveFileTest() {
		assertTrue(testFile.exists(), "Файл не создался");

		testFile.delete();
		log.info("Тестовый файл удален {}", testFile.getPath());
	}

	@Test
	@DisplayName("Удаление файла")
	void deleteFileTest() {

		fileServiceBMP.delete(testFile.getPath());
		log.info("Тестовый файл удален {}", testFile.getPath());

		testFile = reDefineFile(testFile);

		assertFalse(testFile.exists(), "Файл не удалился");
	}

	@Test
	@SneakyThrows
	@DisplayName("Обновление файла")
	void updateFileTest() {
		Color testColor = new Color(250, 0, 0);

		BufferedImage image = ImageIO.read(testFile);
		Graphics2D graphics2D = image.createGraphics();
		graphics2D.setColor(testColor);
		graphics2D.fillRect(10, 10, 10, 10);

		log.info("Меняем цвет изображения на красный {}", testColor);
		fileServiceBMP.update(testFile, image);

		BufferedImage newImage = ImageIO.read(testFile);

		assertThat("Цвет не поменялся на ожидаемый", testColor,
				equalTo(new Color(newImage.getRGB(15, 15))));

		testFile.delete();
		log.info("Тестовый файл удален {}", testFile.getPath());
	}

	File reDefineFile(File file) {
		return new File(file.getPath());
	}
}