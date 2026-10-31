package com.example.SchoolWebsite.repository;

import com.example.SchoolWebsite.model.FeeReceipt;
import com.example.SchoolWebsite.model.Payment;
import com.example.SchoolWebsite.model.SalarySlip;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Repository
public class PaymentRepository {

    private final JdbcTemplate jdbcTemplate;

    public PaymentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Payment> findFeePaymentsByStudentId(String studentId, String startDate, String endDate) {
        StringBuilder sql = new StringBuilder(
            "SELECT p.Transaction_ID, p.Date_Of_Payment, p.Payment_Type, " +
            "p.Receiver, p.Sender, p.Payment_Mode, p.Amount " +
            "FROM Fees f " +
            "JOIN Payments p ON f.Payment_ID = p.Transaction_ID " +
            "WHERE f.Student_ID = ? "
        );

        List<Object> params = new ArrayList<>();
        params.add(studentId);

        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append("AND p.Date_Of_Payment >= ? ");
            params.add(startDate.trim() + " 00:00:00");
        }

        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append("AND p.Date_Of_Payment <= ? ");
            params.add(endDate.trim() + " 23:59:59");
        }

        sql.append("ORDER BY p.Date_Of_Payment DESC");

        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            Payment p = new Payment();
            p.setTransactionId(rs.getString("Transaction_ID"));
            p.setDateOfPayment(rs.getString("Date_Of_Payment"));
            p.setPaymentType(rs.getString("Payment_Type"));
            p.setReceiver(rs.getString("Receiver"));
            p.setSender(rs.getString("Sender"));
            p.setPaymentMode(rs.getString("Payment_Mode"));
            p.setAmount(rs.getDouble("Amount"));
            return p;
        }, params.toArray());
    }

    public List<Payment> findFeePaymentsByStudentId(String studentId) {
        return findFeePaymentsByStudentId(studentId, null, null);
    }

    public Map<String, Object> getTransactionStats() {
        String sql = "SELECT " +
                     "COALESCE(SUM(CASE WHEN Payment_Type = 'Fee' THEN Amount ELSE 0 END), 0) AS totalFee, " +
                     "COALESCE(SUM(CASE WHEN Payment_Type = 'Salary' THEN Amount ELSE 0 END), 0) AS totalSalary, " +
                     "COUNT(*) AS totalCount, " +
                     "COUNT(CASE WHEN Payment_Type = 'Fee' THEN 1 END) AS feeCount, " +
                     "COUNT(CASE WHEN Payment_Type = 'Salary' THEN 1 END) AS salaryCount " +
                     "FROM Payments";

        return jdbcTemplate.queryForMap(sql);
    }

    public List<Payment> findTransactions(String type, String mode, String startDate, String endDate, String search, int page, int pageSize) {
        StringBuilder sql = new StringBuilder(
            "SELECT p.Transaction_ID, p.Date_Of_Payment, p.Payment_Type, p.Receiver, p.Sender, " +
            "p.Payment_Mode, p.Amount, " +
            "COALESCE(f.Student_ID, ss.Staff_ID, ts.Teacher_ID) AS Related_ID, " +
            "CASE " +
            "    WHEN f.Student_ID IS NOT NULL THEN 'Student' " +
            "    WHEN ts.Teacher_ID IS NOT NULL THEN 'Teacher' " +
            "    WHEN ss.Staff_ID IS NOT NULL THEN 'Staff' " +
            "    ELSE 'Other' " +
            "END AS Related_Role " +
            "FROM Payments p " +
            "LEFT JOIN Fees f ON p.Transaction_ID = f.Payment_ID " +
            "LEFT JOIN Staff_Salary ss ON p.Transaction_ID = ss.Payment_ID " +
            "LEFT JOIN Teacher_Salary ts ON p.Transaction_ID = ts.Payment_ID " +
            "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (type != null && !type.trim().isEmpty()) {
            sql.append("AND p.Payment_Type = ? ");
            params.add(type.trim());
        }

        if (mode != null && !mode.trim().isEmpty()) {
            sql.append("AND p.Payment_Mode = ? ");
            params.add(mode.trim());
        }

        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append("AND p.Date_Of_Payment >= ? ");
            params.add(startDate.trim() + " 00:00:00");
        }

        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append("AND p.Date_Of_Payment <= ? ");
            params.add(endDate.trim() + " 23:59:59");
        }

        if (search != null && !search.trim().isEmpty()) {
            String pattern = "%" + search.trim() + "%";
            sql.append("AND (p.Transaction_ID LIKE ? OR p.Sender LIKE ? OR p.Receiver LIKE ? OR f.Student_ID LIKE ? OR ss.Staff_ID LIKE ? OR ts.Teacher_ID LIKE ?) ");
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }

        sql.append("ORDER BY p.Date_Of_Payment DESC, p.Transaction_ID DESC ");

        int offset = Math.max(0, (page - 1) * pageSize);
        sql.append("LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(offset);

        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            Payment p = new Payment();
            p.setTransactionId(rs.getString("Transaction_ID"));
            p.setDateOfPayment(rs.getString("Date_Of_Payment"));
            p.setPaymentType(rs.getString("Payment_Type"));
            p.setReceiver(rs.getString("Receiver"));
            p.setSender(rs.getString("Sender"));
            p.setPaymentMode(rs.getString("Payment_Mode"));
            p.setAmount(rs.getDouble("Amount"));
            p.setRelatedId(rs.getString("Related_ID"));
            p.setRelatedRole(rs.getString("Related_Role"));
            return p;
        }, params.toArray());
    }

    public List<Payment> findTransactions(String type, String mode, String search, int page, int pageSize) {
        return findTransactions(type, mode, null, null, search, page, pageSize);
    }

    public int countTransactions(String type, String mode, String startDate, String endDate, String search) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) " +
            "FROM Payments p " +
            "LEFT JOIN Fees f ON p.Transaction_ID = f.Payment_ID " +
            "LEFT JOIN Staff_Salary ss ON p.Transaction_ID = ss.Payment_ID " +
            "LEFT JOIN Teacher_Salary ts ON p.Transaction_ID = ts.Payment_ID " +
            "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (type != null && !type.trim().isEmpty()) {
            sql.append("AND p.Payment_Type = ? ");
            params.add(type.trim());
        }

        if (mode != null && !mode.trim().isEmpty()) {
            sql.append("AND p.Payment_Mode = ? ");
            params.add(mode.trim());
        }

        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append("AND p.Date_Of_Payment >= ? ");
            params.add(startDate.trim() + " 00:00:00");
        }

        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append("AND p.Date_Of_Payment <= ? ");
            params.add(endDate.trim() + " 23:59:59");
        }

        if (search != null && !search.trim().isEmpty()) {
            String pattern = "%" + search.trim() + "%";
            sql.append("AND (p.Transaction_ID LIKE ? OR p.Sender LIKE ? OR p.Receiver LIKE ? OR f.Student_ID LIKE ? OR ss.Staff_ID LIKE ? OR ts.Teacher_ID LIKE ?) ");
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }

        Integer count = jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
        return count != null ? count : 0;
    }

    public int countTransactions(String type, String mode, String search) {
        return countTransactions(type, mode, null, null, search);
    }

    public List<String> getAvailablePaymentTypes() {
        String sql = "SELECT DISTINCT Payment_Type FROM Payments ORDER BY Payment_Type";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public List<String> getAvailablePaymentModes() {
        String sql = "SELECT DISTINCT Payment_Mode FROM Payments ORDER BY Payment_Mode";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    // Generate next sequential Transaction ID (e.g. TXN1375)
    public synchronized String generateNextTransactionId() {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(Transaction_ID, 4) AS UNSIGNED)), 0) + 1 FROM Payments WHERE Transaction_ID LIKE 'TXN%'";
        Integer nextVal = jdbcTemplate.queryForObject(sql, Integer.class);
        int num = (nextVal != null) ? nextVal : 1;
        return String.format("TXN%04d", num);
    }

    // Insert payment record into Payments table
    public void savePayment(String transactionId, String dateOfPayment, String paymentType, String receiver, String sender, String paymentMode, double amount) {
        String sql = "INSERT INTO Payments (Transaction_ID, Date_Of_Payment, Payment_Type, Receiver, Sender, Payment_Mode, Amount) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, transactionId, dateOfPayment, paymentType, receiver, sender, paymentMode, amount);
    }

    // Link payment with student in Fees table
    public void saveFeeLink(String paymentId, String studentId) {
        String sql = "INSERT INTO Fees (Payment_ID, Student_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql, paymentId, studentId);
    }

    // Find full fee receipt details by Transaction_ID
    public FeeReceipt findFeeReceipt(String transactionId) {
        String sql = "SELECT p.Transaction_ID, p.Date_Of_Payment, p.Payment_Type, p.Receiver, p.Sender, p.Payment_Mode, p.Amount, " +
                     "f.Student_ID, s.Roll_No, s.First_Name, s.Middle_Name, s.Last_Name, s.Standard, s.Division, s.Remaining_Fees " +
                     "FROM Payments p " +
                     "JOIN Fees f ON p.Transaction_ID = f.Payment_ID " +
                     "JOIN Student s ON f.Student_ID = s.Admission_No " +
                     "WHERE p.Transaction_ID = ?";

        List<FeeReceipt> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            FeeReceipt r = new FeeReceipt();
            r.setTransactionId(rs.getString("Transaction_ID"));
            r.setDateOfPayment(rs.getString("Date_Of_Payment"));
            r.setPaymentType(rs.getString("Payment_Type"));
            r.setReceiver(rs.getString("Receiver"));
            r.setSender(rs.getString("Sender"));
            r.setPaymentMode(rs.getString("Payment_Mode"));
            r.setAmount(rs.getDouble("Amount"));

            r.setStudentId(rs.getString("Student_ID"));
            r.setRollNo(rs.getInt("Roll_No"));
            String first = rs.getString("First_Name");
            String mid = rs.getString("Middle_Name");
            String last = rs.getString("Last_Name");
            StringBuilder sb = new StringBuilder(first != null ? first : "");
            if (mid != null && !mid.trim().isEmpty()) sb.append(" ").append(mid.trim());
            if (last != null && !last.trim().isEmpty()) sb.append(" ").append(last.trim());
            r.setStudentName(sb.toString().trim());

            r.setStandard(rs.getString("Standard"));
            r.setDivision(rs.getString("Division"));
            r.setRemainingFees(rs.getDouble("Remaining_Fees"));
            return r;
        }, transactionId);

        return list.isEmpty() ? null : list.get(0);
    }

    // Link payment with teacher in Teacher_Salary table
    public void saveTeacherSalaryLink(String paymentId, String teacherId) {
        String sql = "INSERT INTO Teacher_Salary (Payment_ID, Teacher_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql, paymentId, teacherId);
    }

    // Link payment with staff in Staff_Salary table
    public void saveStaffSalaryLink(String paymentId, String staffId) {
        String sql = "INSERT INTO Staff_Salary (Payment_ID, Staff_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql, paymentId, staffId);
    }

    // Check if salary has already been paid for a given employee in a specific month and year
    public boolean isSalaryPaidForMonth(String employeeType, String employeeId, int year, int month) {
        String sql;
        if ("Teacher".equalsIgnoreCase(employeeType)) {
            sql = "SELECT COUNT(*) FROM Payments p " +
                  "JOIN Teacher_Salary ts ON p.Transaction_ID = ts.Payment_ID " +
                  "WHERE ts.Teacher_ID = ? AND YEAR(p.Date_Of_Payment) = ? AND MONTH(p.Date_Of_Payment) = ?";
        } else {
            sql = "SELECT COUNT(*) FROM Payments p " +
                  "JOIN Staff_Salary ss ON p.Transaction_ID = ss.Payment_ID " +
                  "WHERE ss.Staff_ID = ? AND YEAR(p.Date_Of_Payment) = ? AND MONTH(p.Date_Of_Payment) = ?";
        }

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, employeeId, year, month);
        return count != null && count > 0;
    }

    // Retrieve salary slip details by Transaction_ID
    public SalarySlip findSalarySlip(String transactionId) {
        // First try Teacher_Salary
        String teacherSql = "SELECT p.Transaction_ID, p.Date_Of_Payment, p.Payment_Type, p.Receiver, p.Sender, p.Payment_Mode, p.Amount, " +
                            "YEAR(p.Date_Of_Payment) AS sal_year, MONTH(p.Date_Of_Payment) AS sal_month, " +
                            "t.Employee_ID AS employee_id, t.First_Name, t.Middle_Name, t.Last_Name, " +
                            "t.Email, t.Phone_No AS contact_number " +
                            "FROM Payments p " +
                            "JOIN Teacher_Salary ts ON p.Transaction_ID = ts.Payment_ID " +
                            "JOIN Teacher t ON ts.Teacher_ID = t.Employee_ID " +
                            "WHERE p.Transaction_ID = ?";

        List<SalarySlip> teacherList = jdbcTemplate.query(teacherSql, (rs, rowNum) -> {
            SalarySlip s = new SalarySlip();
            s.setTransactionId(rs.getString("Transaction_ID"));
            s.setDateOfPayment(rs.getString("Date_Of_Payment"));
            s.setPaymentType(rs.getString("Payment_Type"));
            s.setReceiver(rs.getString("Receiver"));
            s.setSender(rs.getString("Sender"));
            s.setPaymentMode(rs.getString("Payment_Mode"));
            s.setAmount(rs.getDouble("Amount"));

            int y = rs.getInt("sal_year");
            int m = rs.getInt("sal_month");
            s.setSalaryYear(y);
            s.setSalaryMonth(m);
            if (m >= 1 && m <= 12) {
                s.setMonthName(Month.of(m).getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            }

            s.setEmployeeId(rs.getString("employee_id"));
            s.setEmployeeType("Teacher");
            s.setDesignatedRole("Teacher");
            s.setEmail(rs.getString("Email"));
            s.setPhoneNo(rs.getString("contact_number"));

            String first = rs.getString("First_Name");
            String mid = rs.getString("Middle_Name");
            String last = rs.getString("Last_Name");
            StringBuilder sb = new StringBuilder(first != null ? first : "");
            if (mid != null && !mid.trim().isEmpty()) sb.append(" ").append(mid.trim());
            if (last != null && !last.trim().isEmpty()) sb.append(" ").append(last.trim());
            s.setEmployeeName(sb.toString().trim());

            return s;
        }, transactionId);

        if (!teacherList.isEmpty()) {
            return teacherList.get(0);
        }

        // Try Staff_Salary
        String staffSql = "SELECT p.Transaction_ID, p.Date_Of_Payment, p.Payment_Type, p.Receiver, p.Sender, p.Payment_Mode, p.Amount, " +
                          "YEAR(p.Date_Of_Payment) AS sal_year, MONTH(p.Date_Of_Payment) AS sal_month, " +
                          "s.Employee_ID AS employee_id, s.First_Name, s.Middle_Name, s.Last_Name, " +
                          "s.Designated_Role, s.Contact_Number " +
                          "FROM Payments p " +
                          "JOIN Staff_Salary ss ON p.Transaction_ID = ss.Payment_ID " +
                          "JOIN Staff s ON ss.Staff_ID = s.Employee_ID " +
                          "WHERE p.Transaction_ID = ?";

        List<SalarySlip> staffList = jdbcTemplate.query(staffSql, (rs, rowNum) -> {
            SalarySlip s = new SalarySlip();
            s.setTransactionId(rs.getString("Transaction_ID"));
            s.setDateOfPayment(rs.getString("Date_Of_Payment"));
            s.setPaymentType(rs.getString("Payment_Type"));
            s.setReceiver(rs.getString("Receiver"));
            s.setSender(rs.getString("Sender"));
            s.setPaymentMode(rs.getString("Payment_Mode"));
            s.setAmount(rs.getDouble("Amount"));

            int y = rs.getInt("sal_year");
            int m = rs.getInt("sal_month");
            s.setSalaryYear(y);
            s.setSalaryMonth(m);
            if (m >= 1 && m <= 12) {
                s.setMonthName(Month.of(m).getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            }

            s.setEmployeeId(rs.getString("employee_id"));
            s.setEmployeeType("Staff");
            s.setDesignatedRole(rs.getString("Designated_Role"));
            s.setEmail("");
            s.setPhoneNo(rs.getString("Contact_Number"));

            String first = rs.getString("First_Name");
            String mid = rs.getString("Middle_Name");
            String last = rs.getString("Last_Name");
            StringBuilder sb = new StringBuilder(first != null ? first : "");
            if (mid != null && !mid.trim().isEmpty()) sb.append(" ").append(mid.trim());
            if (last != null && !last.trim().isEmpty()) sb.append(" ").append(last.trim());
            s.setEmployeeName(sb.toString().trim());

            return s;
        }, transactionId);

        return staffList.isEmpty() ? null : staffList.get(0);
    }

    // Financial statement totals for a date range
    public Map<String, Object> getFinancialSummary(String startDate, String endDate) {
        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "COALESCE(SUM(CASE WHEN Payment_Type = 'Fee' THEN Amount ELSE 0 END), 0) AS totalFees, " +
            "COALESCE(SUM(CASE WHEN Payment_Type = 'Salary' THEN Amount ELSE 0 END), 0) AS totalSalaries, " +
            "COALESCE(SUM(CASE WHEN Payment_Type = 'Salary' AND Transaction_ID IN (SELECT Payment_ID FROM Teacher_Salary) THEN Amount ELSE 0 END), 0) AS teacherSalaries, " +
            "COALESCE(SUM(CASE WHEN Payment_Type = 'Salary' AND Transaction_ID IN (SELECT Payment_ID FROM Staff_Salary) THEN Amount ELSE 0 END), 0) AS staffSalaries " +
            "FROM Payments WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();
        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append("AND Date_Of_Payment >= ? ");
            params.add(startDate.trim() + " 00:00:00");
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append("AND Date_Of_Payment <= ? ");
            params.add(endDate.trim() + " 23:59:59");
        }

        return jdbcTemplate.queryForMap(sql.toString(), params.toArray());
    }

    // Monthly breakdown of fee collections vs salary disbursements
    public List<Map<String, Object>> getMonthlyFinancialBreakdown(String startDate, String endDate) {
        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "DATE_FORMAT(Date_Of_Payment, '%Y-%m') AS periodMonth, " +
            "COALESCE(SUM(CASE WHEN Payment_Type = 'Fee' THEN Amount ELSE 0 END), 0) AS feesCollected, " +
            "COALESCE(SUM(CASE WHEN Payment_Type = 'Salary' AND Transaction_ID IN (SELECT Payment_ID FROM Teacher_Salary) THEN Amount ELSE 0 END), 0) AS teacherSalaries, " +
            "COALESCE(SUM(CASE WHEN Payment_Type = 'Salary' AND Transaction_ID IN (SELECT Payment_ID FROM Staff_Salary) THEN Amount ELSE 0 END), 0) AS staffSalaries, " +
            "COALESCE(SUM(CASE WHEN Payment_Type = 'Salary' THEN Amount ELSE 0 END), 0) AS totalSalaries, " +
            "COALESCE(SUM(CASE WHEN Payment_Type = 'Fee' THEN Amount ELSE -Amount END), 0) AS netOperatingBalance " +
            "FROM Payments WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();
        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append("AND Date_Of_Payment >= ? ");
            params.add(startDate.trim() + " 00:00:00");
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append("AND Date_Of_Payment <= ? ");
            params.add(endDate.trim() + " 23:59:59");
        }

        sql.append("GROUP BY DATE_FORMAT(Date_Of_Payment, '%Y-%m') ORDER BY periodMonth DESC");
        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    // Full export of filtered transactions for CSV export
    public List<Payment> findAllFilteredTransactions(String type, String mode, String startDate, String endDate, String search) {
        StringBuilder sql = new StringBuilder(
            "SELECT p.Transaction_ID, p.Date_Of_Payment, p.Payment_Type, " +
            "p.Receiver, p.Sender, p.Payment_Mode, p.Amount, " +
            "CASE " +
            "  WHEN f.Student_ID IS NOT NULL THEN 'Student Fee' " +
            "  WHEN ts.Teacher_ID IS NOT NULL THEN 'Teacher Salary' " +
            "  WHEN ss.Staff_ID IS NOT NULL THEN 'Staff Salary' " +
            "  ELSE p.Payment_Type " +
            "END AS subType " +
            "FROM Payments p " +
            "LEFT JOIN Fees f ON p.Transaction_ID = f.Payment_ID " +
            "LEFT JOIN Staff_Salary ss ON p.Transaction_ID = ss.Payment_ID " +
            "LEFT JOIN Teacher_Salary ts ON p.Transaction_ID = ts.Payment_ID " +
            "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();
        if (type != null && !type.trim().isEmpty()) {
            sql.append("AND p.Payment_Type = ? ");
            params.add(type.trim());
        }
        if (mode != null && !mode.trim().isEmpty()) {
            sql.append("AND p.Payment_Mode = ? ");
            params.add(mode.trim());
        }
        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append("AND Date_Of_Payment >= ? ");
            params.add(startDate.trim() + " 00:00:00");
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append("AND Date_Of_Payment <= ? ");
            params.add(endDate.trim() + " 23:59:59");
        }
        if (search != null && !search.trim().isEmpty()) {
            String pattern = "%" + search.trim() + "%";
            sql.append("AND (p.Transaction_ID LIKE ? OR p.Sender LIKE ? OR p.Receiver LIKE ? OR f.Student_ID LIKE ? OR ss.Staff_ID LIKE ? OR ts.Teacher_ID LIKE ?) ");
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }

        sql.append("ORDER BY p.Date_Of_Payment DESC, p.Transaction_ID DESC");

        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            Payment p = new Payment();
            p.setTransactionId(rs.getString("Transaction_ID"));
            p.setDateOfPayment(rs.getString("Date_Of_Payment"));
            p.setPaymentType(rs.getString("Payment_Type"));
            p.setReceiver(rs.getString("Receiver"));
            p.setSender(rs.getString("Sender"));
            p.setPaymentMode(rs.getString("Payment_Mode"));
            p.setAmount(rs.getDouble("Amount"));
            p.setSubType(rs.getString("subType"));
            return p;
        }, params.toArray());
    }
}
