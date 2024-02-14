import java.util.List;

public class MetricsHelper {

    public static void computeMetrics(List<Integer> ranks, int nrQuestions) {
        double pAt1 = calculatePrecision(ranks, nrQuestions, 1);
        double pAt5 = calculatePrecision(ranks, nrQuestions, 5);
        double MRR = calculateMeanReciprocalRank(ranks, nrQuestions);

        System.out.printf("Precision at 1: %f\n", pAt1);
        System.out.printf("Precision at 5: %f\n", pAt5);
        System.out.printf("Mean Reciprocal Rank: %f\n", MRR);
    }

    private static double calculatePrecision(List<Integer> ranks, int nrQuestions, int k) {
        long nrCorrectItems = ranks.stream().filter(rank -> rank > 0 && rank <= k).count();
        return (double) nrCorrectItems / nrQuestions;
    }

    private static double calculateMeanReciprocalRank(List<Integer> ranks, int nrQuestions) {
        double sumMRR = ranks.stream().mapToDouble(rank -> 1.0 / rank).sum();
        return sumMRR / nrQuestions;
    }
}
