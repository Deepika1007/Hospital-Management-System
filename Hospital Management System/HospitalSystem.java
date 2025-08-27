package jdbc;

import java.sql.*;
import java.util.Scanner;

public class HospitalSystem {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/hms";
    private static final String USER = "root";
    private static final String PASSWORD = "Deepika@07";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            System.out.println("Connected to Hospital DB!");

            while (true) {
                System.out.println("\n===== MENU =====");
                System.out.println("1. Add Doctor");
                System.out.println("2. Add Patient");
                System.out.println("3. Schedule Appointment");
                System.out.println("4. Admit Patient");
                System.out.println("5. Discharge & Generate Bill");
                System.out.println("6. View Bills");
                System.out.println("7. Exit");
                System.out.print("Select: ");
                int choice = sc.nextInt();

                switch (choice) {
                    case 1 -> insertDoctor(sc, conn);
                    case 2 -> insertPatient(sc, conn);
                    case 3 -> scheduleAppointment(sc, conn);
                    case 4 -> admitPatient(sc, conn);
                    case 5 -> dischargeAndBill(sc, conn);
                    case 6 -> viewBills(conn);
                    case 7 -> {
                        System.out.println("Exit Statement!");
                        return;
                    }
                    default -> System.out.println("Invalid choice!");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void insertDoctor(Scanner sc, Connection conn) throws SQLException {
        System.out.print("Doctor ID: ");
        int Id = sc.nextInt(); sc.nextLine();
        System.out.print("Name: ");
        String Name = sc.nextLine();
        System.out.print("Speciality: ");
        String Speciality = sc.nextLine();

        String sql = "INSERT INTO doctor (Id, Name, Speciality) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Id);
            stmt.setString(2, Name);
            stmt.setString(3, Speciality);
            stmt.executeUpdate();
            System.out.println("Doctor added.");
        }
    }

    static void insertPatient(Scanner sc, Connection conn) throws SQLException {
        System.out.print("Patient ID: ");
        int id = sc.nextInt(); sc.nextLine();
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Age: ");
        int age = sc.nextInt(); sc.nextLine();
        System.out.print("Gender: ");
        String gender = sc.nextLine();
        System.out.print("Contact: ");
        String contact = sc.nextLine();

        String sql = "INSERT INTO patient (p_id, p_Name, p_Age, p_Gender, p_Contact) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.setInt(3, age);
            stmt.setString(4, gender);
            stmt.setString(5, contact);
            stmt.executeUpdate();
            System.out.println("Patient added.");
        }
    }

    static void scheduleAppointment(Scanner sc, Connection conn) throws SQLException {
        System.out.print("Appointment ID: ");
        int id = sc.nextInt();
        System.out.print("Patient ID: ");
        int pid = sc.nextInt();
        System.out.print("Doctor ID: ");
        int did = sc.nextInt(); sc.nextLine();
        System.out.print("Start Time (YYYY-MM-DD HH:MM:SS): ");
        String startTs = sc.nextLine();
        System.out.print("Status (Scheduled/Completed): ");
        String status = sc.nextLine();

        String sql = "INSERT INTO appointment (a_id, patient_id, doctor_id, start_ts, Status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setInt(2, pid);
            stmt.setInt(3, did);
            stmt.setString(4, startTs);
            stmt.setString(5, status);
            stmt.executeUpdate();
            System.out.println("Appointment scheduled.");
        }
    }

    static void admitPatient(Scanner sc, Connection conn) throws SQLException {
        System.out.print("Admission ID: ");
        int id = sc.nextInt();
        System.out.print("Patient ID: ");
        int pid = sc.nextInt(); sc.nextLine();
        System.out.print("Ward: ");
        String ward = sc.nextLine();
        System.out.print("Admit Time (YYYY-MM-DD HH:MM:SS): ");
        String admitTs = sc.nextLine();

        String sql = "INSERT INTO admission (ad_id, patient_id, Ward, admit_ts) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setInt(2, pid);
            stmt.setString(3, ward);
            stmt.setString(4, admitTs);
            stmt.executeUpdate();
            System.out.println("Patient admitted.");
        }
    }

    static void dischargeAndBill(Scanner sc, Connection conn) throws SQLException {
        System.out.print("Admission ID: ");
        int admissionId = sc.nextInt(); sc.nextLine();
        System.out.print("Discharge Time (YYYY-MM-DD HH:MM:SS): ");
        String dischargeTs = sc.nextLine();

        String updateSql = "UPDATE admission SET discharge_ts = ? WHERE ad_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            stmt.setString(1, dischargeTs);
            stmt.setInt(2, admissionId);
            stmt.executeUpdate();
            System.out.println("Patient discharged.");
        }

        int patientId = -1;
        try (PreparedStatement stmt = conn.prepareStatement("SELECT patient_id FROM admission WHERE ad_id = ?")) {
            stmt.setInt(1, admissionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                patientId = rs.getInt("patient_id");
            } else {
                System.out.println("Admission ID not found. Aborting billing.");
                return;
            }
        }

        System.out.print("Total Bill Amount: ");
        double total = sc.nextDouble();
        System.out.print("Amount Paid: ");
        double paid = sc.nextDouble(); sc.nextLine();
        System.out.print("Payment Status (Paid/Unpaid/Partial): ");
        String status = sc.nextLine();
        
        int billId=-1;
        String billSql = "INSERT INTO bill (b_id, patient_id, Total, Paid, Status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(billSql,Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, admissionId); 
            stmt.setInt(2, patientId);
            stmt.setDouble(3, total);
            stmt.setDouble(4, paid);
            stmt.setString(5, status);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    billId = rs.getInt(1);
                    System.out.println("Bill created with ID: " + billId);
                } else {
                    System.out.println("Failed to retrieve generated bill ID.");
                    return;
                }
            }
        }

        System.out.print("Number of bill items: ");
        int count = sc.nextInt(); sc.nextLine();
        for (int i = 1; i <= count; i++) {
            System.out.print("Item Description "+i+": ");
            String desc = sc.nextLine();
            System.out.print("Amount: ");
            double amount = sc.nextDouble(); sc.nextLine();

            String itemSql = "INSERT INTO bill_item (bill_id, Description, amount) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(itemSql)) {
            	stmt.setInt(1, billId);
            	stmt.setString(2, desc);
            	stmt.setDouble(3, amount);
                stmt.executeUpdate();
            }
        }

        System.out.println("Bill items added.");
    }

    static void viewBills(Connection conn) throws SQLException {
        String sql = "SELECT * FROM bill";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n===== Bills =====");
            while (rs.next()) {
                System.out.printf("Bill ID: %d | Patient ID: %d | Total: %.2f | Paid: %.2f | Status: %s%n",
                		rs.getInt("b_id"),
                		rs.getInt("patient_id"),
                		rs.getDouble("Total"),
                		rs.getDouble("Paid"),
                		rs.getString("Status"));
            }
        }
    }
}
