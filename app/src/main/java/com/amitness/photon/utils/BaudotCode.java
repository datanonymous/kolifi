package com.amitness.photon.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Amit on 7/24/17.
 */

public class BaudotCode {

    private Map<Character, String> encodingMap = new HashMap<Character, String>();
    private Map<String, Character> decodingMap = new HashMap<String, Character>();
    private final int bitsPerLetter = 5;

    public String getStartBits() {
        return startBits;
    }

    public String getStopBits() {
        return stopBits;
    }

    private final String startBits = "10000";
    private final String stopBits = "00000";

    public BaudotCode() {
        encodingMap.put('A', "00011");
        encodingMap.put('B', "11001");
        encodingMap.put('C', "01110");
        encodingMap.put('D', "01001");
        encodingMap.put('E', "00001");
        encodingMap.put('F', "01101");
        encodingMap.put('G', "11010");
        encodingMap.put('H', "10100");
        encodingMap.put('I', "00110");
        encodingMap.put('J', "01011");
        encodingMap.put('K', "01111");
        encodingMap.put('L', "10010");
        encodingMap.put('M', "11100");
        encodingMap.put('N', "01100");
        encodingMap.put('O', "11000");
        encodingMap.put('P', "10110");
        encodingMap.put('Q', "10111");
        encodingMap.put('R', "01010");
        encodingMap.put('S', "00101");
        encodingMap.put('T', "10000");
        encodingMap.put('U', "00111");
        encodingMap.put('V', "11110");
        encodingMap.put('W', "10111");
        encodingMap.put('X', "11101");
        encodingMap.put('Y', "10101");
        encodingMap.put('Z', "10001");
        for (Map.Entry<Character, String> entry : encodingMap.entrySet()) {
            decodingMap.put(entry.getValue(), entry.getKey());
        }
    }

    private String encodeLetter(Character check) {
        return encodingMap.get(check);
    }

    private Character decodeLetter(String binaryLetter) {
        return decodingMap.get(binaryLetter);
    }

    public String encode(String text) {
        String payloadStream = "";
        for (char c : text.toCharArray()) {
            String encodedLetter = encodeLetter(c);
            payloadStream += encodedLetter;
        }
        return payloadStream;
    }

    public String decode(String bitStream) {
        String text = "";
        List<String> strings = new ArrayList<String>();
        int index = 0;
        while (index < bitStream.length()) {
            strings.add(bitStream.substring(index, Math.min(index + bitsPerLetter, bitStream.length())));
            index += bitsPerLetter;
        }

        for (String s : strings) {
            text += decodeLetter(s);
        }

        return text;
    }

    public String getBitStream(String message) {
        return startBits + encode(message) + stopBits;
    }
}
