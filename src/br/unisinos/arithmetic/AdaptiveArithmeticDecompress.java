/* 
 * Reference arithmetic coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-arithmetic-coding
 * https://github.com/nayuki/Reference-arithmetic-coding
 */
package br.unisinos.arithmetic;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Decompression application using adaptive arithmetic coding.
 * <p>Usage: java AdaptiveArithmeticDecompress InputFile OutputFile</p>
 * <p>This decompresses files generated by the "AdaptiveArithmeticCompress" application.</p>
 */
public class AdaptiveArithmeticDecompress {
	
	public static void main(String[] args) throws IOException {
		// Handle command line arguments
		if (args.length != 2) {
			System.err.println("Usage: java AdaptiveArithmeticDecompress InputFile OutputFile");
			System.exit(1);
			return;
		}
		File inputFile  = new File(args[0]);
		File outputFile = new File(args[1]);
		
		// Perform file decompression
		try (BitInputStream in = new BitInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
				OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {
			decompress(in, out);
		}
	}
	
	
	// To allow unit testing, this method is package-private instead of private.
	static void decompress(BitInputStream in, OutputStream out) throws IOException {
		FlatFrequencyTable initFreqs = new FlatFrequencyTable(257);
		FrequencyTable freqs = new SimpleFrequencyTable(initFreqs);
		ArithmeticDecoder dec = new ArithmeticDecoder(32, in);
		while (true) {
			// Decode and write one byte
			int symbol = dec.read(freqs);
			if (symbol == 256)  // EOF symbol
				break;
			out.write(symbol);
			freqs.increment(symbol);
		}
	}
	
}
