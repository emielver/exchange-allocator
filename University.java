/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exchangeallocatorframe;

import java.text.Normalizer;

/* Class: University.java
 * Author: Emiel Verkade - Maastricht University
 * 
 * Description:
 * This program outlines the 'University' object which
 * was created by myself for this specific allocation problem.
 * There are several methods designed and implemented with
 * descriptions above them detailing what they do.
 *  
 */
public class University {

    private final String UniversityName;
    private String universityComparisonName;
    private String alternativeName;
    private final int UniCapacity;
    private final int UniNumber;
    private int AmountOfStudentsAllocated;
    private double equallyWeightedRequirement;
    private double studyAbroadGPA;
    private double GPARequirement;
    private String OtherRequirement;

    // blank constructor
    public University() {
        this.UniversityName = "NONE";
        this.universityComparisonName = "none";
        this.alternativeName = "";
        this.UniCapacity = 0;
        this.UniNumber = 0;
        this.AmountOfStudentsAllocated = 0;
        this.equallyWeightedRequirement = 0;
        this.GPARequirement = 0;
        this.OtherRequirement = "";
    }

    // useful constuctor
    public University(String UniversityName, int UniCapacity, int UniNumber, int gradeReqType, double gradeReq, String OtherRequirement) {
        this.UniversityName = UniversityName;
        this.universityComparisonName = Normalizer.normalize(UniversityName.toLowerCase().replaceAll("\\s", "").replaceAll("[^\\p{ASCII}]", ""),Normalizer.Form.NFD);
        this.alternativeName = "";
        this.UniCapacity = UniCapacity;
        this.UniNumber = UniNumber;
        this.AmountOfStudentsAllocated = 0;
        this.GPARequirement = 0;
        this.equallyWeightedRequirement = 0;
        this.studyAbroadGPA = 0;
        switch (gradeReqType) {
            case 1:
                this.studyAbroadGPA = gradeReq;
                break;
            case 2:
                this.equallyWeightedRequirement = gradeReq;
                break;
            case 3:
                this.GPARequirement = gradeReq;
                break;
            default:
                break;
        }
        this.OtherRequirement = OtherRequirement;
    }

    // returns the university's name
    public String getName() {
        return this.UniversityName;
    }
    // returns the university's capacity

    public int getCapacity() {
        return this.UniCapacity;
    }
    // returns the amount of students allocated at the university

    public int getAllocated() {
        return this.AmountOfStudentsAllocated;
    }
    // adds one to the current number of students allocated to the university

    public void incrementAllocated() {
        this.AmountOfStudentsAllocated++;
    }
    // subtracts one to the current number of students allocated to the university

    public void decrementAllocated() {
        this.AmountOfStudentsAllocated--;
    }
    
    public boolean hasSAReq() {
        return (this.studyAbroadGPA != 0);
    }
    // returns the university number

    public int getNumber() {
        return this.UniNumber;
    }
    // returns the spare capacity of the university

    public int spotsLeft() {
        return (this.UniCapacity - this.AmountOfStudentsAllocated);
    }
    // returns the GPA requirement of the university

    public double getGPAReq() {
        return this.GPARequirement;
    }
    // returns any other requirements of the university

    public String getOtherReq() {
        return this.OtherRequirement;
    }

    public boolean hasEquallyWeightedReq() {
        return (this.equallyWeightedRequirement != 0);
    }
    
    public double getStudyAbroadReq() {
        return (this.studyAbroadGPA);
    }

    public double getEquallyWeightedReq() {
        return this.equallyWeightedRequirement;
    }
    
    public String getComparisonName(){
        return this.universityComparisonName;
    }

    public boolean hasGPAReq() {
        return (this.GPARequirement != 0);
    }

    public boolean hasOtherReq() {
        return (!this.OtherRequirement.isEmpty());
    }
    
    public boolean hasSpace() {
        return (this.AmountOfStudentsAllocated < this.UniCapacity);
    }
    
    public String getAverageRequirement() {
        if (hasGPAReq()) return ("GPA " + String.valueOf(this.GPARequirement).replace(".",","));
        if (hasSAReq()) return (String.valueOf(this.studyAbroadGPA).replace(".",","));
        if (hasEquallyWeightedReq()) return (String.valueOf(this.equallyWeightedRequirement).replace(".",",") + " overall average grade");
        return ("");
    }
    
    public String getAlternativeName() {
        return this.alternativeName;
    }
    
    public boolean hasAlternativeName() {
        return (!this.alternativeName.equals(""));
    }

    public String toString() {
        return this.UniversityName;
    }

    /*
  // return string representation of this student record
  public String toString() {
    if ( this.spotsLeft() !=0) {
      // Grammar is important
      if (this.spotsLeft() == 1) {
        return ( this.UniversityName + " has space for " + this.UniCapacity
                  + " student(s). The number of students currently allocated are " + this.AmountOfStudentsAllocated + "."
                  + " Therefore, there is " + this.spotsLeft() + " place still open at the university."
               + " The requirements for this university are " + this.GPARequirement + " " + this.OtherRequirement);
      } 
      // Grammar is important
      else {
        return ( this.UniversityName + " has space for " + this.UniCapacity
                  + " student(s). The number of students currently allocated are " + this.AmountOfStudentsAllocated + "."
                  + " Therefore, there are " + this.spotsLeft() + " places still open at the university."
                              + " The requirements for this university are " + this.GPARequirement + " " + this.OtherRequirement);
      }
    }
    // Only happens if spotsLeft == 0 so no space.
    else {
      return (this.UniversityName + " has space for " + this.UniCapacity + " student(s)." +
              " This university is full. The requirements for this university are " + this.GPARequirement + " " + this.OtherRequirement);
    }
  }
     */
}
