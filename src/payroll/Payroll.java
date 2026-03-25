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
    static final int GRACE_PERIOD_END_MINUTES = 490;    // Represent 8:10 AM in total minutes from midnight. 
    static final int OFFICIAL_SHIFT_END_MINUTES = 1020; // Represent 5:00 PM in total minutes from midnight. 
    static final int LUNCH_START_MINUTES = 720; // Represent 12:00 PM in total minutes from midnight. 
    static final int LUNCH_END_MINUTES = 780;   // Represent 1:00 PM in total minutes from midnight. 
    static final int LUNCH_DURATION_MINUTES = 60;  // Represent the standard 1 hour lunch break deduction. 
    
    // --- PARALLEL ARRAYS FOR EMPLOYEE DATA (NO OOP) ---
    static int[] empIds = new int[35];
    static String[] empNames = new String[35];
    static String[] empBirthdays = new String[35];
    static double[] empRates = new double[35];
    static int empCount = 0;
    
    //--- High Performance Data Structure  (NO OOP) ---
    // a matrix acting as a fast lookup table: [Employee Index}Month 1-12}{Day1-31}
    static double[][][] dailyHoursMatrix = new double[100][13][32]; // Updated from 100][12][31] ot 100/13/32
    

    // --- PARALLEL ARRAYS FOR ATTENDANCE DATA (NO OOP) ---
    // Keep for Raw record counting if needed) 
    static int[] attEmpIds = new int[5169];
    static int[] attMonths = new int[5169];
    static int[] attDays = new int[5169];
    static double[] attHoursWorked = new double[5169];
    static int attCount = 0;

    public static void main(String[] args){
        
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
        if (username.equals("employee")){
            String[] employeeMenuOptions = {
                "1. Enter your Employee Number",
                "2. Exit Program"
            };      
             
            while (true){
                int choice = displayMenuAndGetChoice(scanner, employeeMenuOptions,"Choice: ");

                if (choice == 1){
                    int employeeId = getValidIntegerInput(scanner,"Enter Employee Number: ");
                    int employeeIndex = findEmployeeIndex(employeeId);
                    
                    if (employeeIndex == -1) {
                        System.out.println("Employee number does not exist");
                    } else {
                        System.out.println("\nEmployee Number: " + empIds[employeeIndex]);
                        System.out.println("Employee Name: " + empNames[employeeIndex]);
                        System.out.println("Birthday: " + empBirthdays[employeeIndex]);
                        System.out.println("\nExiting program per system requirements.");
                        System.exit(0); // Will Exit after displaying the details 
                    }
                } else if (choice == 2){
                    System.out.println("Terminating Program.");
                    System.exit(0);// Exit program
                } else {
                    System.out.println ("Invalid Choice. Please select 1 or 2.");
                }
            }
        } else if (username.equals("payroll_staff")){
            String[] staffMenuOptions = {
                "1. Process Payroll",
                "2. Exit the program"
            };
            while (true) {
                // FIXED: Changed this to use our safe int helper instead of String scanner.nextLine()
                int choice = displayMenuAndGetChoice(scanner, staffMenuOptions, "Choice: ");

                if (choice == 1){
                    processPayrollMenu(scanner);
                }else if (choice == 2){
                    System.out.println("Terminating Program."); 
                    System.exit(0);// Termination of Program after process of Bulk Payroll 
                } else {
                    System.out.println ("Invalid Choice. Please select 1 or 2.");
                }
            }
        }
    }
    
    // Menu Helpers 
    
     //Displays a menu from an array of strings and ask the user for a valud integer choice. 
     //This reduces repetitive System.out.println code across different menus.  
     
    public static int displayMenuAndGetChoice(Scanner scanner, String[]options, String promptMessage){
        System.out.println("\nOptions:");
        for (String option : options){
            System.out.println(option);
        }
        return getValidIntegerInput(scanner, promptMessage);
    }
    
   
   //Input validation method utilizing a try catch block 
   //This prevents the application from crashing if the user enters a non numeric character. 
    
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
            int subChoice = displayMenuAndGetChoice(scanner, subMenuOptions, "Choice: ");
            
            // FIXED: Changed subchoice to subChoice (capital C) to match the variable
            if (subChoice == 1) {
                
                int employeeId = getValidIntegerInput(scanner,"Enter Employee Number: "); // Added colon for formatting
                int employeeIndex = findEmployeeIndex(employeeId);
                
                if (employeeIndex == -1) {

                    System.out.println("Employee number does not exist");
                } else {
                    displayEmployeePayroll(employeeIndex);
                    System.out.println("\nPayroll processed. Exiting Program");
                    System.exit(0); // Fullfills the requirement to exit after displaying payroll.
                    
                }
            } else if (subChoice == 2) {
                for (int i = 0; i < empCount; i++) {
                    displayEmployeePayroll(i);
                }
                System.out.println("\nAll payroll processed successfully. Exiting Program");
                System.exit(0); // Fullfills the requirement to exit after displaying payroll.
            } else if (subChoice == 3) {
                System.out.println("Terminating Program.");
                System.exit(0);
            } else {
                System.out.println("Invalid choice. Please select a valid option.");
            }
        }
    }

    // --- CORE PAYROLL COMPUTATION ---
    public static void displayEmployeePayroll(int employeeIndex) {
        int currentEmployeeId = empIds[employeeIndex];
        double hourlyRate = empRates[employeeIndex];

        System.out.println("\n=============================================");
        System.out.println("Employee #: " + currentEmployeeId);
        System.out.println("Employee Name: " + empNames[employeeIndex]);
        System.out.println("Birthday: " + empBirthdays[employeeIndex]);

        // Iterate throught required tracking months from June (Month 6) to December ( Month 12)
        for (int currentMonth = 6; currentMonth <= 12; currentMonth++) {
            String monthName = getMonthName(currentMonth);
            // To Determine if the month ends on the 30th or 31st to set the correct second cut off date 
            int endDayOfMonth = (currentMonth == 6 || currentMonth == 9 || currentMonth == 11) ? 30 : 31;

            //  Calculation of the first cut off covering Days 1 to 15. 
            double hoursFirstCutoff = getHoursForPeriod(currentEmployeeId, currentMonth, 1, 15);
            double grossFirstCutoff = hoursFirstCutoff * hourlyRate;
            
            //  Calculation of the second cut off covering Days 16 to the end of the month. 
            double hoursSecondCutoff = getHoursForPeriod(currentEmployeeId, currentMonth, 16, endDayOfMonth);
            double grossSecondCutoff = hoursSecondCutoff * hourlyRate;

            // Combine both cutoffs to determine the total monthly gross for accurate tax deduction and calculation. 
            double totalMonthlyGross = grossFirstCutoff + grossSecondCutoff;

            // Compute Deductions (using simulated PH rates based on gross)
            double sssDeduction = 0, philhealthDeduction = 0, pagibigDeduction = 0, withholdingTax = 0;
            
            // Only apply government deduction if the employee earned a salary during this specific month. 
            if (totalMonthlyGross > 0) {
                sssDeduction = computeSSS(totalMonthlyGross); // Simulated Standard SSS
                philhealthDeduction = computePhilHealth(totalMonthlyGross); // Simulated PhilHealth (5% / 2)
                pagibigDeduction = computePagIbig(totalMonthlyGross); // Flat standard HDMF
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
            System.out.println("SSS: " + sssDeduction);
            System.out.println("PhilHealth: " + philhealthDeduction);
            System.out.println("Pag-IBIG: " + pagibigDeduction);
            System.out.println("Tax: " + withholdingTax);
            System.out.println("Total Deductions: " + totalMonthlyDeductions);
            System.out.println("Net Salary: " + (grossSecondCutoff - totalMonthlyDeductions));
            System.out.println("---------------------------------------------");
        }
    }

    // --- HELPER LOGIC ---
    // Accurate SSS contribution base on employee bracket
    public static double computeSSS(double monthlyGross){
        // If the Gross exceeds the minimum cap, apply the highest  fixed bracket deductions 
        if (monthlyGross< 3250.0){ 
            return 135.0;}
        //If the Gross exceeds the maximum cap, apply the highest  fixed bracket deductions 
        else if (monthlyGross >= 24750.0) { 
            return 1125.0;}
        else{ 
        // Otherwise calculat the bracket step based on 500 peso increments     
            int mscMultiplier = (int) ((monthlyGross- 2750) / 500);
            double msc = 3000 + (mscMultiplier * 500);
            return msc * 0.045;
            
        }
            
            
    }    
    
    // Philhealt Contribution based on 3% bracket 
    public static double computePhilHealth(double monthlyGross){
        double totalPremium = 0.0;
        if (monthlyGross <= 10000.00){
            totalPremium = 300;
        } else if (monthlyGross < 60000.00){
            totalPremium = monthlyGross * 0.03;
        }else {
            totalPremium = 1800.00;
        }
        return totalPremium / 2.0;
    } 
    
    public static double computePagIbig(double monthlyGross){
        double contribution = 0.0;
        if (monthlyGross >= 1000.00 && monthlyGross <= 1500.00){
            contribution = monthlyGross * 0.01;
        } else if (monthlyGross > 1500.00){
            contribution = monthlyGross * 0.02;
        }
        return contribution;
    }

    // Standard simulated tax brackets
    public static double computeTax(double monthlyGross, double sss, double ph, double pagibig) {
        double taxable = monthlyGross - sss - ph - pagibig;
        if (taxable <= 20833.33) return 0.0;
        if (taxable <= 33333.33) return (taxable - 20833.33) * 0.20;
        if (taxable <= 66666.67) return 2500.00 + (taxable - 33333.33) * 0.25;
        if (taxable <= 166666.67) return 10833.00 + (taxable - 66666.67) * 0.30;
        if (taxable <= 666666.67) return 40833.33 + (taxable - 166666.67) * 0.32;
        return 200833.33 + (taxable - 666666.67) * 0.35;
    }

 //Calculate the total hours worked for a specific employee with a given date range 
 //By utilizing primitive array (DailyHoursMatrix), we eliminate 
 //the O(N) linear search. Instead of looping throught thousands of global attendance records,
 //it instantly access the exact employee, looping only a maximum of 16 times 
 //This make the algorithm highly scalable for  large dataset 
 
    public static double getHoursForPeriod(int targetEmployeeId, int targetMonth, int startDay, int endDay) {
        double totalHours = 0.0;
             int employeeIndex = findEmployeeIndex(targetEmployeeId);
        // Safety check if employee not found will show 0 hours 
            if (employeeIndex == -1){
            return 0.0;
            }
        // Only throught the specific dats requested for instant retrieval eg Day 1 to Day 15 
                for (int currentDay = startDay; currentDay <= endDay; currentDay++) {
            totalHours += dailyHoursMatrix[employeeIndex][targetMonth][currentDay];
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
        return monthNamesArray[monthNumber];
    }

    // --- FILE READING LOGIC (NO OOP) ---

    public static void loadEmployeeDetails(String filePath) {
        String regexSplit = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Skip CVS Header
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                if (currentLine.trim().isEmpty()) continue;
                String[] data = currentLine.split(regexSplit, -1);
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
                int empIndex = findEmployeeIndex(employeeId);
                if (empIndex != -1){ // it storees the hours directly into the matrix for instant retrieval
                    dailyHoursMatrix[empIndex][currentMonth][currentDay] += totalPayableHours;
                }            
                // (Keeping legacy arrays populated just in case raw data counting is needed elsewhere)
                attEmpIds[attCount] = employeeId;
                attMonths[attCount] = currentMonth;
                attDays[attCount] = currentDay;
                attHoursWorked[attCount] = totalPayableHours;
                attCount++;
            }
        } catch (Exception e) {
            System.out.println("Error reading Attendance file.");
        }
    }

    // Applies strict company attendance rules including Grace Periods and Lunch Deduction.
    public static double computeDailyHours(String timeInString, String timeOutString) {
        String[] inTimeParts = timeInString.split(":");
        String[] outTimeParts = timeOutString.split(":");
        
        int timeInMinutes = (Integer.parseInt(inTimeParts[0]) * 60) + Integer.parseInt(inTimeParts[1]);
        int timeOutMinutes = (Integer.parseInt(outTimeParts[0]) * 60) + Integer.parseInt(outTimeParts[1]);

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
        if (timeInMinutes <= LUNCH_START_MINUTES && timeOutMinutes >= LUNCH_END_MINUTES){
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