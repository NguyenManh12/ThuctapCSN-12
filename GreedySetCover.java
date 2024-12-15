import java.util.*;

public class GreedySetCover {

    // Ngoại lệ: Không thể phủ tập hợp
    static class NoSolutionException extends Exception {
        public NoSolutionException(String message) {
            super(message);
        }
    }

    // Ngoại lệ: Đầu vào không hợp lệ
    static class InvalidInputException extends Exception {
        public InvalidInputException(String message) {
            super(message);
        }
    }

    public static void main(String[] args) {
        // Định nghĩa các bộ kiểm tra
        int[][][] testSets = {
                { // Test Case 1: Có thể phủ tập hợp
                        {0, 1, 2}, // S1
                        {2, 3},    // S2
                        {3, 4},    // S3
                        {0, 4}     // S4
                },
                { // Test Case 2: Không thể phủ tập hợp
                        {0, 1},    // S1
                        {2, 3},    // S2
                        {3, 4}     // S3
                },
                { // Test Case 3: Đầu vào không hợp lệ
                        {0, 1, 6}, // S1 (chứa phần tử không hợp lệ 6)
                        {2, 3},    // S2
                        {3, 4}     // S3
                }
        };
        int[] universes = {5, 6, 5}; // Kích thước của các tập U cho mỗi bộ kiểm tra

        // Lặp qua các bộ kiểm tra
        for (int testCase = 0; testCase < testSets.length; testCase++) {
            System.out.println("=== Test Case " + (testCase + 1) + " ===");
            try {
                int U = universes[testCase];
                int[][] sets = testSets[testCase];
                SetCoverResult result = findMinimumSetCover(U, sets);

                // In kết quả
                System.out.println("Các tập con đã chọn: " + result.selectedSets);
                System.out.println("Số lượng tập con: " + result.selectedSets.size());
                System.out.println("Các phần tử đã được phủ: " + result.coveredElements);
            } catch (NoSolutionException | InvalidInputException e) {
                System.err.println("Lỗi: " + e.getMessage());
            }
            System.out.println();
        }
    }

    // Lớp lưu trữ kết quả
    static class SetCoverResult {
        List<Integer> selectedSets; // Các chỉ số của tập con đã chọn
        Set<Integer> coveredElements; // Các phần tử đã được phủ

        public SetCoverResult(List<Integer> selectedSets, Set<Integer> coveredElements) {
            this.selectedSets = selectedSets;
            this.coveredElements = coveredElements;
        }
    }

    // Thuật toán tham lam tìm phủ tập hợp nhỏ nhất
    public static SetCoverResult findMinimumSetCover(int U, int[][] sets)
            throws NoSolutionException, InvalidInputException {

        // Kiểm tra đầu vào hợp lệ
        validateInput(U, sets);

        Set<Integer> uncovered = new HashSet<>();
        for (int i = 0; i < U; i++) uncovered.add(i); // Khởi tạo các phần tử chưa được phủ

        List<Integer> selectedSets = new ArrayList<>();
        Set<Integer> coveredElements = new HashSet<>();
        while (!uncovered.isEmpty()) {
            int bestSetIndex = -1;
            int maxCover = 0;
            // Tìm tập hợp phủ được nhiều phần tử chưa được phủ nhất
            for (int i = 0; i < sets.length; i++) {
                int currentCover = 0;
                for (int element : sets[i]) {
                    if (uncovered.contains(element)) {
                        currentCover++;
                    }
                }
                if (currentCover > maxCover) {
                    maxCover = currentCover;
                    bestSetIndex = i;
                }
            }

            // Nếu không tìm thấy tập hợp phù hợp, không thể phủ tập hợp
            if (bestSetIndex == -1) {
                throw new NoSolutionException("Không thể phủ tập U bằng các tập con đã cho.");
            }

            // Thêm tập hợp tốt nhất vào kết quả
            selectedSets.add(bestSetIndex);
            for (int element : sets[bestSetIndex]) {
                uncovered.remove(element); // Đánh dấu phần tử là đã được phủ
                coveredElements.add(element); // Thêm phần tử vào tập phủ
            }
        }

        return new SetCoverResult(selectedSets, coveredElements);
    }

    // Kiểm tra đầu vào hợp lệ
    public static void validateInput(int U, int[][] sets) throws InvalidInputException {
        for (int[] set : sets) {
            for (int element : set) {
                if (element < 0 || element >= U) {
                    throw new InvalidInputException("Phần tử " + element + " không thuộc tập U.");
                }
            }
        }
    }
}
