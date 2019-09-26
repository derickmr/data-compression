package br.unisinos.executable;

import br.unisinos.CRC.CRC;
import br.unisinos.LZ77.LZ77;
import br.unisinos.arithmetic.ArithmeticCompress;
import br.unisinos.arithmetic.BitInputStream;
import br.unisinos.arithmetic.BitOutputStream;
import br.unisinos.arithmetic.FrequencyTable;

import java.io.*;

import static br.unisinos.arithmetic.ArithmeticCompress.writeFrequencies;
import static br.unisinos.arithmetic.ArithmeticDecompress.decompress;
import static br.unisinos.arithmetic.ArithmeticDecompress.readFrequencies;

public class Executable {
    public void execute (String filename) throws FileNotFoundException {
        LZ77 compressor = new LZ77();
        File dest = null;
        CRC crc = new CRC();

        File input = new File (filename);
        dest = new File(filename + "-compressedLZ77");
        //compressao com LZ77
        try {
            compressor.encode(input, dest);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArithmeticCompress arithmeticCompress = new ArithmeticCompress();

        File inputFile  = new File(filename + "-compressedLZ77");
        File outputFile = new File(filename + "-compressedFinal");

        // Read input file once to compute symbol frequencies
        FrequencyTable freqs = null;
        try {
            freqs = ArithmeticCompress.getFrequencies(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        freqs.increment(256);  // EOF symbol gets a frequency of 1

        // Compressao com compressao aritmetica, chegando na compressao final
        try (InputStream in = new BufferedInputStream(new FileInputStream(inputFile));
             BitOutputStream out = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)))) {
            writeFrequencies(out, freqs);
            arithmeticCompress.compress(freqs, in, out);
        } catch (Exception e){
            e.printStackTrace();
        }

        //Calculo CRC8 dos primeiros 8 bytes do arquivo
        File crcFile = new File("crc");
        FileInputStream fileInputStream = new FileInputStream(outputFile);
        String result;
        try {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream))) {

                result = "";
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                byte[] resultBytes = result.getBytes();
                byte[] stringBytes = new byte[8];

                for (int i = 0; i < 8; i++){
                    stringBytes[i] = resultBytes[i];
                }

                String crcString = new String(stringBytes);

                FileOutputStream fileOutputStream = new FileOutputStream(crcFile);
                fileOutputStream.write(crc.calculateCRC(crcString).getBytes());
                fileOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File inputFileCompressed  = outputFile;

        fileInputStream = new FileInputStream(crcFile);

        //Verificacao do CRC no lado do decoder
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream))){
            result = "";
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }

            if (!crc.isCRCValid(result)) {
                System.err.println("CRC dos primeiros 8 bytes não valido!");
            }
            else {
                System.out.println("CRC dos primeiros 8 bytes valido!");
            }

            crcFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Descompressao aritmetica
        File outputFileUncompressed = new File(filename + "-uncompressedArithmetic");

        try (BitInputStream in = new BitInputStream(new BufferedInputStream(new FileInputStream(inputFileCompressed)));
             OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFileUncompressed))) {
            freqs = readFrequencies(in);
            decompress(freqs, in, out);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // Descompressao LZ77, chegando no arquivo original
        File uncompressedArithmetic = new File (filename + "-uncompressedArithmetic");
        File uncompressedFinal = new File (filename + "-uncompressed");

        try {
            compressor.decode(uncompressedArithmetic, uncompressedFinal, input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //delete middle files
//        uncompressedArithmetic.delete();
//        inputFileCompressed.delete();
    }

    public static void main (String[] args){
        Executable executable = new Executable();
        try {
            System.out.println("Iniciando compressao e descompressao para o arquivo alice29.txt");
            executable.execute("alice29.txt");
            System.out.println("Compressao e descompressao finalizada para o arquivo alice29.txt!");
            System.out.println("Iniciando compressao e descompressao para o arquivo sum");
            executable.execute("sum");
            System.out.println("Compressao e descompressao finalizada para o arquivo sum!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
