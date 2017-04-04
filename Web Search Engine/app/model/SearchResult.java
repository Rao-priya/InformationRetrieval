package model;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {
    List<Place> places = new ArrayList<>();
    int currentPage = 1;
    int nextFrom = 0;
    long totalHits = -1;
    List<PaginationInfo> paginationInfos = new ArrayList<>();

    public List<PaginationInfo> getPaginationInfos() {
        return paginationInfos;
    }

    public void setPaginationInfos(final List<PaginationInfo> paginationInfos) {
        this.paginationInfos = paginationInfos;
    }

    public void setPaginationInfos(final PaginationInfo paginationInfo) {
        this.paginationInfos.add(paginationInfo);
    }

    public SearchResult() {
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(final int currentPage) {
        this.currentPage = currentPage;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public int getNextFrom() {
        return nextFrom;
    }

    public long getTotalHits() {
        return totalHits;
    }

    public SearchResult(List<Place> places, int nextFrom, long totalHits) {
        this.places = places;
        this.nextFrom = nextFrom;
        this.totalHits = totalHits;
    }
}
