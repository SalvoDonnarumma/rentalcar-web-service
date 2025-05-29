package com.xantrix.webapp.utils;

import com.xantrix.webapp.dtos.PagingData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Paging {

    //Metodo di creazione classi Pages
    public List<PagingData> setPages(int page, long numRecords){

        List<PagingData> pages = new ArrayList<>();

        System.out.println("Numero di pagina: " + page);
        System.out.println("Numero di records: " + numRecords);

        int recForPage = 10;
        int min = 1;
        int max = 5;

        page = (page == 0) ? 1: page;

        if(pages != null)
            pages.clear();

        int group = (int) Math.ceil((double)page/5);

        max = group * 5;
        min = (max - 5 == 0) ? 1 : max - 4;

        int maxPages = (int) Math.ceil((double)numRecords/recForPage);

        for(int i = min; i <= Math.min(max, maxPages); i++){
            boolean isSelected = (i == page);
            pages.add(new PagingData(i, isSelected));
        }

        return pages;
    }
}
