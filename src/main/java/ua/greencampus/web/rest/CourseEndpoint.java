package ua.greencampus.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import ua.greencampus.dto.*;
import ua.greencampus.entity.Course;
import ua.greencampus.service.CourseService;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Nikolay Yashchenko
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/api/course")
public class CourseEndpoint {

    private Validator courseIdValidator;
    private Validator courseDtoValidator;
    private ConversionService conversionService;
    private CourseService courseService;

    @Autowired
    public CourseEndpoint(@Qualifier("idValidator") Validator courseIdValidator,
                          @Qualifier("courseDtoValidator") Validator courseDtoValidator,
                          ConversionService conversionService, CourseService courseService) {
        this.courseIdValidator = courseIdValidator;
        this.courseDtoValidator = courseDtoValidator;
        this.conversionService = conversionService;
        this.courseService = courseService;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getByParams(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                      @RequestParam(value = "size", defaultValue = "20", required = false) int size,
                                      @RequestParam(value = "sort", defaultValue = "", required = false) String sort,
                                      @RequestParam(value = "keywords", required = false) String keywords) {
        BindingResult bindingResult = new MapBindingResult(new HashMap<>(), "page");
        if (page < 0 || size < 0) {
            bindingResult.rejectValue("bad_param", page < 0 ? "page" : "size" + " must be > 0");
            return ResponseEntity.badRequest().body(new BaseResponse(bindingResult));
        }

        List<Course> courseList = keywords == null ?
                courseService.getByParams(page, size, sort) : courseService.search(keywords);

        List<CourseDto> courseDtos = courseList.stream()
                .map(c -> conversionService.convert(c, CourseDto.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new EntityListResponse<>(courseDtos));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> read(@PathVariable("id") Long id) {
        BindingResult bindingResult = new MapBindingResult(new HashMap<>(), "id");
        courseIdValidator.validate(id, bindingResult);
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new BaseResponse(bindingResult));
        }

        CourseWithThemesDto courseDto = conversionService.convert(courseService.readWithThemes(id), CourseWithThemesDto.class);

        return ResponseEntity.ok(new EntityResponse<>(courseDto));
    }

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> create(@RequestBody CourseDto courseDto, BindingResult bindingResult) {
        courseDtoValidator.validate(courseDto, bindingResult);
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new BaseResponse(bindingResult));
        }

        Course course = conversionService.convert(courseDto, Course.class);
        course = courseService.create(course);

        courseDto = conversionService.convert(course, CourseDto.class);

        return ResponseEntity.ok(new EntityResponse<>(courseDto));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> update(@PathVariable("id") Long id, @RequestBody CourseDto courseDto,
                                               BindingResult bindingResult) {
        courseDto.setId(id);
        courseDtoValidator.validate(courseDto, bindingResult);
        courseIdValidator.validate(id, bindingResult);
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new BaseResponse(bindingResult));
        }

        Course course = conversionService.convert(courseDto, Course.class);
        course = courseService.update(course);

        courseDto = conversionService.convert(course, CourseDto.class);

        return ResponseEntity.ok(new EntityResponse<>(courseDto));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> delete(@PathVariable("id") Long id) {
        BindingResult bindingResult = new MapBindingResult(new HashMap<>(), "id");
        courseIdValidator.validate(id, bindingResult);
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new BaseResponse(bindingResult));
        }
        Course course = courseService.read(id);
        if (course == null) {
            return ResponseEntity.badRequest().body(new BaseResponse(bindingResult));
        }

        CourseDto courseDto = conversionService.convert(course, CourseDto.class);
        courseService.delete(course);

        return ResponseEntity.ok(new EntityResponse<>(courseDto));
    }
}
