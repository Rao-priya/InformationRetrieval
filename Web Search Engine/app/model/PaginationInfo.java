package model;

import java.util.ArrayList;
import java.util.List;

public class PaginationInfo {
    int pageNum;
    int startFrom;

    String urlParams;

    public String getUrlParams() {
        return urlParams;
    }

    public void setUrlParams(final String urlParams) {
        this.urlParams = urlParams;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(final int pageNum) {
        this.pageNum = pageNum;
    }

    public int getStartFrom() {
        return startFrom;
    }

    public void setStartFrom(final int startFrom) {
        this.startFrom = startFrom;
    }

    public static List<PaginationInfo> calculatePagination(int currentPage, int currentFrom, int nextFrom, long totalResults){
        //Calculate previous starts
        ArrayList<PaginationInfo> paginationInfos = new ArrayList<>();

        for(int i =1; i < currentPage; i++){
            int rowsPerPage = currentFrom/ (currentPage -1);
            final PaginationInfo paginationInfo = new PaginationInfo();
            paginationInfo.setPageNum(i);
            paginationInfo.setStartFrom( (i-1) * rowsPerPage);
            paginationInfos.add(paginationInfo);
        }

        final PaginationInfo paginationInfo = new PaginationInfo();
        paginationInfo.setPageNum(currentPage);
        paginationInfo.setStartFrom(currentFrom);
        paginationInfos.add(paginationInfo);

        int remainingPages = (int)(totalResults - nextFrom)/10;
        remainingPages += (totalResults - nextFrom)%10 > 0 ? 1 :0;
        for(int i = 0; i< remainingPages; i++){
            final PaginationInfo paginationInfoLocal = new PaginationInfo();
            paginationInfoLocal.setPageNum(currentPage + 1 + i );
            paginationInfoLocal.setStartFrom( nextFrom + (10 *i ));
            paginationInfos.add(paginationInfoLocal);
        }

        return paginationInfos;
    }

    public static void main(String[] args) {
        final List<PaginationInfo> paginationInfos = PaginationInfo.calculatePagination(0, 0, 11, 40);
    }
}
