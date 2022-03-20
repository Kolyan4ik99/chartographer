package com.chartographer.controllers;

import com.chartographer.exceptions.ChartaNotFoundException;
import com.chartographer.exceptions.FragmentNotFoundException;
import com.chartographer.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.IOException;

@RestController
@Validated
public class ImageController {

	@Autowired
	ImageService imageService;

	/**
	 * Создание новой Харты. Размер не превышает в ширину 20_000 и в высоту 50_000
	 * @param width Ширина Харты
	 * @param height Высота Харты
	 * @return Код ответа 201 и уникальный идентификатор Харты
	 */
	@PostMapping("/chartas/")
	public ResponseEntity postChartas(@RequestParam @Max(20000) @Min(1) Integer width,
										@RequestParam @Max(50000) @Min(1) Integer height) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(imageService.save(width, height));
	}

	/**
	 * Обновление фрагмента Харты
	 * @param id Уникальный идентификатор обновляемой Харты
	 * @param x Начало обновляемой области по оси х
	 * @param y Начало обновляемой области по оси y
	 * @param width Ширина обновляемой области по оси х
	 * @param height Высота обновляемой области по оси х
	 * @param imageByteArray Новое изображение фрагмента
	 * @return Код ответа 200, если обновление удачное
	 * @throws ChartaNotFoundException id не верный
	 * @throws FragmentNotFoundException некоректная область
	 * @throws IOException
	 */
	@PostMapping("/chartas/{id}/")
	public ResponseEntity updatePartCharta(@PathVariable Integer id,
											@Min(0) @RequestParam Integer x,
											@Min(0) @RequestParam Integer y,
											@Min(0) @RequestParam Integer width,
											@Min(0) @RequestParam Integer height,
											@RequestBody byte[] imageByteArray)
			throws ChartaNotFoundException, FragmentNotFoundException, IOException {

		return ResponseEntity.ok()
				.body(imageService.update(imageByteArray, id, x, y, width, height));
	}

	/**
	 * Получение фрагмента Харты.
	 * @param id Уникальный идентификатор получаемой Харты
	 * @param x Начало полученной области по оси х
	 * @param y Начало полученной области по оси y
	 * @param width Ширина полученной области по оси х
	 * @param height Высота полученной области по оси х
	 * @return byte[] (массив байт)
	 * @throws ChartaNotFoundException id не верный
	 * @throws FragmentNotFoundException некоректная область
	 * @throws IOException
	 */
	@GetMapping("/chartas/{id}/")
	public ResponseEntity getPartOfCharta(@PathVariable Integer id,
											@Min(0) @RequestParam Integer x,
											@Min(0) @RequestParam Integer y,
											@Min(0) @Max(5000) @RequestParam Integer width,
											@Min(0) @Max(5000) @RequestParam Integer height)
			throws ChartaNotFoundException, FragmentNotFoundException, IOException {

		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.parseMediaType(MediaType.IMAGE_PNG_VALUE))
				.body(imageService.getImage(id, x, y, width, height));
	}

	/**
	 * Удаление Харты и все её фрагменты по id
	 * @param id Уникальный идентификатор удаляемой Харты
	 * @return Код ответа 200 если удаления удачное
	 * @throws ChartaNotFoundException id не верный
	 */
	@DeleteMapping("/chartas/{id}/")
	public ResponseEntity deleteCharta(@PathVariable Integer id) throws ChartaNotFoundException {

		imageService.delete(id);
		return ResponseEntity.ok().body("");
	}
}
