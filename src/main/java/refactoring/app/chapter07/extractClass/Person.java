package refactoring.app.chapter07.extractClass;

public class Person {
    protected String name;
    protected TelephoneNumber telephoneNumber;

    public Person() {
        telephoneNumber = new TelephoneNumber();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getOfficeAreaCode() {
        return telephoneNumber.getAreaCode();
    }

    public void setOfficeAreaCode(String officeAreaCode) {
        this.telephoneNumber.setAreaCode(officeAreaCode);
    }

    public String getOfficeNumber() {
        return this.telephoneNumber.getNumber();
    }

    public void setOfficeNumber(String officeNumber) {
        this.telephoneNumber.setNumber(officeNumber);
    }
}
