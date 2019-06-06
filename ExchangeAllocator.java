/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exchangeallocatorframe;

/* Program: ExchangeAllocator.java
 * Author: Emiel Verkade, Maastricht University
 * Tested with parameters : 464 students, 150 universities
 * RunTime: 00.0ms
 * 
 * Description: 
 * This program aims to solve the problem of allocating to each student an exchange university.
 * This program uses the following algorithm, as was understood to be the algorithm used by the IRO department.
 * The program searches per student if there is capacity in their first option. If there is, the program 
 * sends the student to that university, and increases the number of students allocated to that university
 * by one. If not, the program continues down the list of choices until the program has either allocated an
 * exchange destination or has run out of choices, leaving the student with the university allocation "0".
 * This program prints out every student, their choices and their allocated spot. It does the same for 
 * universities, printing their number, their capacity, and how many SBE students were allocated there.
 * This program prints out the run time given the parameters, and also prints out the number of students who
 * got their first choice, second choice, etc.
 * 
 * TODO:
 * If student study is not found, let me know instead of printing
 */
import static exchangeallocatorframe.ErrorStudentsFrame.errorDone;
import static exchangeallocatorframe.ErrorStudentsFrame.lastIndex;
import static exchangeallocatorframe.ExchangeAllocatorFrame.studyList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.Normalizer;
import java.util.ArrayList;
import javax.swing.*;

public class ExchangeAllocator implements ActionListener {

