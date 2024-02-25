package ru.javawebinar.basejava.storage.serialization;

import ru.javawebinar.basejava.model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class DataStreamSerialization implements SerializationType {

    @Override
    public void write(Resume r, OutputStream os) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(os)) {
            dos.writeUTF(r.getUuid());
            dos.writeUTF(r.getFullName());
            Map<ContactType, String> contacts = r.getContactMap();

            writeWithException(contacts.entrySet(), dos, entry -> {
                dos.writeUTF(entry.getKey().name());
                dos.writeUTF(entry.getValue());
            });

            Map<SectionType, Section> sections = r.getSectionMap();
            writeWithException(sections.entrySet(), dos, entry -> {
                dos.writeUTF(entry.getKey().name());
                switch (entry.getKey()) {
                    case PERSONAL:
                    case OBJECTIVE:
                        dos.writeUTF(entry.getValue().toString());
                        break;
                    case ACHIEVEMENT:
                    case QUALIFICATIONS:
                        List<String> list = ((ListSection) entry.getValue()).getList();
                        writeWithException(list, dos, dos::writeUTF);
                        break;
                    case EXPERIENCE:
                    case EDUCATION:
                        writeCompanySection((CompanySection) entry.getValue(), dos);
                        break;
                }
            });
        }
    }

    @Override
    public Resume read(InputStream is) throws IOException {
        try (DataInputStream dis = new DataInputStream(is)) {
            String uuid = dis.readUTF();
            String fullName = dis.readUTF();
            Resume resume = new Resume(uuid, fullName);

            readWithException(dis, () -> resume.addContactInfo(ContactType.valueOf(dis.readUTF()), dis.readUTF()));

            readWithException(dis, () -> {
                SectionType type = SectionType.valueOf(dis.readUTF());
                switch (type) {
                    case PERSONAL:
                    case OBJECTIVE:
                        resume.addInfoAtSection(type, dis.readUTF());
                        break;
                    case ACHIEVEMENT:
                    case QUALIFICATIONS:
                        ArrayList<String> strings = new ArrayList<>();
                        readWithException(dis, () -> strings.add(dis.readUTF()));
                        resume.addInfoAtSection(type, strings.toArray(new String[0]));
                        break;
                    case EXPERIENCE:
                    case EDUCATION:
                        resume.addInfoAtSection(type, readCompanySection(dis));
                        break;
                }
            });

            return resume;
        }
    }

    private static void writeCompanySection(CompanySection companySection, DataOutputStream dos) throws IOException {
        List<Company> companies = companySection.getCompanies();
        writeWithException(companies, dos, c -> {
            dos.writeUTF(c.getCompanyName());
            String website = c.getWebsite();
            dos.writeUTF(website);

            List<Company.Period> periods = c.getPeriods();
            writeWithException(periods, dos, p -> {
                dos.writeUTF(p.getStartDate().toString());
                dos.writeUTF(p.getEndDate().toString());
                dos.writeUTF(p.getTitle());
                dos.writeUTF(p.getDescription());
            });
        });
    }

    private static Company[] readCompanySection(DataInputStream dis) throws IOException {
        ArrayList<Company> companies = new ArrayList<>();
        readWithException(dis, () -> {
            String name = dis.readUTF();

            companies.add(new Company(name, dis.readUTF()));

            List<Company.Period> periods = companies.get(companies.size() - 1).getPeriods();
            readWithException(dis, () -> {
                LocalDate startDate = LocalDate.parse(dis.readUTF());
                LocalDate endDate = LocalDate.parse(dis.readUTF());
                String title = dis.readUTF();
                String description = dis.readUTF();
                periods.add(new Company.Period(startDate, endDate, title, description));
            });
        });
        return companies.toArray(new Company[0]);
    }

    private static <T> void writeWithException(Collection<T> collection, DataOutputStream dos, CustomConsumer<? super T> action) throws IOException {
        Objects.requireNonNull(collection);
        Objects.requireNonNull(action);
        Objects.requireNonNull(dos);
        dos.writeInt(collection.size());
        for (T t : collection) {
            action.accept(t);
        }
    }

    private static void readWithException(DataInputStream dis, CustomRunnable action) throws IOException {
        Objects.requireNonNull(action);
        Objects.requireNonNull(dis);
        int size = dis.readInt();
        for (int i = 0; i < size; i++) {
            action.run();
        }
    }

    @FunctionalInterface
    private interface CustomConsumer<T> {
        void accept(T a) throws IOException;
    }

    @FunctionalInterface
    private interface CustomRunnable {
        void run() throws IOException;
    }
}