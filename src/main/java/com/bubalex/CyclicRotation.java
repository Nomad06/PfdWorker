package com.bubalex;

public class CyclicRotation {
    public static boolean isCyclicRotation(String example, String rotation) {
        if(example == null || rotation == null || example.length() != rotation.length()) return false;
        char firstSymbol = example.charAt(0);
        int firstSymbolEncounter = rotation.indexOf(firstSymbol);

        while(firstSymbolEncounter != -1) {
            boolean isCyclic = findSimilarity(example, rotation, firstSymbolEncounter);
            if (isCyclic) return true;
            firstSymbolEncounter = rotation.indexOf(firstSymbol, firstSymbolEncounter + 1);
        }
        return false;
    }

    private static boolean findSimilarity(String example, String rotation, int index) {
        int firstPartLength = rotation.length() - index;
        String partExample = rotation.substring(index);
        String partRotation = example.substring(0, firstPartLength);
        if (!partExample.equals(partRotation)) return false;

        partRotation = rotation.substring(0, index);
        partExample = example.substring(firstPartLength);

        return partExample.equals(partRotation);
    }
}
