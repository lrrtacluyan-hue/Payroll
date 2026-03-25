 MotorPH Payroll System
 Project Overview
The MotorPH Payroll System is a Java-based console application designed to automate and manage employee payroll. Built to Philippine statutory standards, it processes attendance records from CSV files, calculates working hours per cutoff period, and accurately computes gross pay, standard government deductions, and net salary.

This project was developed to fulfill the milestone requirements for the MotorPH Payroll System module.

Role-Based Access Control
The application features a secure, role-based login system designed for two specific types of users:

1. Employee Portal
Employees can securely log into their accounts to view their fundamental records. Upon entering their unique Employee Number, the system displays:

Employee Number
Full Name
Date of Birth

2. Payroll Staff Portal
Authorized payroll staff possess administrative access to process and generate payroll reports. They are presented with two primary actions:

Process Single Employee: The staff inputs a specific employee number. The system retrieves their data from the CSV database, calculates their precise hours worked, applies all statutory deductions, and generates a comprehensive payroll report from June to December.
Process All Employees: The system automates the above process for the entire company database, generating mass payroll computation results for every registered employee.

Payroll Computation Logic
Working Hours Calculation
The system computes payable hours based on strict company policies using imported attendance logs:

Official Shift: 8:00 AM to 5:00 PM (Maximum 8 payable hours per day).
Grace Period: Employees logging in before 8:10 AM are not penalized for tardiness.
Total hours are dynamically calculated based on actual log-in and log-out timestamps.

Salary & Deductions
Payroll is divided into two semi-monthly cutoffs:

First Cutoff (Days 1–15): Computes Gross Salary (Total Hours Worked × Hourly Rate) with no deductions applied.
Second Cutoff (Days 16–30/31): Computes the remaining Gross Salary and applies the month's accumulated statutory deductions:

SSS Contribution
PhilHealth Contribution
Pag-IBIG Contribution
Withholding Tax

Net Salary: Calculated by subtracting the total deductions from the Gross Salary.

System Features & Tech Stack
Core Features:

Role-based login authentication.
Automated attendance-to-payroll conversion.
Accurate Philippine statutory deduction algorithms.
Dynamic CSV database integration (Employee Details & Attendance Records).
Scalable reporting (Individual or Batch processing).

Technologies Used:

Language: Java
APIs: Java File I/O, Java Time API
Data Processing: CSV Parsing
Version Control: Git & GitHub

Sample Program Output
Plaintext

Employee Number: 10002
Employee Name: Antonio Lim
Birthday: 06/19/1988

--- Month: June ---

First Cutoff: June 1 to 15
Total Hours Worked: 73.38
Gross Salary: 26,208.12
Net Salary: 26,208.12

Second Cutoff: June 16 to 30 (Includes all deductions)
Total Hours Worked: 73.90
Gross Salary: 26,392.64

SSS Deduction: 1,125.00
PhilHealth Deduction: 789.01
Pag-IBIG Deduction: 1,052.01
Withholding Tax Deduction: 7,316.94

Total Deductions: 10,282.96
Net Salary: 16,109.67
Developer Information
This project was solely developed and maintained by Ryan Tacluyan.

Key Responsibilities Included:

Translating business requirements into programmatic logic.
Implementing precise payroll and time-tracking algorithms.
Designing login authentication and File I/O operations.
Continuous testing, debugging, and code optimization.
Managing project versioning via Git and GitHub.

How to Run the Program
Prerequisites
Java Development Kit (JDK) 8 or higher.

Apache NetBeans IDE (or any preferred Java IDE).

Installation Steps
Clone this repository to your local machine:

Bash
git clone https://github.com/lrrtacluyan-hue/Payroll.git
Open the project in your Java IDE.
Important: Ensure the two required database files are placed in the main root directory of the project:
MotorPH_Employee Data - Employee Details.csv
MotorPH_Employee Data - Attendance Record.csv
Compile and run the Payroll.java file.

Project Link: https://docs.google.com/spreadsheets/d/1lmSuXnrJWzOBy0bLc7G0Z2_Mrja5Fy7UsWYpkerAGNc/edit?usp=sharing

Developer Information
Lead Developer: Ryan Tacluyan
Framework: Java SE
Architecture: Procedural / Parallel Arrays (No OOP constraint)

