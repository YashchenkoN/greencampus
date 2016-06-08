package ua.greencampus.service;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.greencampus.dao.CourseDao;
import ua.greencampus.entity.Course;
import ua.greencampus.entity.UserCourse;
import ua.greencampus.entity.UserCourseRole;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nikolay Yashchenko
 */
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseDao lectureDao;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserCourseService userCourseService;

    @Transactional
    @Override
    public Course create(Course lecture) {
        lecture = lectureDao.create(lecture);
        userCourseService.create(new UserCourse(
                userService.read(authenticationService.getLoggedInUserId()), lecture, UserCourseRole.CREATOR)
        );
        return lecture;
    }

    @Transactional(readOnly = true)
    @Override
    public Course read(Long id) {
        return lectureDao.read(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Course readWithThemes(Long id) {
        Course course = lectureDao.read(id);
        Hibernate.initialize(course.getThemes());
        return course;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Course> getByParams(int offset, int size, String sort) {
        // todo sorting
        return lectureDao.getByParams(offset, size, sort);
    }

    @Transactional
    @Override
    public Course update(Course lecture) {
        return lectureDao.update(lecture);
    }

    @Transactional
    @Override
    public void delete(Course lecture) {
        lectureDao.delete(lecture);
    }
}
