package com.mycompany.motorphpayroll;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class MotorPHPayroll {

    
    static final String EmployeeFile = "resources/MotorPH_Employee Data - Employee Details.csv";
    static final String AttendanceFile = "resources/MotorPH_Employee Data - Attendance Record.csv";
    static final String CSVSplit = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    
    static final double[] SSSranges = {
        3250, 3750, 4250, 4750, 5250, 5750, 6250, 6750, 7250, 7750, 8250, 8750, 9250, 9750,
        10250, 10750, 11250, 11750, 12250, 12750, 13250, 13750, 14250, 14750, 15250, 15750, 16250,
        16750, 17250, 17750, 18250, 18750, 19250, 19750, 20250, 20750, 21250, 21750, 22250, 22750, 
        23250, 23750, 24250, 24750
    };
    
    
    static final double[] SSSvalue = {
        135.00, 157.50, 180.00, 202.50, 225.00, 247.50, 270.00, 292.50, 315.00, 337.50, 360.00, 
        382.50, 405.00, 427.50, 450.00, 472.50, 495.00, 517.50, 540.00, 562.50, 585.00, 607.50, 
        630.00, 652.50, 675.00, 697.50, 720.00, 742.50, 765.00, 787.50, 810.00, 832.50, 855.00, 
        877.50, 900.00, 922.50, 945.00, 967.50, 990.00, 1012.50, 1035.00, 1057.50, 1080.00, 1102.50
    };
    
    
    static ArrayList<String[]> employeeData = new ArrayList<>(); 
    static ArrayList<String[]> attendanceData = new ArrayList<>();
    
    
    //Loads all employee records from the employee CSV file.
  
    static void loadEmployeeData() {
        employeeData.clear();
    
        try (BufferedReader br = new BufferedReader (new FileReader (EmployeeFile))) {
            br.readLine();
            String line;

            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    employeeData.add(line.split(CSVSplit));
                }
            }
        } catch (Exception e) {
            System.out.println("Loading Employee data failed - " + e.getMessage());
        }
    }
    
    //Loads all attendance records from the attendance CSV file
    
    static void loadAttendanceData() {
        attendanceData.clear();
    
        try (BufferedReader br = new BufferedReader (new FileReader (AttendanceFile))) {
            br.readLine();
            String line;
            
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    attendanceData.add(line.split (CSVSplit));
                }
            }
        } catch (Exception e) {
            System.out.println("Loading Attandance data failed - " + e.getMessage());
        }
    }
    
    //Reads numeric menu input safely.
    
    static int menuchoice (Scanner sc) {
        String input = sc.nextLine().trim();
    
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Input error: " + e.getMessage());
            System.exit(0);
            return -1;
        }
    }

    //employee menu
    
    static void employeeMenu (Scanner sc) {
        System.out.println("\nl. Employee number"); 
        System.out.println("2. Exit\n");
        
        int choice = menuchoice (sc);
        
        if (choice == 1) {
            System.out.print ("\nEmployee number: "); 
            String input = sc.nextLine().trim();
            
            int empNumber;
            
            try {
                empNumber = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Input error - " + e.getMessage());
                System.exit(0);
                return;
            }
            
            String empNum = String.valueOf(empNumber); 
            processempinfo (empNum, true);
        } else if (choice == 2) {
            System.exit(0);
        
        } else {
            System.out.println("Input error - Invalid Input. Program terminated.");
            System.exit(0);
        }
    }
    
    //payroll staff menu
    static void payrollMenu (Scanner sc) { 
        System.out.println("\n1. Process Payroll"); 
        System.out.println("2. Exit\n");

        int choice = menuchoice(sc);

        if (choice == 1) {
            System.out.println("\nl. One employee");
            System.out.println("2. All employees"); 
            System.out.println("3. Exit\n");
            
            int subChoice = menuchoice (sc);
            
            if (subChoice == 1) {
                System.out.print ("\nEmployee number: ");
                String input = sc.nextLine().trim();
                
                int empNumber;
                
                try {
                    empNumber= Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Input error: " + e.getMessage());
                    System.exit(0);
                    return;
                }
                
                String empNum = String.valueOf(empNumber);
                double rate = processempinfo(empNum, false);
                
                if (rate != -1) {
                    payrollcomputation (empNum, rate);
                }
            } else if (subChoice == 2) {
                allempprocess();
            } else if (subChoice == 3) {
                System.exit(0);
            } else {
                    System.out.println("Error - Invalid input. Terminating program");
                    System.exit(0);
            }
        } else if (choice == 2) {
                System.exit(0);
        } else {
                System.out.println("Error - Invalid input. Terminating program");
                System.exit(0);
        }
    }
    
    //finds employee info based on employee number
    static double processempinfo (String empNum, boolean showDetails) { 
        String[] employee = employeenumb (empNum);
    
            if (employee != null) {
                String firstName = employee [2].trim();
                String lastName = employee [1].trim();
                String birthday = employee [3].trim();
                double hourlyRate = Double.parseDouble(employee [18].trim());
                
                if (showDetails) {
                    System.out.println("\nEmployee #: " + empNum);
                    System.out.println("Employee Name: " + lastName + ", " + firstName);
                    System.out.println("Birthday #: " + birthday);
                }
                
                return hourlyRate;
            }

          System.out.println("Employee does not exist. Terminating program");
          return -1;
        }


    static String[] employeenumb(String empNum) {
        for (String[] employee : employeeData) {
            if (employee[0].trim().equals(empNum)) {
                return employee;
            }
        }
        
        return null;
    }

    
    static void empheader (String empNum) { 
        String[] employee = employeenumb (empNum);
        
        if (employee != null) {
            String firstName = employee [2].trim(); 
            String lastName = employee [1].trim(); 
            String birthday = employee [3].trim();
           
            System.out.println("\n--------------------------------------------------");
            System.out.println("Employee #:"+empNum); 
            System.out.println("Employee Name: " + lastName + ", " + firstName);
            System.out.println("Birthday: " + birthday); 
            System.out.println("\n--------------------------------------------------");
        }
    }
    //process all employee data in their dataset
    static void allempprocess () {
        for (String[] employee : employeeData) {
            String empNum = employee [0].trim();
            double hourlyRate = Double.parseDouble (employee [18].trim()); 
            payrollcomputation (empNum, hourlyRate);
        }
    }
    
    //compute payroll for one employee
    
    static void payrollcomputation(String empNum, double rate) {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");
        TreeMap<YearMonth, double[]> payrollPeriods = new TreeMap<>();
        
        int baseYear = 2024;
        
        for (int month = 6; month <= 12; month++) {
            YearMonth period = YearMonth.of(baseYear, month);
            payrollPeriods.put(period, new double[2]);
        }
        
        for (String[] data: attendanceData) {
            if  (!data[0].trim().equals(empNum)){
                continue;
            }
                    
            String[] dataParts = data[3].trim().split("/");
            
            int month = Integer.parseInt(dataParts[0]);
            int day = Integer.parseInt(dataParts[0]);
            int year = Integer.parseInt(dataParts[0]);
            
            YearMonth period = YearMonth. of (year, month);
            payrollPeriods.putIfAbsent(period, new double[2]);
            
            LocalTime login = LocalTime.parse(data[4].trim(), timeFormat);
            LocalTime logout = LocalTime.parse(data [5].trim(), timeFormat);
            
            double hoursWorked = hourscomputation (login, logout);
            
            if (day <= 15) {
            payrollPeriods. get (period) [0] += hoursWorked;
            } else {
               payrollPeriods.get (period) [1] += hoursWorked;
            }
        }

        empheader(empNum);
    
        for (Map.Entry<YearMonth, double[]> entry : payrollPeriods.entrySet()) {
            YearMonth period = entry.getKey();
            double firstHalfHours = entry.getValue()[0];
            double secondHalfHours = entry.getValue() [1];
        
            double firstGross = firstHalfHours + rate;
            double secondGross = secondHalfHours + rate;
            double monthlyGross = firstGross + secondGross;
        
            double sss = SScomputationS (monthlyGross);
            double philHealth = philhealthcomputation (monthlyGross);
            double pagibig = pagibigcomputation (monthlyGross);
        
            double totalGovernmentDeductions = sss + philHealth + pagibig; 
            double taxableIncome = monthlyGross - totalGovernmentDeductions; 
            double withholdingTax = Taxes (taxableIncome);
            double totalDeductions = totalGovernmentDeductions + withholdingTax;
        
            double firstNet = firstGross;
            double secondNet = secondGross - totalDeductions;
        
            int daysInMonth = period.lengthOfMonth();
            String monthYear = formatMonthYear (period);
        
            String monthOnly = period.getMonth().toString();
            monthOnly = monthOnly.substring(0, 1) + monthOnly.substring (1).toLowerCase();
        
            System.out.println("\nPayroll Period: " + monthYear);
            System.out.println("\nCutoff Date: " + monthOnly + " 1 to 15");
            System.out.println("Total Hours Worked: "+firstHalfHours);
            System.out.println("Gross Salary:" + firstGross);
            System.out.println("Net Salary: " + firstNet);
            System.out.println("\nCutoff Date: " + monthOnly + " 16 to " + daysInMonth);
            System.out.println("Total Hours Worked: " + secondHalfHours);
            System.out.println("Gross Salary: " + secondGross);
            System.out.println("sss: " + sss);
            System.out.println("PhilHealth: " + philHealth);
            System.out.println("Pag-IBIG: " + pagibig);
            System.out.println("Tax: " + withholdingTax);
            System.out.println("Total Deductions: " + totalDeductions);
            System.out.println("Net Salary: " + secondNet);
        }
    }
    
    //
    
    static double hourscomputation(LocalTime login, LocalTime logout) {
        LocalTime officialStart = LocalTime.of(8, 0);
        LocalTime graceLimit = LocalTime.of(8, 10);
        LocalTime officialEnd = LocalTime.of(17, 0);
        
        if (login.isBefore(officialStart)) {
            login = officialStart;
        }
        if (!login.isAfter(graceLimit)) {
            login = officialStart;
        }
        if (logout.isAfter(officialEnd)) {
            logout = officialEnd;
        }
        if (!logout.isAfter(login)) {
            return 0;
        }
        
        double hoursWorked = Duration.between(login, logout).toMinutes() / 60.0;
        
        if (hoursWorked > 1) {
            hoursWorked -= 1;
        }
        
        if (hoursWorked < 0) {
            return 0;
        }
        
        return hoursWorked;
    }
    
    //Computes SSS based on monthly gross salary
    
    static double SScomputationS (double salary) {
        for (int i = 0; i < SSSranges.length; i++) {
            if (salary <= SSSranges[i]) {
                return SSSvalue[i];
                
            }
        }
        
        return 1125.00;
    }
    
    //compute PhilHealth employees' share
    
    static double philhealthcomputation(double salary) {
        double premium;
        
        if (salary <= 10000) {
            premium = 300.00;
        } else if (salary >= 60000) {
            premium = 1800.00;
        } else {
            premium = salary * 0.03;
        }
        
        return premium = 2;
    }
    //Computes pag-IBIG employees' share
    static double pagibigcomputation(double salary) {
        double contribution;
        
        if (salary <= 1500) {
            contribution = salary * 0.01;
        } else {
            contribution = salary * 0.02;
        }
        
        if (contribution > 100) {
            return 100;
        }
        return contribution;
    }
    
    //calculate withholdning tax
    static double Taxes(double taxableIncome) {
        if (taxableIncome <= 20832) {
            return 0;
        } else if (taxableIncome < 33333) {
            return (taxableIncome - 20833) * 0.20;
        } else if (taxableIncome < 66667) {
            return 2500 + (taxableIncome - 33333) * 0.25;
        } else if (taxableIncome < 166667) {
            return 10833 + (taxableIncome - 66667) * 0.30;
        } else if (taxableIncome < 666667) {
            return 40833.33 + (taxableIncome - 166667) * 0.25;
        } else {
            return 200833.33 + (taxableIncome - 666667) * 0.35;
        }
    }
    
    //formats monthyear
    
    static String formatMonthYear(YearMonth period) {
        String month = period.getMonth().toString();
        String formattedMonth = month.substring(0, 1) + month.substring(1).toLowerCase();
        
        return formattedMonth + " " + period.getYear();
    }
    
    // program entry point
    
   public static void main(String[] args) {
       Scanner sc = new Scanner(System.in);
       
       loadEmployeeData();
       loadAttendanceData();
       
       System.out.print("Enter Name: ");
       String username = sc.nextLine().trim().toLowerCase();
       
       System.out.print("Enter Password: ");
       String password = sc.nextLine().trim();
       
       boolean validUser = username.equals("employee") || username.equals("payroll staff");
       boolean validPassword = password.equals("02488");
       
       if (!validUser || !validPassword) {
           System.out.println("Incorrect Password/Username");
           sc.close();
           return;
       }
       if (username.equals("employee")) {
           employeeMenu(sc);
       } else {
           payrollMenu(sc);
       }
       sc.close();
   }
}