    static Student[] studentArray;
    static University[] uniArray;
    static boolean isFinished = false;
    static boolean selectionFinished = false;
    static String lostUniversities = "";
    static Student[] errorArray;
    static int errorIndex;
    public static void run(boolean[] options, String studentFile, String uniFile,
        String programFile, String directory) throws IOException {
        errorIndex = 0;
        //Starts the timer
        System.out.println("INITIAL LAST INDEX: " + lastIndex);
        double startTime = System.currentTimeMillis();
        // if we didn't allocate any students yet
        // read their information in from the files
        if (lastIndex == -1) {
            // read the students information into the student array
            studentArray = studentList(studentFile);
            System.out.println("Students should be read now.");
            errorArray = new Student[studentArray.length];
            // read the universities information into the uni array
            uniArray = uniList(uniFile);
            System.out.println("Universities should be read now");
            // give the universities and student choices numbers
            uniNumberFinder(studentArray, uniArray);
            if (!lostUniversities.isEmpty()) {
                JOptionPane.showMessageDialog(new JFrame(), lostUniversities, "Could not find the following universities",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            System.out.println("Universities & students should have numbers now");
            // fills in the student's study programs
            extraReqs(programFile, studentArray);
            System.out.println("Study Programs are read");
        }
        // Allocates the students
        System.out.println("NOW LAST INDEX ~~~~ " + lastIndex);
        studentAllocator(studentArray, uniArray, lastIndex);
        System.out.println("Students have been allocated.");

        // Ends the timer
        double endTime = System.currentTimeMillis();
        // prompts user to check students who need manual checking
        try {
        if (!errorDone) {
            fixErrorStudents(studentArray);
        }
        } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e, "Error Fixing Error Students.",
                                    JOptionPane.ERROR_MESSAGE);
                            System.exit(0);
        }
        System.out.println("We got here");
        // if they've been checked
        // if someone got their choice rejected
        if (errorDone && lastIndex < (studentArray.length-1)) {
            errorDone = false;
            // clear all allocations below that person
            studentClear(studentArray, lastIndex);
            // run the program again
            run(options, studentFile, uniFile, programFile, directory);
        }
        if (errorDone && !isFinished) {
            // Calculates the time taken to allocate the students
            double runTime = (endTime - startTime);

            // Writes the Student Allocation file
            if (options[0]) {
                System.out.println("StudentAllocation should be printing now.");
                CSVFileWriter.writeCSVFileStudent("StudentAllocation.csv", studentArray, directory);
                CSVFileWriter.writeCSVFileTotal("StudentNamedAllocation.csv", studentArray, directory);
            }

            // Writes the University Allocations file
            if (options[1]) {
                System.out.println("UniAllocation should be printing now.");
                CSVFileWriter.writeCSVFileUniversity("AllocationIncludingRanking.csv", studentArray, uniArray, directory);
                System.out.println("UniAverageAllocation should be printing now.");
                CSVFileWriter.writeCSVFileUniversityAverages("AllocationIncludingAverages.csv", studentArray, directory);
            }

            // Writes a file for students with no allocations
            if (options[2]) {
                System.out.println("SadStudents should be printing now.");
                CSVFileWriter.writeCSVFileNoAllocation("StudentsNoAllocation.csv", studentArray, directory);
            }
            // Writes a file containing universities with spots remaining
            if (options[3]) {
                System.out.println("BackupList should be printing now.");
                CSVFileWriter.writeCSVFileBackup("BackupList.csv", uniArray, directory);
            }

            // Writes a file containing all the students who's study tracks could not be read
            if (options[4]) {
                System.out.println("ErrorStudents should be printing now.");
                CSVFileWriter.writeCSVFileError("ErrorReadingStudents.csv", studentArray, errorArray, directory);
            }
            int noOfUnis = uniArray.length;

            // Prints the program statistics 
            if (options[5]) {
                System.out.println("System Stats should be printing now.");
                statsCalc(studentArray, noOfUnis, runTime, directory);
            }

            System.out.println("Houston, we don't have a problem!");

            isFinished = true;
            FinalFrame frame = new FinalFrame();
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }

    // This method reads the studentList from a text file and adds the data to an array.
    public static Student[] studentList(String filename) throws IOException {
        int size = 0;
        // gets the number of students present in the file
        try (BufferedReader sizeFinder = new BufferedReader(new FileReader(filename))) {
            size = 0;
            while (sizeFinder.readLine() != null) {
                size++;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e, "Error Reading Student Courses File.",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            e.printStackTrace();
        }
        System.out.println("Number of lines in studentList file: " + size);
        Student[] array = null;
        try (BufferedReader text = new BufferedReader(new FileReader(filename))) {
            text.readLine();
            // creates an array of students of the size
            // we actually get one size too many since we count the line of the header
            // as a student, which works perfectly as we insert a dummy student in the 
            // first (0th) entry of the array
            array = new Student[size];
            array[0] = new Student();
            for (int i = 1; i < size; i++) {
                String firstName;
                String lastName;
                String prefix;
                String phoneNumber;
                String email1;
                String email2;
                String[] choices = new String[6];
                String Line = text.readLine();
                if (!Line.isEmpty()) {
                    String[] parts = Line.split(";", -1);
                    int ranking = Integer.parseInt(parts[1]);
                    int id = Integer.parseInt(parts[2]);
                    firstName = parts[3];
                    lastName = parts[4];
                    prefix = parts[5];
                    phoneNumber = parts[6];
                    email1 = parts[7];
                    email2 = parts[8];
                    for (int j = 0; j < 6; j++) {
                        try {
                            choices[j] = parts[j + 9];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Debug reading student's choices");
                            System.out.println("Ranking: " + parts[1]);
                            String errorMessage = "Check student ranked ";
                            errorMessage += parts[1];
                            JOptionPane.showMessageDialog(null, errorMessage, "Error Reading Student Choices File.",
                                    JOptionPane.ERROR_MESSAGE);
                            System.exit(0);
                        }
                    }
                    boolean actualStudent = true;
                    for (int j = 0; j < 6; j++) {
                        if (choices[j].equals("CANCELLATION")) {
                            actualStudent = false;
                        }
                    }
                    if (actualStudent) array[i] = new Student(ranking, id, firstName, lastName, prefix, phoneNumber, email1, email2, choices);
                    else array[i] = new Student();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e, "Error Reading Student Courses File.",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            e.printStackTrace();
        }
        // return the filled array of students
        return array;
    }

    // This method reads the list of students and their study programmes
    public static void extraReqs(String filename, Student[] studentArray) throws IOException {
        int numberOfLines = 0;
        int j = 0;
        int currentRanking = 0;
        String[] parts = new String[0];
        try {
            BufferedReader text = new BufferedReader(new FileReader(filename));
            text.readLine();
            // counts the number of lines in the file
            while (text.readLine() != null) {
                numberOfLines++;
            }
            text.close();
            text = new BufferedReader(new FileReader(filename));
            text.readLine();
            // for every line in the file
            for (j = 0; j < numberOfLines; j++) {

                // student has not yet been found
                boolean found = false;
                // save the line information from the file
                String Line = text.readLine();
                // split the line according to the delimiter
                parts = Line.split(";", -1);

                currentRanking = Integer.parseInt(parts[0]);
                // the cancellation is the last part of the line
                String cancellation;
                try {
                    cancellation = parts[10].trim();
                } catch (ArrayIndexOutOfBoundsException e) {
                    cancellation = "";
                }
                // read the study of the student
                String study = studyChecker(parts[8]);
                // convert the double from dutch format to english
                parts[9] = parts[9].trim().replaceAll(",", ".");
                // split the parts of the average grade
                String[] partsOfParts = parts[9].split(" ", -1);
                // convert the last part to an integer
                double studyAbroadAverage = 0.0;
                try {
                    studyAbroadAverage = Double.parseDouble(partsOfParts[partsOfParts.length - 1]);
                }
                catch (NumberFormatException ex) {
                    studyAbroadAverage = Double.parseDouble(partsOfParts[0]);
                }
                // for every student 
                for (int i = 0; i < studentArray.length; i++) {
                    // get the student
                    Student student = studentArray[i];
                    // if the student ID equals the ID of the person interested
                    if (student.getID()== Integer.parseInt(parts[1])) {
                        // set the study of the student
                        student.setStudy(study);
                        // set the average of the student
                        student.setStudyAbroadAverage(studyAbroadAverage);
                        // we found the student 
                        found = true;
                        // if they cancelled 
                        if (!cancellation.isEmpty()) {
                            // wipe their choices
                            System.out.println("~~~~Student WIPED~~~~");
                            System.out.println("Student ranking: " + student.getRanking());
                            System.out.println("Grade file ranking: " + currentRanking);
                            System.out.println("Line number: " + j);
                            for (int c = 1; c < 7; c++) {
                                student.clearChoice(c);
                            }
                        }
                        // stop searching for this student
                        break;
                    }
                }
                // if we didn't find them and they didn't cancel
                if (!found && cancellation.isEmpty()) {
                    // let us know we couldn't find them
                    System.out.println("Whoopsie! Couldn't find " + parts[1]);
                    int ranking = Integer.parseInt(parts[0]);
                    int id = Integer.parseInt(parts[1]);
                    String firstName = parts[2];
                    String lastName = parts[3];
                    String prefix = parts[4];
                    String phone = parts[5];
                    String email1 = parts[6];
                    String email2 = parts[7];
                    errorArray[errorIndex] = new Student(ranking, id, firstName, lastName, prefix, phone, email1, email2, null);
                    errorIndex++;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Debug reading student's GPAs");
            System.out.println("Linenumber: " + j);
            String errorMessage = "Check student ranking: " + currentRanking;
            JOptionPane.showMessageDialog(null, errorMessage, "Error Reading Student GPA File.",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } catch (ArrayIndexOutOfBoundsException a) {
            System.out.println("Debug reading student's information");
            System.out.println("Linenumber: " + j);
            String errorMessage = "Check line number ";
            errorMessage += (j+2);
            errorMessage += "\nAlso student ranking " + currentRanking;
            errorMessage += "\n" + a;
            for (int i = 0; i < parts.length; i++) {
                System.out.println(parts[i]);
            }
            JOptionPane.showMessageDialog(null, errorMessage, "Error Reading Student GPA File.",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    //Converts a read study of a student to an easy to compare string for the program
    public static String studyChecker(String studentStudy) {
        studentStudy = studentStudy.toLowerCase().trim();
        for (int i = 0; i < studyList.length; i++) {
            String line = studyList[i];
            String[] parts = line.split(";");
            if (studentStudy.equals(parts[0].toLowerCase().trim())) {
                return parts[1];
            }
        }
        return "Could not find";
    }

    // This method reads the uniList from a text file and adds the data to an array.
    public static University[] uniList(String filename) throws IOException {
        int size;
        try (BufferedReader sizeFinder = new BufferedReader(new FileReader(filename))) {
            size = 0;
            while (sizeFinder.readLine() != null) {
                size++;
            }
        }
        University[] array = null;
        try {
            BufferedReader text = new BufferedReader(new FileReader(filename));
            text.readLine();
            // creates a new array of universities
            array = new University[size];
            // first university is a dummy university
            array[0] = new University();
            // for every university in the file
            for (int i = 1; i < size; i++) {
                // start by setting GPA to 0
                double GPARequirement = 0.0;
                // read the line with information
                String line = text.readLine();
                // split the information into parts
                String[] parts = line.split(";", 5);
                // the name of the university is the first piece of information
                String name = parts[0].trim();
                // the requirements type is so far unknown
                int reqType = -1;
                // read the capacity of the university and store it
                int uniCapacity = Integer.parseInt(parts[1]);
                // if there are extra requirements
                if (!parts[2].equals("")) {
                    // change the reqType to 0 for now to indicate a requirement exists
                    reqType = 0;
                    // make sure to keep formatting the same
                    parts[2] = parts[2].replace(",", ".");

                    try {
                        // if the requirement contains GPA
                        if (parts[2].contains("GPA")) {
                            // reqType = 3 is GPA requirement
                            reqType = 3;
                            // add the GPA requirement
                            String[] partsOfParts = parts[2].split(" ");
                            // GPA requirement should be in the format of "X.Y GPA"
                            for (String littlePart : partsOfParts) {
                                if (!littlePart.equals("GPA")) {
                                    GPARequirement = Double.parseDouble(littlePart);
                                }
                            }
                        } // if the requirement is overall average grade
                        else if (parts[2].toLowerCase().contains("overall average grade")) {
                            // reqType = 3 is overall average grade requirement
                            reqType = 2;
                            String[] partsOfParts = parts[2].split(" ");
                            // add the overall average grade requirement
                            GPARequirement = Double.parseDouble(partsOfParts[0]);
                        } else {
                            // reqType = 1 is equally weighted requirement
                            reqType = 1;
                            // add the equally weighted average requirement
                            GPARequirement = Double.parseDouble(parts[2]);
                        }
                        // if a NumberFormatException is thrown, print out a debugging statement
                    } catch (NumberFormatException e) {
                        System.out.println("Debug Reading University Adding GPA Reqs");
                        System.out.println(name);
                        System.out.println(parts[2]);
                        GPARequirement = Double.parseDouble((parts[2].split(" ")[0]));
                        System.out.println(GPARequirement);
                        JOptionPane.showMessageDialog(null, e, "Error Reading University File. Check GPA requirements",
                                JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    }
                }
                // if there is an other requirement, add it
                String OtherRequirement = uniReqCheck(parts[3]);
                // create the university object and add it to the array
                array[i] = new University(name, uniCapacity, i, reqType, GPARequirement, OtherRequirement);
            }
        }
        catch (IOException e){
            JOptionPane.showMessageDialog(null, e, "Error Reading University File. Check the contents of the file",
                                JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
        }
        return array;
    }

    // Finds the computer allocated number of the university choices each student makes
    public static void uniNumberFinder(Student[] studentArray, University[] uniArray) {
        // for every student in the array
        for (Student student : studentArray) {
            // if the student is the dummy student, do nothing
            if (student.getRanking() == 0) {
            } // otherwise
            else {
                // for every element in the array
                for (int i = 1; i <= 6; i++) {
                    boolean isFound = false;
                    // for every university in the array
                    for (University university : uniArray) {

                        String uniName = university.getComparisonName();
                        String studentChoice = student.getChoice(i);
                        studentChoice = Normalizer.normalize(studentChoice.toLowerCase().replaceAll("\\s", ""), Normalizer.Form.NFD);
                        studentChoice = studentChoice.replaceAll("[^\\p{ASCII}]", "");
                        // if the student picked that university
                        if (studentChoice.equals(uniName)) {
                            // match the choice name to the university number
                            student.setChoiceNumber(i, university.getNumber());
                            isFound = true;
                            break;
                        } else if (university.hasAlternativeName()) {
                            if (studentChoice.equals(university.getAlternativeName())) {
                                student.setChoiceNumber(i, university.getNumber());
                                isFound = true;
                                break;
                            }
                        }
                    }
                    if (!isFound // university was not found
                            && !student.getChoice(i).isEmpty() // and the student didn't enter nothing
                            && !student.getChoice(i).equals("NONE")
                            && !student.getChoice(i).equals("CANCELLATION")) { // and the student didn't write NONE
                        System.out.println("~~~~~~ERROR~~~~~~");
                        System.out.println("University not found: " + student.getChoice(i));
                        System.out.println("Student ranked: " + student.getRanking());
                        System.out.println(student.getChoice(i).toLowerCase().replaceAll("\\s", ""));
                        lostUniversities += ("University not found: " + student.getChoice(i) + "\n");
                        lostUniversities += "Student ranked: " + student.getRanking() + "\n";
                        lostUniversities += "Choice number: " + i + "\n";
//                        isFound = false;
//                        UniChooserFrame frame = new UniChooserFrame(student.getRanking(), i);
//                        frame.pack();
//                        frame.setLocationRelativeTo(null);
//                        frame.setVisible(true);
//
//                        System.out.println("");
                    }
                }
            }
        }
    }

    // Converts specials requirements of the university to an easy to read set
    public static String uniReqCheck(String req) {
        String[] parts = req.split(" ");
        if (parts.length < 5) {
            String s = "";
            for (String part : parts) {
                if (part.equals("IB")) {
                    s += "IB,";
                }
                if (part.equals("EBE")) {
                    s += "EBE,";
                }
                if (part.equals("E&OR") || part.equals("Econometrics")) {
                    s += "E&OR,";
                }
            }
            return s;
        } else {
            return req;
        }
    }

    // This method allocates the students to their choices.
    public static void studentAllocator(Student[] studentArray, University[] uniArray, int start) {
        for (Student student : studentArray) {
            if (student.getRanking() < start || student.getRanking() == 0) {
            } else {
                int ranking = student.getRanking();
                // checks the capacity of each student's choice.
                for (int j = 1; j <= 6; j++) {
                    int choice = student.getNumberChoices(j);
                    University uni = uniArray[choice];
                    // if there is space in the university
                    if (uni.hasSpace()) {
                        // checks if student adheres to requirements
                        getCheckedSon(student, uni);
                        // if they do, allocate
                        if (student.allChecked()) {
                            allocator(student, uni, j);
                            if (student.getID() == 6158027) {
                                System.out.println("Allocated 6158027 to " + uni.getName());
                            }
                            break;
                            // move onto next student
                            // if student didnt get approved but have difficult other req
                            // and dont have a study abroad req that they didnt fail
                            // allocate them
                        } else if (!uni.hasSAReq() && student.getOtherChecked()) {
                            allocator(student, uni, j);
                            break;
                        } else if (uni.getOtherReq().length() > 20 && (!uni.hasSAReq())) {
                            System.out.println("Cannot check prereq so will put student to list " + student.getID());
                            allocator(student, uni, j);
                            break;
                        }
                    }
                }
            }
        }
    }

    // Counts the number of students who got their nth choice
    public static int counter(Student[] studentArray, int n) {
        int counter = 0;
        for (Student student : studentArray) {
            if (student.getChoiceNumber() == n) {
                counter++;
            }
        }
        return counter;
    }

    // Allocates a student to a university, and updates all data to do with this
    public static void allocator(Student student, University uni, int choiceNo) {
        int ranking = student.getRanking();
        uni.incrementAllocated();
        student.setUni(uni);
        student.setUniNumber(uni.getNumber());
        student.setChoiceNumber(choiceNo);
        if (ranking == 327) {
            System.out.println("327 allocated to " + student.getUniName());
            System.out.println("Choice number " + choiceNo);
        }
    }

    // Checks if a student has the right study program for their choice of university
    // not sure if I like this function, might need to redo
    public static void getCheckedSon(Student student, University university) {
        int ranking = student.getRanking();
        if (ranking == 327) {
            System.out.println(university.getName());
        }
        // if a university does not have another requirement
        if (!university.hasOtherReq()) {
            // the student adheres to that requirement
            student.setOtherChecked(true);
            if (ranking == 327) {
                System.out.println("Other = true");
            }
        } // if a university does have another requirement
        else {
            // get the requirement
            String uniReqs = university.getOtherReq();
            String[] array = uniReqs.split(",");
            // check all possible study requirements
            for (String study : array) {
                if (ranking == 327) {
                    System.out.println("Study required: " + study);
                    System.out.println("Study of student: " + student.getStudyTrack());
                    System.out.println("SA average: " + student.getStudyAbroadAverage());
                }
                // if the student's study is the same as one of the study requirements
                if (student.getStudyTrack().equals(study)) {
                    // set the student to checked
                    student.setOtherChecked(true);
                    if (ranking == 327) {
                        System.out.println("Other = true");
                    }
                    // stop checking study requirements
                    break;
                }
            }
            if (ranking == 327) {
                System.out.println("Other = false");
            }
        }
        // if a university has a GPA req or an equally weighted req
        if (university.hasGPAReq() || university.hasEquallyWeightedReq()) {
            // cannot check the student's average compared to university minimum
            student.setStudyAbroadChecked(false);
            if (ranking == 327) {
                System.out.println("Average = false");
            }
        } // otherwise
        else {
            // get the study abroad requirement
            double studyAbroadReq = university.getStudyAbroadReq();
            // if the student adheres to the study abroad average
            if (student.getStudyAbroadAverage() >= studyAbroadReq) {
                // set the student to adhering to the requirement
                student.setStudyAbroadChecked(true);
                if (ranking == 327) {
                    System.out.println("Average = true");
                }
            } // otherwise the student does not adhere
            else {
                // set the student to not adhering
                student.setStudyAbroadChecked(false);
                if (ranking == 327) {
                    System.out.println("Average = false");
                }
            }
        }
    }

    // Creats a Student Array of all students who need to be checked
    public static void fixErrorStudents(Student[] studentArray) {
        ArrayList<Student> errorList = new ArrayList<>();
        int choiceNumber;
        for (Student student : studentArray) {
            choiceNumber = student.getChoiceNumber();
            if (student.getRanking() == 0) {
            } // dummy student not counted
            else if (student.isMasterApproved(choiceNumber)) {
            } // if student was master approved
            else if (student.getChoiceNumber() == 0) {
            } // if student didn't get any choices
            else if (!student.allChecked()) {
                errorList.add(student);
            }
        }
        int size = errorList.size();
        Student[] errorStudents = new Student[size];
        errorList.toArray(errorStudents);
        if (size == 0) {
            errorDone = true;
            lastIndex = studentArray[studentArray.length - 1].getRanking();
            return;
        }
        ErrorStudentsFrame frame = new ErrorStudentsFrame(errorStudents);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        System.out.println("There should be an Error Frame now!");
    }

    //Clears the students allocations from below a certain position
    public static void studentClear(Student[] studentArray, int starting) {
        for (Student student : studentArray) {
            if (student.getRanking() < starting) {
            } else {
                if (student.getUni() != null) {
                    student.getUni().decrementAllocated();
                    student.setUniNumber(0);
                    student.setUni(null);
                    student.setChoiceNumber(0);
                    student.setOtherChecked(false);
                    student.setStudyAbroadChecked(false);
                }
            }
        }
        System.out.println("STUDENTS GOT CLEARED FROM " + starting
                + " to " + (studentArray.length - 1));
    }

    // Calculates and prints the statistics for the allocation and the program
    public static void statsCalc(Student[] studentArray, int noOfUnis, double runTime, String directory) throws IOException {
        int noOfStudents = studentArray.length;
        // Writes a file called SystemStatistics.txt
        File dir = new File(directory);
        File actualFile = new File(dir, "SystemStatistics.txt");
        // Special case in terms of text for students who did not get any of their choices
        try (PrintWriter writer = new PrintWriter(actualFile, "UTF-8")) {
            // Special case in terms of text for students who did not get any of their choices
            int students = counter(studentArray, 0);
            double percentage = (1.0 - (((double) students) / ((double) noOfStudents)));
            writer.println("The program allocated to " + noOfStudents + " students a university place, given "
                    + noOfUnis + " universities. The program did this in " + runTime + " ms.");
            writer.println(students + " were not able to be allocated a place and will have to "
                    + "be allocated a place from the backup list. This indicates a "
                    + (percentage * 100.0) + "% success rate.");
            // for loop to output which % of students got their ith choice (choices range from 1 to 6)
            for (int i = 1; i <= 6; i++) {
                students = counter(studentArray, i);
                percentage = ((double) students / ((double) noOfStudents)) * 100.0;
                writer.println(percentage + "% of students got choice number " + i + ".");
            }
        }
        System.out.println("The System Statistics file was succesfully created.");
    }

    public void actionPerformed(ActionEvent e) {
        String buttonText = e.getActionCommand();

        if (buttonText.equals("Confirm") || buttonText.equals("Cancel")) {
            selectionFinished = true;
        }
    }
}
