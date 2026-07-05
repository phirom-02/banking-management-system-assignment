package com.geekybyte.bmsgui.model;

/**
 * Generic shape for every dropdown/combobox lookup the backend serves.
 * - label: pre-formatted display text
 * - value: the identifier to submit back (always a String — a customer id
 * or an account number, depending on which combo this came from)
 * - metadata: optional extra fields (e.g. an account's numeric id, needed
 * by endpoints that filter by id rather than account number)
 */
public class ComboOption<T> {
    private String label;
    private String value;
    private T metadata;

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

    /**
     * Safety net anywhere this is rendered without an explicit cell factory.
     */
    @Override
    public String toString() {
        return label != null ? label : String.valueOf(value);
    }
}
