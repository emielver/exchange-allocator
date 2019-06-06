/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exchangeallocatorframe;

/* Class: Student.java
 * Author: Emiel Verkade - Maastricht University
 * 
 * Description:
 * This program outlines the components of the 'Student' object
 * created by myself for this specific problem. There are several
 * methods designed and implemented with descriptions above the methods
 *  
 */

public class Student {
  
  private final int Ranking;
  private final int StudentNumber;
  private String[] Choices;
  private int[] NumberChoices;
  private University university;
  private int UniNumber;
  private int ChoiceNumber;
  private String StudyTrack;
  private double studyAbroadAverage;
  private boolean studyAbroadChecked;
  private boolean requirementsChecked;
  private final boolean[] masterApproval;
  private final String firstName;
  private final String lastName;
  private final String email1;
  private final String email2;
  private final String prefix;
  private final String phoneNumber;
  
  
  //constructor
    public Student() {
    this.Ranking = 0;
    this.StudentNumber = 0;
    this.Choices = null;;
    this.NumberChoices = null;
    this.university = null;
    this.UniNumber = 0;
    this.ChoiceNumber = 0;
    this.StudyTrack = "";
    this.studyAbroadAverage = 0.0;
    this.studyAbroadChecked = false;
    this.requirementsChecked = false;
    this.masterApproval = new boolean[6];
    this.firstName = null;
    this.lastName = null;
    this.email1 = null;
    this.email2 = null;
    this.prefix = null;
    this.phoneNumber = null;
  }
    
  // constructor
  public Student(int Ranking, int StudentNumber, String firstName, String lastName, String prefix, String phoneNumber, String email1, String email2,  String[] Choices) {
    this.Ranking = Ranking;
    this.StudentNumber = StudentNumber;
    this.Choices = Choices;
    this.NumberChoices = new int[6];
    this.university = null;
    this.UniNumber = 0;
    this.ChoiceNumber = 0;
    this.StudyTrack = "";
    this.studyAbroadAverage = 0.0;
    this.studyAbroadChecked = false;
    this.requirementsChecked = false;
    this.masterApproval = new boolean[6];
    this.firstName = firstName;
    this.lastName = lastName;
    this.prefix = prefix;
    this.email1 = email1;
    this.email2 = email2;
    this.phoneNumber = phoneNumber;
  }
  // returns a student's ranking
  public int getRanking() {
    return this.Ranking;
  }
  // returns a student's ID number
  public int getID() {
    return this.StudentNumber;
  }
  // returns a student's university number
  public int getUniNumber() {
    return this.UniNumber;
  }
  // returns a student's 1st choice
  public String getChoice(int i) {
    return this.Choices[i-1];
  }
  // returns a student's ith choice
  public int getNumberChoices(int i) {
    return this.NumberChoices[i-1];
  }
  // returns a student's choice number 
  public int getChoiceNumber() {
    return this.ChoiceNumber;
  }
  
  public String getPhoneNumber() {
      return this.phoneNumber;
  }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getEmail1() {
        return this.email1;
    }
    
    public String getEmail2() {
        return this.email2;
    }

    public String getPrefix() {
        return this.prefix;
    }
 
  // returns the university a student was assigned
  public University getUni() {
    return this.university;
  }
  // returns a student's allocated university
  public String getUniName () {
    return this.university.toString();
  } 
  // returns a student's study track
  public String getStudyTrack() {
    return this.StudyTrack;
  }
  // returns a student's GPA
  public double getStudyAbroadAverage() {
    return this.studyAbroadAverage;
  }
  // returns if the requirements have been checked
  public boolean allChecked() {
    return (this.requirementsChecked && this.studyAbroadChecked);
  }
  // returns if a student's GPA was checked with regards to the university
  public boolean getSAChecked() {
    return this.studyAbroadChecked;
  }
  
  public boolean getOtherChecked() {
      return this.requirementsChecked;
  }
  
  public int getApproved() {
      int number = 0;
      for (int i = 0; i<6; i++) {
          if (this.masterApproval[i]) {
              number = i;
          }
      }
      return number;
  }
  // sets the student's choices as numbers
  public void setNumberChoices (int[] array) {
    this.NumberChoices = array;
  }
  // sets the student's study track
  public void setStudy (String study) {
    this.StudyTrack = study;
  }
  // sets the student's chosen uniNumber
  public void setUniNumber (int uniNumber) {
    this.UniNumber= uniNumber;
  }
  // sets the allocated university to the input
  public void setUni (University university) {
    this.university = university;
  }
  // sets the student's GPA
  public void setStudyAbroadAverage (double number) {
    this.studyAbroadAverage = number;
  }
  // sets the choice number to the input
  public void setChoiceNumber (int number) {
    this.ChoiceNumber = number;
  }
  
  public void setChoiceNumber(int choiceNumber, int uniNumber) {
      this.NumberChoices[choiceNumber-1] = uniNumber;
  }
  // sets the person as having their requirements checked
  public void setOtherChecked(boolean result) {
    this.requirementsChecked = result;
  }
  // sets the student to having their GPA requirments checked
  public void setStudyAbroadChecked(boolean result) {
    this.studyAbroadChecked = result;
  }
  
  public void clearChoice (int choiceNumber) {
    this.Choices[choiceNumber-1] = "";
    this.NumberChoices[choiceNumber-1] = 0;
  }
    // Wipes the student's choice
  public void studentWipe() {
    int choice = this.getChoiceNumber();
    System.out.println("Wiping" + this.Ranking + "'s " + 
            choice + "th choice.");
    this.clearChoice(choice);
    this.getUni().decrementAllocated();
    
  }
  
  // returns master approval
  public boolean isMasterApproved(int number) {
      if (number == 0) return true;
      return this.masterApproval[number-1];
  }
  
  public void setApproval(int number) {
      this.masterApproval[number-1] = true;
  }
  
  public String toString() {
    return String.valueOf(this.StudentNumber);
  }
  /*
  // return string representation of this student record
  public String toString() {
    if (this.ChoiceNumber!=0) {
      return ("Student " + this.StudentNumber + ", studying " + this.StudyTrack + " was assigned to the following university: " 
                + this.UniName  + ". This was choice number " + this.ChoiceNumber + ".");
    }
    else {
      return ("Student " + this.StudentNumber + " did not get assigned any universities. Please refer"
                + " to the list generated at the end of the program to see which universities they may " 
                + "go to.");
    }
  }
  */
}
