package ru.javawebinar.basejava;

import ru.javawebinar.basejava.model.Resume;
import ru.javawebinar.basejava.storage.MapResumeStorage;
import ru.javawebinar.basejava.storage.Storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Interactive test for ru.javawebinar.basejava.storage.ArrayStorage implementation
 * (just run, no need to understand)
 */
public class MainArray {
    private final static Storage ARRAY_STORAGE = new MapResumeStorage();

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Resume r;
        int i = 0;
        while (true) {
            System.out.print("Введите одну из команд - (list | size | save fullName | delete uuid | get uuid | clear | exit): ");
            String[] params = reader.readLine().trim().toLowerCase().split(" ");
            if (params.length < 1 || params.length > 3) {
                System.out.println("Неверная команда.");
                continue;
            }
            String param = null;
            if (params.length == 3 || params.length == 2) {
                param = params[1].intern();
            }
            switch (params[0]) {
                case "list":
                    printAll();
                    break;
                case "size":
                    System.out.println(ARRAY_STORAGE.size());
                    break;
                case "save":
                    r = new Resume(String.valueOf(++i), param);
                    ARRAY_STORAGE.save(r);
                    printAll();
                    break;
                case "update":
                    r = new Resume(param, params[2]);
                    ARRAY_STORAGE.update(r);
                    printAll();
                    break;
                case "delete":
                    ARRAY_STORAGE.delete(param);
                    printAll();
                    i--;
                    break;
                case "get":
                    System.out.println(ARRAY_STORAGE.get(param));
                    break;
                case "clear":
                    ARRAY_STORAGE.clear();
                    printAll();
                    i = 0;
                    break;
                case "exit":
                    return;
                default:
                    System.out.println("Неверная команда.");
                    break;
            }
        }
    }

    static void printAll() {
        List<Resume> all = ARRAY_STORAGE.getAllSorted();
        System.out.println("----------------------------");
        if (all.isEmpty()) {
            System.out.println("Empty");
        } else {
            for (Resume r : all) {
                System.out.println(r);
            }
        }
        System.out.println("----------------------------");
    }
}
