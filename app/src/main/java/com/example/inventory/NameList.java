package com.example.inventory;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.storage.StorageReference;

@IgnoreExtraProperties
public class NameList{
    private String description;
    private String name;
    private String measure_unit;
    private String upload_path;
    private String numUnits;
    public NameList() {
        //this constructor is required
        }
 public NameList(String description, String name, String measure_unit, String upload_path, String numUnits) {
            this.description = description;
            this.name = name;
            this.measure_unit = measure_unit;
            this.upload_path = upload_path;
     this.numUnits = numUnits;
 }
        public String getDescription() {
            return description;
        }
        public String getName() {
            return name;
        }

    public String getMeasure_unit() {
        return measure_unit;
    }

    public String getUpload_path() {
        return upload_path;
    }

    public String getNumUnits() {
        return numUnits;
    }

    public void setNumUnits(String numUnits) {
        this.numUnits = numUnits;
    }
}