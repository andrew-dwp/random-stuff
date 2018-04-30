import java.util.ArrayList;
import java.util.Arrays;

/**
 * Given a list of seats already taken and a number of rows, calculate the number of 3 person families that can be still accommodated in a theater with the
 * configuration ABC | DEFG | HJK.  A group may not cross an aisle and must be in contiguous seats in a row.
 *
 * The below is a solution to this problem using Java Streams.
 *
 * Contains a manual test suite for confirmation.
 */
public class HowManySeatsLeft {

    private static final String REGEX_DISCOUNT_ABC = "[ABC]{1,}";
    private static final String REGEX_DISCOUNT_ABC_MANY = "^.*[ABC].*${1,}";
    private static final String REGEX_DISCOUNT_HJK = "[HJK]{1,}";
    private static final String REGEX_DISCOUNT_HJK_MANY = "^.*[HJK].*${1,}";
    private static final String REGEX_DISCOUNT_EF = "[EF]{1,}";
    private static final String REGEX_DISCOUNT_EF_MANY = "^.*[EF].*${1,}";
    private static final int MAX_BLOCKS_PER_ROW = 3;

    public static void main(String[] args) {
        String bigOne = "";

        for (int i = 1; i <= 50; i++){
            bigOne = bigOne+" "+i+"A"+" "+i+"B"+" "+i+"C"+" "+i+"D"+" "+i+"E"+" "+i+"F"+" "+i+"G"+" "+i+"H"+" "+i+"J"+" "+i+"K";
        }

        int test0 = 0;

        if (getSetsOfSeats(50, bigOne) == test0 ){
            System.out.println("Test 0 Seats Available was correct");
        } else {
            System.out.println("Test 0 Failed");
        }

        int test1 = 5;

        if (getSetsOfSeats(3, "1A 1B 1C 2F 3G 3E 3D 2D 2G 20G 0A 1H 1J") == test1 ){
            System.out.println("Test 1 Seats Available was correct");
        } else {
            System.out.println("Test 1 Failed");
        }

        int test2 = 2;

        if (getSetsOfSeats(1, "1A") == test2 ){
            System.out.println("Test 2 Seats Available was correct");
        } else {
            System.out.println("Test 2 Failed");
        }

        int test3 = 9;

        if (getSetsOfSeats(3, "") == test3 ){
            System.out.println("Test 3 Seats Available was correct");
        } else {
            System.out.println("Test 3 Failed");
        }

        int test4 = 99;

        if (getSetsOfSeats(34, "1A 1D 1G 50H 25D 25G") == test4 ){
            System.out.println("Test 4 Seats Available was correct");
        } else {
            System.out.println("Test 4 Failed");
        }

        int test5 = 0;

        if (getSetsOfSeats(3, "1A 1E 1H 2A 2E 2H 3A 3E 3H 1B 1C") == test5 ){
            System.out.println("Test 5 Seats Available was correct");
        } else {
            System.out.println("Test 5 Failed");
        }

        int test6 = 9;

        if (getSetsOfSeats(3, "1M") == test6 ){
            System.out.println("Test 6 Seats Available was correct");
        } else {
            System.out.println("Test 6 Failed");
        }

        int test7 = 0;

        if (getSetsOfSeats(3, "1A 1B 1C 3B 3C 3D 3E 1K 2A 2B 2C 2D 2E 2F 2G 2H 1D 1E 1F 1G 1H 1J 2J 2K 3A 3F 3G 3H 3J 3K") == test7 ){
            System.out.println("Test 7 Seats Available was correct");
        } else {
            System.out.println("Test 7 Failed");
        }

        int test8 = 3;

        if (getSetsOfSeats(1, "") == test8 ){
            System.out.println("Test 8 Seats Available was correct");
        } else {
            System.out.println("Test 8 Failed");
        }
    }

    private static int getSetsOfSeats(int N, String S) {
        int totalBlocks = MAX_BLOCKS_PER_ROW * N;
        ArrayList<String> lastSeatsInRowAL = new ArrayList<>();
        ArrayList<Integer> countSeatsAllocations = new ArrayList<>();
        ArrayList<String> lastSeat = new ArrayList<>();
        lastSeatsInRowAL.add("");
        countSeatsAllocations.add(totalBlocks);
        lastSeat.add("");


        //This stream:
        // * breaks down the string into seats
        // * does some clean up:
        // ** removes duplicates
        // ** removes any invalid seat allocations greater than number of rows and less than 0 (assuming no row 0)
        // * and orders the list
        if (!S.isEmpty()) {
            Arrays.stream(S.trim().split(" "))
                    .distinct()
                    .filter(seat -> ((Integer.parseInt(seat.substring(0, seat.length() - 1)) <= N) && (Integer.parseInt(seat.substring(0, seat.length() - 1)) > 0)))
                    .sorted() //this orders the row A-K even if the rows are not sequential
                    .forEach(seat -> {
                        int blocksAvailable = countSeatsAllocations.get(0);

                        //if new row reset seat history
                        if (!lastSeatsInRowAL.get(0).isEmpty() && !lastSeatsInRowAL.get(0).substring(0, lastSeat.get(0).length() - 1).equals(seat.substring(0, seat.length() - 1))) {
                            lastSeatsInRowAL.set(0, "");
                        }

                        if (seat.substring(seat.length() - 1).matches(REGEX_DISCOUNT_ABC) && !lastSeatsInRowAL.get(0).matches(REGEX_DISCOUNT_ABC_MANY)) {
                            blocksAvailable--;
                        } else if (seat.substring(seat.length() - 1).matches(REGEX_DISCOUNT_HJK) && !lastSeatsInRowAL.get(0).matches(REGEX_DISCOUNT_HJK_MANY)) {
                            blocksAvailable--;
                        } else if (seat.substring(seat.length() - 1).matches(REGEX_DISCOUNT_EF) && !lastSeatsInRowAL.get(0).matches(REGEX_DISCOUNT_EF_MANY)) {
                            blocksAvailable--;
                        } else if (seat.contains("G") && lastSeatsInRowAL.get(0).contains("D") && !lastSeatsInRowAL.get(0).matches(REGEX_DISCOUNT_EF_MANY)) { //don't need to check other way around as finding a g will also discount d scenario
                            blocksAvailable--;
                        }
                        // below is for watching for a change from 2 digit seat number to one digit seat number
                        lastSeat.set(0, seat);
                        //count available to next iteration
                        countSeatsAllocations.set(0, blocksAvailable);
                        //add new row seat to list
                        lastSeatsInRowAL.set(0, lastSeatsInRowAL.get(0).concat(seat));
                    });
        }

        return countSeatsAllocations.get(0);
    }
}



