package refactoring.app.chapter11.replaceFunctionWithCommand;

public class Example {
    public int score(Candidate candidate, MedicalExample medicalExample, ScoringGuide scoringGuide) {
        return new Score(candidate, medicalExample, scoringGuide).execute();
    }
}
