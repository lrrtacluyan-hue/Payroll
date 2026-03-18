/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package payroll;

/**
 *
 * @author Ryan
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

public class Payroll {

    // --- PARALLEL ARRAYS FOR EMPLOYEE DATA (NO OOP) ---
    static int[] empIds = new int[35];
    static String[] empNames = new String[35];
    static String[] empBirthdays = new String[35];
    static double[] empRates = new double[35];
    static int empCount = 0;

    // --- PARALLEL ARRAYS FOR ATTENDANCE DATA (NO OOP) ---
    static int[] attEmpIds = new int[5169];
    static int[] attMonths = new int[5169];
    static int[] attDays = new int[5169];
    static double[] attHoursWorked = new double[5169];
    static int attCount = 0;

    public static void main(String[] args) {
        
        // Load data from files into our parallel arrays
        loadEmployeeDetails("MotorPH_Employee Data - Employee Details.csv");
        loadAttendanceRecords("MotorPH_Employee Data - Attendance Record.csv");

        Scanner scanner = new Scanner(System.in);

        // --- AUTHENTICATION ---
        System.out.println("--- MOTORPH PAYROLL SYSTEM ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (!password.equals("12345") || (!username.equals("employee") && !username.equals("payroll_staff"))) {
            System.out.println("Incorrect username and/or password");
            return; // Terminate program
        }

        // --- ROLE-BASED MENUS ---
        if (username.equals("employee")) {
            while (true) {
                System.out.println("\nOptions:");
                System.out.println("1. Enter your employee number");
                System.out.println("2. Exit the program");
                System.out.print("Choice: ");
                String choice = scanner.nextLine();

                if (choice.equals("1")) {
                    System.out.print("Enter Employee Number: ");
                    int id = Integer.parseInt(scanner.nextLine());
                    int index = findEmployeeIndex(id);
                    
                    if (index == -1) {
                        System.out.println("Employee number does not exist");
                    } else {
                        System.out.println("\nEmployee Number: " + empIds[index]);
                        System.out.println("Employee Name: " + empNames[index]);
                        System.out.println("Birthday: " + empBirthdays[index]);
                    }
                } else if (choice.equals("2")) {
                    break; // Exit program
                }
            }
        } else if (username.equals("payroll_staff")) {
            while (true) {
                System.out.println("\nOptions:");
                System.out.println("1. Process Payroll");
                System.out.println("2. Exit the program");
                System.out.print("Choice: ");
                String choice = scanner.nextLine();

                if (choice.equals("1")) {
                    processPayrollMenu(scanner);
                } else if (choice.equals("2")) {
                    break; // Exit program
                }
            }
        }
    }

    // --- PAYROLL STAFF MENUS ---
    public static void processPayrollMenu(Scanner scanner) {
        while (true) {
            System.out.println("\nProcess Payroll (Do not include allowances):");
            System.out.println("1. One employee");
            System.out.println("2. All employees");
            System.out.println("3. Exit the program");
            System.out.print("Choice: ");
            String subChoice = scanner.nextLine();

            if (subChoice.equals("1")) {
                System.out.print("Enter the employee number: ");
                int id = Integer.parseInt(scanner.nextLine());
                int index = findEmployeeIndex(id);
                if (index == -1) {
                    System.out.println("Employee number does not exist");
                } else {
                    displayEmployeePayroll(index);
                }
            } else if (subChoice.equals("2")) {
                for (int i = 0; i < empCount; i++) {
                    displayEmployeePayroll(i);
                }
            } else if (subChoice.equals("3")) {
                break;
            }
        }
    }

    // --- CORE PAYROLL COMPUTATION ---
    public static void displayEmployeePayroll(int index) {
        int id = empIds[index];
        double rate = empRates[index];

        System.out.println("\n=============================================");
        System.out.println("Employee #: " + id);
        System.out.println("Employee Name: " + empNames[index]);
        System.out.println("Birthday: " + empBirthdays[index]);

        // Loop through June (Month 6) to December (Month 12)
        for (int month = 6; month <= 12; month++) {
            String monthName = getMonthName(month);
            int endDay = (month == 6 || month == 9 || month == 11) ? 30 : 31;

            // 1st Cutoff Data
            double hours1 = getHoursForPeriod(id, month, 1, 15);
            double gross1 = hours1 * rate;
            
            // 2nd Cutoff Data
            double hours2 = getHoursForPeriod(id, month, 16, endDay);
            double gross2 = hours2 * rate;

            // Add 1st and 2nd cutoff amounts first before computing deductions
            double monthlyGross = gross1 + gross2;

            // Compute Deductions (using simulated PH rates based on gross)
            double sss = 0, philhealth = 0, pagibig = 0, tax = 0;
            
            if (monthlyGross > 0) {
                sss = computeSSS(monthlyGross); // Simulated Standard SSS
                philhealth = monthlyGross * 0.025; // Simulated PhilHealth (5% / 2)
                pagibig = 200.0; // Flat standard HDMF
                tax = computeTax(monthlyGross, sss, philhealth, pagibig);
            }
            
            double totalDeductions = sss + philhealth + pagibig + tax;

            // Output format explicitly exactly as requested
            System.out.println("\nCutoff Date: " + monthName + " 1 to " + monthName + " 15");
            System.out.println("Total Hours Worked: " + hours1);
            System.out.println("Gross Salary: " + gross1);
            System.out.println("Net Salary: " + gross1); // No deductions on 1st payout

            System.out.println("\nCutoff Date: " + monthName + " 16 to " + monthName + " " + endDay + " (Second payout includes all deductions)");
            System.out.println("Total Hours Worked: " + hours2);
            System.out.println("Gross Salary: " + gross2);
            System.out.println("Each Deduction");
            System.out.println("SSS: " + sss);
            System.out.println("PhilHealth: " + philhealth);
            System.out.println("Pag-IBIG: " + pagibig);
            System.out.println("Tax: " + tax);
            System.out.println("Total Deductions: " + totalDeductions);
            System.out.println("Net Salary: " + (gross2 - totalDeductions));
            System.out.println("---------------------------------------------");
        }
    }

    // --- HELPER LOGIC ---
    // Accurate SSS contribution base on employee bracket
    public static double computeSSS(double monthlyGross){
        if (monthlyGross< 5250.0){ 
            return 250.0;}
        else if (monthlyGross >= 34750.0) { 
            return 1750.0;}
        else{ 
            
            int mscMultiplier = (int) ((monthlyGross- 4750) / 500);
            double msc = 5000 + (mscMultiplier * 500);
            return msc * 0.05;
            
        }
            
            
    }    

    // Standard simulated tax brackets
    public static double computeTax(double monthlyGross, double sss, double ph, double pagibig) {
        double taxable = monthlyGross - sss - ph - pagibig;
        if (taxable <= 20833.33) return 0.0;
        if (taxable <= 33333.33) return (taxable - 20833.33) * 0.15;
        if (taxable <= 66666.67) return 1875.0 + (taxable - 33333.33) * 0.20;
        if (taxable <= 166666.67) return 8541.67 + (taxable - 66666.67) * 0.25;
        if (taxable <= 666666.67) return 33541.67 + (taxable - 166666.67) * 0.30;
        return 183541.67 + (taxable - 666666.67) * 0.35;
    }

    public static double getHoursForPeriod(int id, int month, int startDay, int endDay) {
        double total = 0.0;
        for (int i = 0; i < attCount; i++) {
            if (attEmpIds[i] == id && attMonths[i] == month && attDays[i] >= startDay && attDays[i] <= endDay) {
                total += attHoursWorked[i];
            }
        }
        return total;
    }

    public static int findEmployeeIndex(int id) {
        for (int i = 0; i < empCount; i++) {
            if (empIds[i] == id) return i;
        }
        return -1;
    }

    public static String getMonthName(int month) {
        String[] m = {"", "January", "February", "March", "April", "May", "June", 
                      "July", "August", "September", "October", "November", "December"};
        return m[month];
    }

    // --- FILE READING LOGIC (NO OOP) ---

    public static void loadEmployeeDetails(String filePath) {
        String regexSplit = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Skip Header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(regexSplit, -1);
                if (data.length < 19) continue;

                empIds[empCount] = Integer.parseInt(data[0].trim());
                empNames[empCount] = data[2].trim() + " " + data[1].trim(); // First Last
                empBirthdays[empCount] = data[3].trim();
                
                String rateStr = data[18].replace("\"", "").replace(",", "").trim();
                empRates[empCount] = Double.parseDouble(rateStr);
                
                empCount++;
            }
        } catch (Exception e) {
            System.out.println("Error reading Employee file.");
        }
    }

    public static void loadAttendanceRecords(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Skip Header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",", -1);
                if (data.length < 6) continue;

                if(data[4].isEmpty() || data[5].isEmpty()) continue; // Skip incomplete

                attEmpIds[attCount] = Integer.parseInt(data[0].trim());
                
                // Parse date string (handles MM/DD/YYYY or YYYY-MM-DD)
                String dateStr = data[3].trim();
                if (dateStr.contains("/")) {
                    attMonths[attCount] = Integer.parseInt(dateStr.split("/")[0]);
                    attDays[attCount] = Integer.parseInt(dateStr.split("/")[1]);
                } else if (dateStr.contains("-")) {
                    attMonths[attCount] = Integer.parseInt(dateStr.split("-")[1]);
                    attDays[attCount] = Integer.parseInt(dateStr.split("-")[2]);
                }

                // Compute exact hours based on constraints
                attHoursWorked[attCount] = computeDailyHours(data[4].trim(), data[5].trim());
                attCount++;
            }
        } catch (Exception e) {
            System.out.println("Error reading Attendance file.");
        }
    }

    // Applies strictly rules 4a, 4b, 4c, and 4d
    public static double computeDailyHours(String timeIn, String timeOut) {
        String[] inParts = timeIn.split(":");
        String[] outParts = timeOut.split(":");
        
        int inMins = (Integer.parseInt(inParts[0]) * 60) + Integer.parseInt(inParts[1]);
        int outMins = (Integer.parseInt(outParts[0]) * 60) + Integer.parseInt(outParts[1]);

        // Constraint: Grace period. If login is 8:10 or earlier, it counts as 8:00 AM (480 mins)
        if (inMins <= 490) inMins = 480; 
        
        // Constraint: Cannot login before 8:00 AM.
        if (inMins < 480) inMins = 480;
        
        // Constraint: Cannot earn hours past 5:00 PM (1020 mins)
        if (outMins > 1020) outMins = 1020;

        int totalMins = outMins - inMins;

        // Deduct 1 hour (60 mins) for lunch if they worked across the 12:00 PM - 1:00 PM window
        if (inMins <= 720 && outMins >= 780) {
            totalMins -= 60;
        }

        double hours = totalMins / 60.0;
        if (hours < 0) hours = 0;

        return hours;
    }
}