package com.chartographer.сonfig;

import com.chartographer.DemoApplication;
import com.chartographer.exceptions.BadFolderException;
import com.chartographer.models.Charta;
import com.chartographer.models.Fragment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Configuration
public class StarterConfig {

	private ArrayList<File> files = createFiles();

	/**
	 * Находит и возвращает следующих ID, необходимый для создания Харт
	 * @param chartas Подставляется бин списка готовых Харт
	 * @return ID следующей Харты
	 */
	@Bean
	public Integer getId(ArrayList<Charta> chartas) {
		int max = 1;
		for (Charta charta : chartas) {
			if (charta.getId() > max) {
				max = charta.getId();
			}
		}
		return max + 1;
	}

	/**
	 * Бин формирует полный и готовый лист Харт, из изображений полученных в папке.
	 * @return Готовый к работе список Харт
	 * @throws BadFolderException - один из файлов некорректен для работы
	 * @throws IOException - при непредвиденных исключениях
	 */
	@Bean
	public ArrayList<Charta> Charta() throws BadFolderException, IOException {
		ArrayList<Fragment> fragments = new ArrayList<>();
		for (File file : this.files) {
			if (!file.canRead() || !file.canWrite()) {
				throw new BadFolderException("Bad folder: " + file.getName());
			}
			fragments.add(fillFragment(file));
		}
		ArrayList<Charta> chartas = new ArrayList<>();
		Set<Integer> ids = new HashSet<>();
		for (Fragment fragment : fragments) {
			ids.add(fragment.getId());
		}

		for (Integer i : ids) {
			chartas.add(new Charta(getAllFragmentsById(fragments, i), i));
		}
		return chartas;
	}

	/**
	 * Считывает все изображения, соответствующие определенному маппингу, из рабочей папки.
	 * Если папки нет, то создаёт её.
	 * @return список файлов с изображениями
	 */
	private ArrayList<File> createFiles() {
		File folderFiles = new File(DemoApplication.PATH);
		if (!folderFiles.exists()) {
			try {
				Files.createDirectories(Path.of(folderFiles.getPath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (!folderFiles.isDirectory() || !folderFiles.canWrite() || !folderFiles.canRead()) {
			try {
				throw new BadFolderException("Bad folder: " + folderFiles.getName());
			} catch (BadFolderException e) {
				e.printStackTrace();
			}
		}
		String matcher = "[0-9]{1,}_[0-9]{1,}_[0-9]{1,}_[1-9][0-9]{0,}_[1-9][0-9]{0,}.bmp";
		File[] tmpFiles = folderFiles.listFiles(f -> f.getName().matches(matcher));
		return new ArrayList<>(Arrays.asList(tmpFiles));
	}

	private ArrayList<Fragment> getAllFragmentsById(ArrayList<Fragment> fragments, Integer id) {
		ArrayList<Fragment> returnFragments = new ArrayList<>();
		for (Fragment fragment : fragments) {
			if (fragment.getId() == id) {
				returnFragments.add(fragment);
			}
		}
		return returnFragments;
	}

	private Fragment fillFragment(File file) throws IOException {
		BufferedImage image = ImageIO.read(new File(file.getPath()));
		String[] splitStr = file.getName().split("_");
		return new Fragment(
				Integer.valueOf(splitStr[1]),
				Integer.valueOf(splitStr[2]),
				image.getWidth(),
				image.getHeight(),
				Integer.valueOf(splitStr[0]),
				file);
	}
}
