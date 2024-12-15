import java.util.*;

public class MinimumSetCover {

    // Exception: Không thể phủ tập
    static class NoSolutionException extends Exception {
        public NoSolutionException(String message) {
            super(message);
        }
    }

    // Exception: Đầu vào không hợp lệ
    static class InvalidInputException extends Exception {
        public InvalidInputException(String message) {
            super(message);
        }
    }

    public static void main(String[] args) {
        try {
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
            // Lặp qua từng bộ dữ liệu và kiểm tra
            for (int i = 0; i < testSets.length; i++) {
                try {
                    System.out.println("Ví dụ " + (i + 1) + ":");
                    int U = universes[i];
                    int[][] sets = testSets[i];

                    SetCoverResult result = findMinimumSetCover(U, sets);
                    System.out.println("Các tập con đã chọn: " + result.selectedSets);
                    System.out.println("Số lượng tập con: " + result.selectedSets.size());
                    System.out.println("Các phần tử đã được phủ: " + result.coveredElements);
                    System.out.println();
                } catch (NoSolutionException | InvalidInputException e) {
                    System.err.println("Lỗi: " + e.getMessage());
                    System.out.println();
                }
            }

        } catch (Exception e) {
            System.err.println("Lỗi tổng quát: " + e.getMessage());
        }
    }

    // Lớp để chứa kết quả đầu ra
    static class SetCoverResult {
        List<Integer> selectedSets; // Chỉ số của các tập đã chọn
        Set<Integer> coveredElements; // Các phần tử đã được phủ

        public SetCoverResult(List<Integer> selectedSets, Set<Integer> coveredElements) {
            this.selectedSets = selectedSets;
            this.coveredElements = coveredElements;
        }
    }

    // Hàm tìm phủ tập nhỏ nhất
    public static SetCoverResult findMinimumSetCover(int U, int[][] sets)
            throws NoSolutionException, InvalidInputException {

        // Kiểm tra đầu vào hợp lệ
        validateInput(U, sets);

        int n = sets.length; // Số tập hợp
        int fullMask = (1 << U) - 1; // Trạng thái đầy đủ (tất cả phần tử được phủ)

        // Chuyển đổi các tập hợp thành dạng bitmask
        int[] setMasks = new int[n];
        for (int i = 0; i < n; i++) {
            for (int element : sets[i]) {
                setMasks[i] |= (1 << element);
            }
        }

        // Mảng dp: khởi tạo với giá trị lớn (tương đương vô cùng)
        int[] dp = new int[1 << U];
        int[] parent = new int[1 << U]; // Lưu tập hợp nào đã dẫn đến trạng thái hiện tại
        Arrays.fill(dp, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        dp[0] = 0; // Không cần tập hợp nào để phủ tập rỗng

        // Quy hoạch động
        for (int mask = 0; mask <= fullMask; mask++) {
            if (dp[mask] == Integer.MAX_VALUE) continue; // Bỏ qua trạng thái không thể đạt được
            for (int i = 0; i < n; i++) {
                int newMask = mask | setMasks[i]; // Hợp trạng thái hiện tại với tập hợp
                if (dp[newMask] > dp[mask] + 1) {
                    dp[newMask] = dp[mask] + 1;
                    parent[newMask] = i; // Lưu chỉ số tập hợp đã chọn
                }
            }
        }

        // Nếu không thể phủ toàn bộ tập U
        if (dp[fullMask] == Integer.MAX_VALUE) {
            throw new NoSolutionException("Không thể phủ tập U bằng các tập con đã cho.");
        }

        // Truy vết lại các tập hợp đã chọn
        List<Integer> selectedSets = new ArrayList<>();
        int currentMask = fullMask;
        while (currentMask != 0) {
            int chosenSet = parent[currentMask];
            if (chosenSet == -1) break;
            selectedSets.add(chosenSet);
            currentMask &= ~setMasks[chosenSet]; // Loại bỏ tập hợp khỏi trạng thái
        }

        // Tập hợp các phần tử đã được bao phủ
        Set<Integer> coveredElements = new HashSet<>();
        for (int setIndex : selectedSets) {
            for (int element : sets[setIndex]) {
                coveredElements.add(element);
            }
        }

        return new SetCoverResult(selectedSets, coveredElements);
    }

    // Hàm kiểm tra đầu vào hợp lệ
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
