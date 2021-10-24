package refactoring.app.chapter09.changeReferenceToValue;

public class TelephoneNumber {
    String areaCode;
    String number;

    public TelephoneNumber(String areaCode, String number) {
        this.areaCode = areaCode;
        this.number = number;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public String getNumber() {
        return number;
    }
}
