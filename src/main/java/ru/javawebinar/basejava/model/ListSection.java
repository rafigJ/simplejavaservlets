package ru.javawebinar.basejava.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListSection extends Section {
    private static final long serialVersionUID = 1L;

    private final List<String> list = new ArrayList<>();

    public ListSection() {
    }

    public ListSection(String ...values) {
        list.addAll(Arrays.asList(values));
    }

    public List<String> getList() {
        return list;
    }

    @Override
    public String toString() {
        return String.join("\n", list.toArray(new String[0]));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListSection that = (ListSection) o;

        return list.equals(that.list);
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }
}
