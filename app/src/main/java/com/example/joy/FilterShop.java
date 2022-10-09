package com.example.joy;

import android.widget.Filter;

import com.example.joy.adapter.AdapterShop;
import com.example.joy.model.ModelShop;

import java.util.ArrayList;

public class FilterShop extends Filter {

    private AdapterShop adapter;
    private ArrayList<ModelShop> filterList;

    public FilterShop(AdapterShop adapter, ArrayList<ModelShop> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //validate data for search query
        if(constraint != null && constraint.length()>0){
            //search field not empty

            //change to upper case to make case insensitive
            constraint = constraint.toString().toUpperCase();
            //store our filtered list
            ArrayList<ModelShop> filteredModels = new ArrayList<>();
            for (int i=0; i < filterList.size(); i++){
                //check search by title and category
                if(filterList.get(i).getShopName().toUpperCase().contains(constraint)){
                    //filtered data in list
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else{
            //search field empty
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.shopsList = (ArrayList<ModelShop>) results.values;
        //refresh adepter
        adapter.notifyDataSetChanged();
    }
}
