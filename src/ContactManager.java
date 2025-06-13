import java.util.*;

public class ContactManager {
    private static class Contact implements Comparable<Contact> {
        String name;
        String phone;
        String category;
        long timestamp;

        Contact(String name, String phone, String category) {
            this.name = name;
            this.phone = phone;
            this.category = category;
            this.timestamp = System.currentTimeMillis();
        }

        @Override
        public int compareTo(Contact other) {
            return this.name.compareTo(other.name);
        }

        @Override
        public String toString() {
            return String.format("%s - %s (%s)", name, phone, category);
        }
    }

    private final List<Contact> contacts = new ArrayList<>();
    private final Map<String, List<Contact>> categoryMap = new HashMap<>();

    public void addContact(String name, String phone, String category) {
        Contact contact = new Contact(name, phone, category);
        contacts.add(contact);
        
        // Add to category map
        categoryMap.computeIfAbsent(category, k -> new ArrayList<>()).add(contact);
    }

    public String getContactsInOrder() {
        List<Contact> sorted = new ArrayList<>(contacts);
        Collections.sort(sorted);
        return formatContacts(sorted);
    }

    public String getContactsFIFO() {
        return formatContacts(contacts);
    }

    public String getContactsLIFO() {
        List<Contact> reversed = new ArrayList<>(contacts);
        Collections.reverse(reversed);
        return formatContacts(reversed);
    }

    public String getContactsQuickSort() {
        List<Contact> sorted = new ArrayList<>(contacts);
        quickSort(sorted, 0, sorted.size() - 1);
        return formatContacts(sorted);
    }

    public String getContactsMergeSort() {
        List<Contact> sorted = new ArrayList<>(contacts);
        mergeSort(sorted, 0, sorted.size() - 1);
        return formatContacts(sorted);
    }

    public String searchContact(String searchTerm) {
        // Binary search implementation
        List<Contact> sorted = new ArrayList<>(contacts);
        Collections.sort(sorted);
        
        int left = 0;
        int right = sorted.size() - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            Contact contact = sorted.get(mid);
            
            if (contact.name.equalsIgnoreCase(searchTerm)) {
                return contact.toString();
            }
            
            if (contact.name.compareToIgnoreCase(searchTerm) < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        // If not found with exact match, try partial match
        for (Contact contact : contacts) {
            if (contact.name.toLowerCase().contains(searchTerm.toLowerCase()) ||
                contact.phone.contains(searchTerm)) {
                return contact.toString();
            }
        }
        
        return null;
    }

    public String getContactsByCategory(String category) {
        List<Contact> categoryContacts = categoryMap.getOrDefault(category, new ArrayList<>());
        return formatContacts(categoryContacts);
    }

    private String formatContacts(List<Contact> contactList) {
        if (contactList.isEmpty()) {
            return "No contacts found.";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Contact contact : contactList) {
            sb.append(contact.toString()).append("\n");
        }
        return sb.toString();
    }

    // Quick Sort implementation
    private void quickSort(List<Contact> list, int low, int high) {
        if (low < high) {
            int pi = partition(list, low, high);
            quickSort(list, low, pi - 1);
            quickSort(list, pi + 1, high);
        }
    }

    private int partition(List<Contact> list, int low, int high) {
        Contact pivot = list.get(high);
        int i = (low - 1);
        
        for (int j = low; j < high; j++) {
            if (list.get(j).compareTo(pivot) <= 0) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }

    // Merge Sort implementation
    private void mergeSort(List<Contact> list, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(list, left, mid);
            mergeSort(list, mid + 1, right);
            merge(list, left, mid, right);
        }
    }

    private void merge(List<Contact> list, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        List<Contact> L = new ArrayList<>(list.subList(left, mid + 1));
        List<Contact> R = new ArrayList<>(list.subList(mid + 1, right + 1));

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (L.get(i).compareTo(R.get(j)) <= 0) {
                list.set(k, L.get(i));
                i++;
            } else {
                list.set(k, R.get(j));
                j++;
            }
            k++;
        }

        while (i < n1) {
            list.set(k, L.get(i));
            i++;
            k++;
        }

        while (j < n2) {
            list.set(k, R.get(j));
            j++;
            k++;
        }
    }
}