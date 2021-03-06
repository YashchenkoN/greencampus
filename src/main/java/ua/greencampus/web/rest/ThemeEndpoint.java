package ua.greencampus.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.annotation.*;
import ua.greencampus.dto.BaseResponse;
import ua.greencampus.dto.CourseThemeDto;
import ua.greencampus.dto.EntityResponse;
import ua.greencampus.entity.Course;
import ua.greencampus.entity.CourseTheme;
import ua.greencampus.service.CourseService;
import ua.greencampus.service.CourseThemeService;
import ua.greencampus.validator.ThemeDtoValidator;
import ua.greencampus.validator.ThemeIdValidator;

import java.util.HashMap;

/**
 * Created by Ivan Mikho on 10.04.16.
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/api/theme")
public class ThemeEndpoint {

    private ThemeIdValidator themeIdValidator;
    private ThemeDtoValidator themeDtoValidator;
    private ConversionService conversionService;
    private CourseThemeService themeService;
    private CourseService courseService;

    @Autowired
    public ThemeEndpoint(@Qualifier("themeIdValidator") ThemeIdValidator themeIdValidator,
                         @Qualifier("themeDtoValidator") ThemeDtoValidator themeDtoValidator,
                         ConversionService conversionService, CourseThemeService themeService,
                         CourseService courseService) {
        this.themeIdValidator = themeIdValidator;
        this.themeDtoValidator = themeDtoValidator;
        this.conversionService = conversionService;
        this.themeService = themeService;
        this.courseService = courseService;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> read(@PathVariable("id") Long id) {
        BindingResult bindingResult = new MapBindingResult(new HashMap<>(), "id");
        themeIdValidator.validate(id, bindingResult);
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new BaseResponse(bindingResult));
        }

        CourseThemeDto themeDto = conversionService.convert(themeService.read(id), CourseThemeDto.class);
        return ResponseEntity.ok(new EntityResponse<>(themeDto));
    }

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> create(@RequestBody CourseThemeDto themeDto,
                                               @RequestParam(name = "courseId", required = true) Long courseId,
                                               BindingResult bindingResult) {
        themeDtoValidator.validate(themeDto, bindingResult);
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new BaseResponse(bindingResult));
        }
        CourseTheme theme = conversionService.convert(themeDto, CourseTheme.class);
        Course course = courseService.read(courseId);
        course.getThemes().add(theme);
        courseService.update(course);
        themeDto = conversionService.convert(theme, CourseThemeDto.class);
        return ResponseEntity.ok(new EntityResponse<>(themeDto));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> update(@PathVariable("id") Long id, @RequestBody CourseThemeDto themeDto,
                                               BindingResult bindingResult) {
        themeDto.setId(id);
        themeDtoValidator.validate(themeDto, bindingResult);
        themeIdValidator.validate(id, bindingResult);
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new BaseResponse(bindingResult));
        }
        CourseTheme theme = conversionService.convert(themeDto, CourseTheme.class);
        theme = themeService.update(theme);
        themeDto = conversionService.convert(theme, CourseThemeDto.class);
        return ResponseEntity.ok(new EntityResponse<>(themeDto));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> delete(@PathVariable("id") Long id, BindingResult bindingResult) {
        themeIdValidator.validate(id, bindingResult);
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new BaseResponse(bindingResult));
        }
        CourseTheme theme = themeService.read(id);
        if (theme == null) {
            return ResponseEntity.badRequest().body(new BaseResponse(bindingResult));
        }
        CourseThemeDto themeDto = conversionService.convert(theme, CourseThemeDto.class);
        themeService.delete(theme);
        return ResponseEntity.ok(new EntityResponse<>(themeDto));
    }
}
