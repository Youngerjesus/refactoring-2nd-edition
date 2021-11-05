package refactoring.app.chapter11.replaceFunctionWithCommand;

public class Score {
    Candidate candidate;
    MedicalExample medicalExample;
    ScoringGuide scoringGuide;

    public Score(Candidate candidate, MedicalExample medicalExample, ScoringGuide scoringGuide) {
        this.candidate = candidate;
        this.medicalExample = medicalExample;
        this.scoringGuide = scoringGuide;
    }

    public int execute() {
        int result = 0;
        int healthLevel = 0;
        boolean highMedicalRiskFlag = false;

        if (medicalExample.isSmoker) {
            healthLevel += 10;
            highMedicalRiskFlag = true;
        }

        String certificationGrade = "regular";
        if (scoringGuide.stateWithLowCertification(candidate.originalState)) {
            certificationGrade = "low";
            result -= 5;
        }

        // 비슷한 코드가 한 참 이어짐
        result -= Math.max(healthLevel - 5, 0);
        return result;
    }
}
