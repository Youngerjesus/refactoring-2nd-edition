package refactoring.app.chapter12.replaceSuperclassWithDelegate;

import java.util.ArrayList;
import java.util.List;

public class CatalogItem {
    Long id;
    String title;
    List<String> tags;

    public CatalogItem(Long id, String title, List<String> tags) {
        this.id = id;
        this.title = title;
        this.tags = tags;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean hashTag(String tag) {
        return tags.contains(tag);
    }
}
