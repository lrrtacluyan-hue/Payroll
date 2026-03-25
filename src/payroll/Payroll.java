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
    
    // Replacing Magic Number to Constants makes the code Easier to read and allows to update policies in one place. 
    
    static final int OFFICIAL_SHIFT_START_MINUTES = 480; // Represent 8:00 AM in total minutes from midnight. 
    static final int GRACE_PERIOD_END_MINUTES = 490:    // Represent 8:10 AM in total minutes from midnight. 
    static final int OFFICIAL_SHIFT_END_MINUTES = 1020: // Represent 5:00 PM in total minutes from midnight. 
    static final int LUNCH_START_MINUTES = 720; // Represent 12:00 PM in total minutes from midnight. 
    static final int LUNCH_END_MINUTES = 780;   // Represent 1:00 PM in total minutes from midnight. 
    static final int LUNCH_DURATION_MINUTES = 60:  // Represent the standard 1 hour lunch break deduction. 
    
    static final double PAGIBIG_FLAT_DEDUCTION = 200.00;
    static final double PHILHEALTH_CONTRIBUTION _RATE = 0.025;

    // --- PARALLEL ARRAYS FOR EMPLOYEE DATA (NO OOP) ---
    static int[] empIds = new int[35];
    static String[] empNames = new String[35];
    static String[] empBirthdays = new String[35];
    static double[] empRates = new double[35];
    static int empCount = 0;
    
    //--- High Performance Data Structure  (NO OOP) ---
    // a matrix acting as a fast lookup table: [Employee Index}Month 1-12}{Day1-31}
    static double[] dailyHoursMatrix = new double[100][12][31]
    

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
            //
        if (!password.equals("12345") || (!username.equals("employee") && !username.equals("payroll_staff"))) {
            System.out.println("Incorrect username and/or password");
            System.exit(0); //Validate credentials. If incorrect, Immediately Terminate the program. 
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
                        System.out.println("\nExiting program per system requirements.");
                        System.exit(0); // Will Exit after displaying the details 
                    }
                } else if (choice.equals("2")) {
                    System.out.println("Terminating Program.");
                    System.exit(0);// Exit program
                } else {
                    System.out.println ("Invalid Choice. Please select 1 or 2.");
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
                    System.out.println("Terminating Program."); 
                    System.exit(0);// Termination of Program after process of Bulk Payroll 
                } else {
                    System.out.println ("Invalid Choice. Please select 1 or 2.");
                }
            }
        }
    }
    
    // Menu Helpers 
    /**
     * Displays a menu from an array of strings and ask the user for a valud integer choice. 
     * This reduces repetitive System.out.println code across different menus.  
     */
    public static int displayMenuAndGetChoice(Scanner scanner, String[]options, String promptMessage){
        System.out.println("\nOptions:");
        for (String option : options){
            System.out.println(option);
        }
        return getValidIntegerInput(scanner, promptMessage);
    }
    
   /**
    * Input validation method utilizing a try catch block 
    * This prevents the application from crashing if the user enters a non numeric character. 
    */
    
    public static int getValidIntegerInput(Scanner scanner, String promptMessage) {
        while (true) {
            System.out.print(promptMessage);
            try{
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid whole number.");
            }
        }
    }

    // --- PAYROLL STAFF MENUS ---
    public static void processPayrollMenu(Scanner scanner) {
        String [] subMenuOptions = {
            "1. One Employee",
            "2. All Employees",
            "3. Exit Program",
        };
        while (true) {
            System.out.println("\nProcess Payroll (Do not include allowances):");
            int subChoice = displayMenuAndChoice(scanner, subMenuOptions, "Choice:");
            
            if (subChoice.equals("1")) {
                int employeeId = getValidIntegerInput(scanner,"Enter Employee Number");
                int employeeindex = findEmployeeIndex(employeeid);
                
                if (index == -1) {
                    System.out.println("Employee number does not exist");
                } else {
                    displayEmployeePayroll(employeeindex);
                    System.out.println("\nPayroll processed. Exiting Program");
                    System.exit(0); // Fullfills the requirement to exit after displaying payroll.
                    
                }
            } else if (subChoice.equals("2")) {
                for (int i = 0; i < empCount; i++) {
                    displayEmployeePayroll(i);
                }
                System.out.println("\nAll payroll processed successfully. Exiting Program");
                System.exit(0); // Fullfills the requirement to exit after displaying payroll.
            } else if (subChoice.equals("3")) {
                System.out.println("Terminating Program.");
                System.exit(0);
            } else {
                System.out.println("Invalid choice. Please select a valid option.")
            }
        }
    }

    // --- CORE PAYROLL COMPUTATION ---
    public static void displayEmployeePayroll(int employeeIndex) {
        int currentEmployeeId = empIds[employeeIndex];
        double hourlyrate = empRates[employeeIndex];

        System.out.println("\n=============================================");
        System.out.println("Employee #: " + currentEmployeeId);
        System.out.println("Employee Name: " + empNames[employeeIndex]);
        System.out.println("Birthday: " + empBirthdays[employeeIndex]);

        // Iterate throught required tracking months from June (Month 6) to December ( Month 12)
        for (int currentMonth = 6; currentMonth <= 12; currentMonth++) {
            String monthName = getMonthName(currentMonth);
            // To Determine if the month ends on the 30th or 31st to set the correct second cut off date 
            int endDay = (month == 6 || month == 9 || month == 11) ? 30 : 31;

            //  Calculation of the first cut off covering Days 1 to 15. 
            double hoursFirstCutoff = getHoursForPeriod(currentEmployeeId, currentMonth, 1, 15);
            double grossFirstCutoff = hoursFirstCutoff * rate;
            
            //  Calculation of the second cut off covering Days 16 to the end of the month. 
            double hoursSecondCutoff = getHoursForPeriod(currentEmployeeId, currentMonth, 16, endDayOfMonth);
            double grossSecondCutoff = hoursSecondCutoff * rate;

            // Combine both cutoffs to determine the total monthly gross for accurate tax deduction and calculation. 
            double totalMonthlyGross = grossFirstCutoff + grossSecondCutoff;

            // Compute Deductions (using simulated PH rates based on gross)
            double sssDeduction = 0, philhealthDeduction = 0, pagibigDeduction = 0, withholdingTax = 0;
            
            // Only apply government deduction if the employee earned a salary during this specific month. 
            if (totalMonthlyGross > 0) {
                sssDeduction = computeSSS(totalMonthlyGross); // Simulated Standard SSS
                philhealthDeduction = totalMonthlyGross * PHILHEALTH_CONTRIBUTION_RATE; // Simulated PhilHealth (5% / 2)
                pagibigDeduction = PAGIBIG_FLAT_DEDUCTION; // Flat standard HDMF
                withholdingTax = computeTax(totalMonthlyGross, sssDeduction, philhealthDeduction, pagibigDeduction);
            }
            
            double totalMonthlyDeductions = sssDeduction + philhealthDeduction + pagibigDeduction + withholdingTax;

            // First Cutoff information with out any deduction 
            System.out.println("\nCutoff Date: " + monthName + " 1 to " + monthName + " 15");
            System.out.println("Total Hours Worked: " + hoursFirstCutoff);
            System.out.println("Gross Salary: " + grossFirstCutoff);
            System.out.println("Net Salary: " + grossFirstCutoff); // No deductions on 1st payout
            
            // Second Cutoff information with all computed monthly deduction 
            System.out.println("\nCutoff Date: " + monthName + " 16 to " + monthName + " " + endDayOfMonth + " (Second payout includes all deductions)");
            System.out.println("Total Hours Worked: " + hoursSecondCutoff);
            System.out.println("Gross Salary: " + grossSecondCutoff);
            System.out.println("Each Deduction");
            System.out.println("SSS: " + sssDeductions);
            System.out.println("PhilHealth: " + philhealthDeductions);
            System.out.println("Pag-IBIG: " + pagibigDeductions);
            System.out.println("Tax: " + withholdingTax);
            System.out.println("Total Deductions: " + totalMonthlyDeductions);
            System.out.println("Net Salary: " + (grossSecondCutoff - totalDeductions));
            System.out.println("---------------------------------------------");
        }
    }

    // --- HELPER LOGIC ---
    // Accurate SSS contribution base on employee bracket
    public static double computeSSS(double monthlyGross){
        // If the Gross exceeds the minimum cap, apply the highest  fixed bracket deductions 
        if (monthlyGross< 5250.0){ 
            return 250.0;}
        //If the Gross exceeds the maximum cap, apply the highest  fixed bracket deductions 
        else if (monthlyGross >= 34750.0) { 
            return 1750.0;}
        else{ 
        // Otherwise calculat the bracket step based on 500 peso increments     
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
/**
 * Calculate the total hours worked for a specific employee with a given date range 
 * By utilizing primitive array (DailyHoursMatrix), we eliminate 
 * the O(N) linear search. Instead of looping throught thousands of global attendance records,
 * it instantly access the exact employee, looping only a maximum of 16 times 
 * This make the algorithm highly scalable for  large dataset 
 */
    public static double getHoursForPeriod(int targetEmployeeId, int targetMonth, int startDay, int endDay) {
        double total = 0.0;
        int  employeeIndex = fineEmployeeIndex(targetEmployeeId);
        // Safety check if employee not found will show 0 hours 
        if (employeeIndex == -1){
            return 0.0;
            }
        // Only throught the specific dats requested for instant retrieval eg Day 1 to Day 15 
        for (int currentDay = startDay <= endDay; currentDat++){
            totalHours += dailyHoursMatrix[employeeIndex][targetMonth][currentDay]
        }
        return totalHours;
    }

    public static int findEmployeeIndex(int searchId) {
        for (int i = 0; i < empCount; i++) {
            if (empIds[i] == searchId) return i;
        }
        return -1;
    }

    public static String getMonthName(int monthNumber) {
        String[] monthNamesArray = {"", "January", "February", "March", "April", "May", "June", 
                      "July", "August", "September", "October", "November", "December"};
        return m[month];
    }

    // --- FILE READING LOGIC (NO OOP) ---

    public static void loadEmployeeDetails(String filePath) {
        String regexSplit = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Skip CVS Header
            String currentLine;
            while ((currectLine = br.readLine()) != null) {
                if (currentLine.trim().isEmpty()) continue;
                String[] data = currentLine.split(regexSplit, -1);
                if (data.length < 19) continue;

                empIds[empCount] = Integer.parseInt(rowData[0].trim());
                empNames[empCount] = rowData[2].trim() + " " + rowData[1].trim(); // First Last
                empBirthdays[empCount] = rowData[3].trim();
                
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
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                if (currentLine.trim().isEmpty()) continue;
                String[] rowData = currentLine.split(",", -1);
                if (rowData.length < 6) continue;

                if(rowData[4].isEmpty() || rowData[5].isEmpty()) continue; // Skip incomplete

                int employeeId = Integer.parseInt(rowData[0].trim());
                
                // Parse date string (handles MM/DD/YYYY or YYYY-MM-DD) reliably
                String dateStr = rowData[3].trim();
                int currentMonth = 0;
                int currentDay = 0; 
                
                if (dateStr.contains("/")) {
                    currentMonth = Integer.parseInt(dateStr.split("/")[0]);
                    currentDay = Integer.parseInt(dateStr.split("/")[1]);
                } else if (dateStr.contains("-")) {
                    currentMonth = Integer.parseInt(dateStr.split("-")[1]);
                    currentDay = Integer.parseInt(dateStr.split("-")[2]);
                }

                // Compute exact hours based on constraints
                double totalPayableHours = computeDailyHours(rowData[4].trim(), rowData[5].trim());
                int empIndex = findEmployeeIndex(EmployeeID);
                if (empindex != -1){ // it storees the hours directly into the matrix for instant retrieval
                    dailyHoursMatrix[empIndex][currentMonth][currentDay] += totalPayableHours;
                }            
             
            }
        } catch (Exception e) {
            System.out.println("Error reading Attendance file.");
        }
    }

    // Applies strict company attendance rules including Grace Periods and Lunch Deduction.
    public static double computeDailyHours(String timeInString, String timeOutString) {
        String[] inTimeParts = timeInString.split(":");
        String[] outTimeParts = timeOutString.split(":");
        
        int timeInMinutes = (Integer.parseInt(inTimeParts[0]) * 60) + Integer.parseInt(intTimeParts[1]);
        int timeOutMins = (Integer.parseInt(outTimeParts[0]) * 60) + Integer.parseInt(outTimeParts[1]);

        // Constraint: Grace period. If login is 8:10 or earlier, it counts as 8:00 AM (480 mins)
        if (timeInMinutes <= GRACE_PERIOD_END_MINUTES){
            timeInMinutes = OFFICIAL_SHIFT_START_MINUTES;
        } 
        
        // Constraint: Employees cannot accrue overtime hours prior to the official 8:00 AM shift start. 
        if (timeInMinutes < OFFICIAL_SHIFT_START_MINUTES){
            timeInMinutes = OFFICIAL_SHIFT_START_MINUTES;
        }
        
        // Constraint: Employees cannot accrue overtime hours prior to the official 5:00 PM shift end. 
        if (timeOutMinutes > OFFICIAL_SHIFT_END_MINUTES){
            timeOutMinutes = OFFICIAL_SHIFT_END_MINUTES;
        }

        int totalWorkedMinutes = timeOutMinutes - timeInMinutes;

        // Deduct 1 hour (60 mins) for lunch if they worked across the 12:00 PM - 1:00 PM window
        if (timeInMinutes <= LUNCH_START_MINUTES && timeOutMinutes >= LUNCH_END_MINUTES) {
            totalWorkedMinutes -= LUNCH_DURATION_MINUTES;
        }
       
        double totalPayableHours = totalWorkedMinutes / 60.0;
        
        // Ensure that anomalies in punch data do not result in Negative work hours being process 
        if (totalPayableHours < 0){
            totalPayableHours = 0;
        }

        return totalPayableHours;
    }
}