package refactoring.app.chapter09.changeReferenceToValue;

public class Person {
    TelephoneNumber telephoneNumber;

    public Person(String areaCode, String number) {
        telephoneNumber = new TelephoneNumber(areaCode, number);
    }

    public String getOfficeAreaCode() { return telephoneNumber.areaCode; }
    public void setOfficeAreaCode(String areaCode) { telephoneNumber = new TelephoneNumber(areaCode, telephoneNumber.number); }

    public String getOfficeNumber() { return telephoneNumber.number; }
    public void setOfficeNumber(String number ) { telephoneNumber = new TelephoneNumber(telephoneNumber.areaCode, number); }
}
