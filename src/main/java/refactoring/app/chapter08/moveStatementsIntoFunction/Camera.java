package refactoring.app.chapter08.moveStatementsIntoFunction;

import java.io.OutputStream;

public class Camera {
    public String renderPeron(OutputStream outputStream, Person person) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("<p> %s </p>", person.name));
        result.append(renderPhoto(person.photo));
        result.append(emitPhotoData(person.photo));
        return result.toString();
    }

    public String renderPhoto(Photo photo) {
        return null;
    }

    public String photoDiv(Photo photo) {
        StringBuilder result = new StringBuilder();
        result.append("<div>");
        result.append(emitPhotoData(photo));
        result.append("</div>");
        return result.toString();
    }

    public String emitPhotoData(Photo photo) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("<p> 제목: %s </p>", photo.title));
        result.append(String.format("<p> 위치: %s </p>", photo.location));
        result.append(String.format("<p> 날짜: %s </p>", photo.date.toString()));
        return result.toString();
    }
}
