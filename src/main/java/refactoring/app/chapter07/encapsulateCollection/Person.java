package refactoring.app.chapter07.encapsulateCollection;

import java.util.ArrayList;
import java.util.List;

public class Person {
    protected String name;
    protected List<Course> courses = new ArrayList<>();

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Course> getCourses() {
        return new ArrayList<>(courses);
    }

    public void addCourse(Course course) {
        this.courses.add(course);
    }

    public void removeCourse(Course course) {
        this.courses.remove(course);
    }

    public void removeCourse(int index) {
        try {
            this.courses.remove(index);
        } catch (IndexOutOfBoundsException e) {
            throw e;
        }
    }
}
