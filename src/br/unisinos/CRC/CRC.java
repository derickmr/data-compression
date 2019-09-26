package br.unisinos.CRC;

import java.util.Arrays;

public class CRC {

    private static final String GENERATOR = "11111";

    public String getGenerator () {
        return GENERATOR;
    }

    public String stringToBinaryString (String word) {
        String temp = word;
        byte[] bytes = word.getBytes();
        // byte[] = getBytes() Encodes this String into a sequence of bytes using the platform's default charset, storing the result into a new byte array.
        System.err.println("byte[] bytes = ");
        for(byte b : bytes){
            System.err.println(b);
        }

        StringBuilder binary = new StringBuilder();
        // The principal operations on a StringBuilder are the append and insert methods, which are overloaded so as to accept data of any type.
        // Each effectively converts a given datum to a string and then appends or inserts the characters of that string to the string builder.
        // The append method always adds these characters at the end of the builder; the insert method adds the characters at a specified point.
        for (byte b : bytes)
        {
            int val = b; // value = das ausgewählte Byte
            for (int i = 0; i < 8; i++) // da ein Byte 8 Bit hat geht man 8 mal in dem Byte durch
            {
                binary.append((val & 128) == 0 ? 0 : 1); // check if first number is a 0 or a 1 if a 0 then 0 if a 1 then 1
                // val & 128 is bit AND operator
                val <<= 1; // a zero is added at the end
            }
            binary.append(' ');
        }
        return binary.toString().replaceAll(" ", "");
    }

    public String intArrayToString (int[] array){
        String arrayString = Arrays.toString(array);
        return arrayString.substring(1, arrayString.length()-1).replaceAll(" ", "").replaceAll(",", "");
    }

    public String calculateCRC (String phrase){
        String binaryPhrase = stringToBinaryString(phrase);

        int data[] = new int[binaryPhrase.length() + GENERATOR.length() - 1];
        int divisor[] = new int[GENERATOR.length()];
        for(int i=0;i<binaryPhrase.length();i++)
            data[i] = Integer.parseInt(binaryPhrase.charAt(i)+"");
        for(int i=0;i<GENERATOR.length();i++)
            divisor[i] = Integer.parseInt(GENERATOR.charAt(i)+"");

        //Calculation of CRC
        for(int i=0;i<binaryPhrase.length();i++){
            if(data[i]==1)
                for(int j=0;j<divisor.length;j++)
                    data[i+j] ^= divisor[j];
        }

        for(int i=0;i<binaryPhrase.length();i++)
            data[i] = Integer.parseInt(binaryPhrase.charAt(i)+"");

        return intArrayToString(data);
    }

    public boolean isCRCValid (String crc){
        int[] data = new int[crc.length() + GENERATOR.length() - 1];
        int[] divisor = new int[GENERATOR.length()];
        for(int i=0;i<crc.length();i++)
            data[i] = Integer.parseInt(crc.charAt(i)+"");
        for(int i=0;i<GENERATOR.length();i++)
            divisor[i] = Integer.parseInt(GENERATOR.charAt(i)+"");

        //Calculation of remainder
        for(int i=0;i<crc.length();i++){
            if(data[i]==1)
                for(int j=0;j<divisor.length;j++)
                    data[i+j] ^= divisor[j];
        }

        //Display validity of data
        boolean valid = true;
        for(int i=0;i<data.length;i++)
            if(data[i]==1){
                valid = false;
                break;
            }

        return valid;

    }
}
