# Medical-Records
Spring Boot REST API

Create REST API to handle the processes that concern patients' visits to doctors and keep track of the history of diseases. 

The doctor has a name, birth date, a Specialty (Pediatrics, Surgery, Cardiologist), and can be a general practitioner. 

The patient has a name and pays for health insurance. 

The patient has to choose GP (one of the doctors who is a GP) and a patient can be examined by a doctor. The doctor has to give diagnosis the patient.

When the patient visits the doctor there is a fee. If the patient has health insurance, he/she doesn't have to pay the fee otherwise, the patient has to pay the fee. This fee is the same for all patients and all doctors. 

Implement the CRUD operations of the patients, doctors, and visits, plus the following functionalities:

1. List of all the patients, who have health insurance
2. The percentage of patients who have no insurance (in accordance of the total number of patients) 
3. The total income of all the visits of all patients to all doctors
4. The income from visits fees of a particular doctor 
5. The number of visits of a given patient
6. The number of visits by a diagnosis
7. The number of doctors who have income greater than a given one
8. The total income of visits, by a given diagnosis
9. The total income of patients who have no health insurance
10. The income of a concrete doctor of patients who have health insurance
