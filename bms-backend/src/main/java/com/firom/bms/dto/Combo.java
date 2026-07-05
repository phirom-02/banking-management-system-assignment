package com.firom.bms.dto;

@SuppressWarnings("all")
public class Combo<T> {
    private String label;
    private String value;
    private T metadata;

    public Combo() {
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public T getMetadata() {
        return metadata;
    }

    public void setMetadata(T metadata) {
        this.metadata = metadata;
    }

    public Combo(String label, String value, T metadata) {
        this.label = label;
        this.value = value;
        this.metadata = metadata;
    }
}