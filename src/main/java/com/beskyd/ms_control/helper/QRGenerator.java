package com.beskyd.ms_control.helper;

import java.io.IOException;

public interface QRGenerator {
	public byte[] getPdfBytes() throws IOException, QRGenerationException;
}
