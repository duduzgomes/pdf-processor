package com.pdfprocessor.pdf_worker_service.services;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.awt.image.BufferedImage;
import java.io.InputStream;

@Slf4j
@Service
public class OcrService {

    @Value("${tesseract.data-path:tessdata}")
    private String tessDataPath;

    @Value("${tesseract.language:por}")
    private String language;

    // DPI da renderização — 300 é o mínimo para OCR de qualidade
    private static final int RENDER_DPI = 300;

    public boolean containsName(InputStream pdfStream, String expectedName) {
        String extractedText = extractText(pdfStream);

        log.info("Texto extraído com {} caracteres", extractedText.length());
        log.debug("Texto extraído: {}", extractedText);

        // Normaliza os dois textos para comparação case-insensitive
        // e remove espaços extras que o OCR pode introduzir
        String normalizedText = normalizeText(extractedText);
        String normalizedName = normalizeText(expectedName);

        boolean found = normalizedText.contains(normalizedName);
        log.info("Nome '{}' {} no documento", expectedName, found ? "encontrado" : "não encontrado");

        return found;
    }

    private String extractText(InputStream pdfStream) {
        try {
            // Lê o stream uma única vez em memória
            byte[] pdfBytes = pdfStream.readAllBytes();

            // Tenta extrair texto embutido
            String embeddedText = tryExtractEmbeddedText(pdfBytes);
            if (embeddedText != null && !embeddedText.isBlank()) {
                log.info("Texto extraído diretamente do PDF (sem OCR necessário)");
                return embeddedText;
            }

            // PDF escaneado — usa OCR com os mesmos bytes
            log.info("PDF não contém texto embutido — iniciando OCR");
            return extractTextWithOcr(pdfBytes);

        } catch (Exception e) {
            log.error("Erro ao ler stream do PDF: {}", e.getMessage());
            throw new RuntimeException("Falha ao ler o arquivo PDF", e);
        }
    }

    private String tryExtractEmbeddedText(byte[] pdfBytes) {
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (Exception e) {
            log.warn("Não foi possível extrair texto embutido: {}", e.getMessage());
            return null;
        }
    }

    private String extractTextWithOcr(byte[] pdfBytes) {
        Tesseract tesseract = buildTesseract();
        StringBuilder fullText = new StringBuilder();

        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFRenderer renderer = new PDFRenderer(document);
            int pageCount = document.getNumberOfPages();

            log.info("Iniciando OCR em {} página(s)", pageCount);

            for (int page = 0; page < pageCount; page++) {
                log.debug("Processando página {}/{}", page + 1, pageCount);

                BufferedImage image = renderer.renderImageWithDPI(
                        page, RENDER_DPI, ImageType.RGB
                );

                String pageText = tesseract.doOCR(image);
                fullText.append(pageText).append("\n");
            }

        } catch (TesseractException e) {
            log.error("Erro no Tesseract durante OCR: {}", e.getMessage());
            throw new RuntimeException("Falha no processamento OCR", e);
        } catch (Exception e) {
            log.error("Erro ao processar PDF: {}", e.getMessage());
            throw new RuntimeException("Falha ao processar o documento", e);
        }

        return fullText.toString();
    }   

    private Tesseract buildTesseract() {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tessDataPath);
        tesseract.setLanguage(language);
        // PSM 3 = modo automático de segmentação de página
        tesseract.setPageSegMode(3);
        // OEM 3 = usa LSTM (rede neural) + legado combinados
        tesseract.setOcrEngineMode(3);
        return tesseract;
    }

    private String normalizeText(String text) {
        return text
                .toLowerCase()
                .replaceAll("\\s+", " ") // múltiplos espaços viram um
                .trim();
    }
}
