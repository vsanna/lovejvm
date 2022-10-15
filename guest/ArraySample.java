package dev.ishikawa.test;

class ArraySample {
    static public void main() {
        int[] intArray = new int[]{1,2,3,4,5}; // 104
        int[] intArray2 = new int[10]; // 105

        int a0 = intArray[0]; // 1
        int a1 = intArray[1]; // 2
        int a2 = intArray[2]; // 3
        int a3 = intArray[3]; // 4
        int a4 = intArray[4]; // 5

        int b0 = intArray2[0]; // 0
        int b1 = intArray2[1]; // 0
        int b2 = intArray2[2]; // 0
        int b3 = intArray2[3]; // 0
        int b4 = intArray2[4]; // 0

        ArraySample[] asArray = new ArraySample[]{new ArraySample(), new ArraySample()}; // 107
        ArraySample[] asArray2 = new ArraySample[2]; // 110

        ArraySample as0 = asArray[0]; // 108
        ArraySample as1 = asArray[1]; // 109

        ArraySample bs0 = asArray2[0]; // 0
        ArraySample bs1 = asArray2[1]; // 0


        int[][] intArrArray = new int[][]{new int[]{1,2}, new int[]{3, 4}}; // 112
        int[][] intArrArray2 = new int[10][2]; // 115

        int[] aa0 = intArrArray[0]; // 113
        int[] aa1 = intArrArray[1]; // 114

        int[] bb0 = intArrArray2[0]; // 116
        int[] bb1 = intArrArray2[1]; // 117
        int[] bb2 = intArrArray2[3]; // 119
    }
}