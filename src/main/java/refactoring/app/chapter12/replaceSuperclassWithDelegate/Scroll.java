package refactoring.app.chapter12.replaceSuperclassWithDelegate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Scroll {
    Long id;
    CatalogItem catalogItem;
    LocalDateTime lastCleaned;

    public Scroll(Long id, LocalDateTime dateLastCleaned, CatalogRepository catalogRepository, Long catalogId) {
        this.id = id;
        catalogItem = catalogRepository.get(catalogId);
        lastCleaned = dateLastCleaned;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return catalogItem.getTitle();
    }

    public boolean hashTag(String tag) {
        return catalogItem.hashTag(tag);
    }

    public boolean needsCleaning(LocalDateTime targetDate) {
        int threshold = hashTag("revered") ? 700 : 1500;
        return daysSinceLastCleaning(targetDate) > threshold;
    }

    private long daysSinceLastCleaning(LocalDateTime targetDate) {
        return lastCleaned.until(targetDate, ChronoUnit.DAYS);
    }
}
