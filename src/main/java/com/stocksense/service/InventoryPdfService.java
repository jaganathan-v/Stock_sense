package com.stocksense.service;

import com.stocksense.dto.ProductResponseDto;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Builds a small, dependency-free PDF from current database-backed product totals. */
@Service
public class InventoryPdfService {
    private static final int PAGE_W = 595;
    private static final int PAGE_H = 842;
    private static final int MARGIN = 42;
    private static final int CONTENT_W = PAGE_W - MARGIN * 2;
    private static final int BOTTOM = 785;
    private static final double[] INDIGO = {0.427, 0.369, 0.941};
    private static final double[] TEXT = {0.12, 0.13, 0.18};
    private static final double[] MUTED = {0.40, 0.42, 0.48};
    private static final double[] LIGHT = {0.95, 0.96, 0.98};

    private final ProductService productService;

    public InventoryPdfService(ProductService productService) { this.productService = productService; }

    public byte[] generate() {
        List<ProductResponseDto> products = productService.getAllProducts();
        int lowStock = (int) products.stream().filter(p -> p.getCurrentStock() < DashboardService.LOW_STOCK_THRESHOLD).count();
        int outOfStock = (int) products.stream().filter(p -> p.getCurrentStock() <= 0).count();
        int units = products.stream().mapToInt(p -> Math.max(0, p.getCurrentStock())).sum();
        ReportCanvas pdf = new ReportCanvas();

        pdf.text("StockSense — Inventory Report", MARGIN, 48, 22, true, INDIGO);
        pdf.text("Generated " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), MARGIN, 69, 9, false, MUTED);
        pdf.line(MARGIN, 80, PAGE_W - MARGIN, 80, INDIGO, 1.2);

        pdf.section("Summary", 103);
        int cardGap = 10;
        int cardW = (CONTENT_W - cardGap * 3) / 4;
        String[] labels = {"Products", "Total Units", "Low Stock (<10)", "Out of Stock"};
        String[] values = {Integer.toString(products.size()), Integer.toString(units), Integer.toString(lowStock), Integer.toString(outOfStock)};
        for (int i = 0; i < labels.length; i++) {
            int x = MARGIN + i * (cardW + cardGap);
            pdf.fillRect(x, 120, cardW, 54, LIGHT);
            pdf.text(labels[i], x + 8, 139, 8, false, MUTED);
            pdf.text(values[i], x + 8, 162, 17, true, TEXT);
        }

        pdf.section("Stock by Location", 201);
        TreeMap<String, List<LocationRow>> byLocation = new TreeMap<>();
        for (ProductResponseDto product : products) {
            for (Map.Entry<String, Integer> entry : product.getLocationStocks().entrySet()) {
                if (entry.getValue() != null && entry.getValue() > 0) {
                    byLocation.computeIfAbsent(entry.getKey(), key -> new ArrayList<>())
                            .add(new LocationRow(product.getName(), product.getSku(), entry.getValue()));
                }
            }
        }
        if (byLocation.isEmpty()) {
            pdf.text("No positive stock is currently recorded at any location.", MARGIN, pdf.y() + 18, 9, false, MUTED);
            pdf.y(pdf.y() + 35);
        } else {
            for (Map.Entry<String, List<LocationRow>> group : byLocation.entrySet()) {
                pdf.ensure(72);
                pdf.text(group.getKey(), MARGIN, pdf.y() + 17, 11, true, TEXT);
                pdf.y(pdf.y() + 24);
                pdf.tableHeader(new String[]{"Location", "Product", "SKU", "Quantity"}, new int[]{112, 202, 105, 92}, new boolean[]{false, false, false, true});
                List<LocationRow> rows = group.getValue();
                rows.sort(Comparator.comparing(LocationRow::product, String.CASE_INSENSITIVE_ORDER));
                int index = 0;
                for (LocationRow row : rows) {
                    if (!pdf.ensure(22)) {
                        pdf.section("Stock by Location (continued)", 48);
                        pdf.tableHeader(new String[]{"Location", "Product", "SKU", "Quantity"}, new int[]{112, 202, 105, 92}, new boolean[]{false, false, false, true});
                    }
                    pdf.tableRow(index++, new String[]{"", row.product, row.sku, Integer.toString(row.quantity)}, new int[]{112, 202, 105, 92}, new boolean[]{false, false, false, true}, null);
                }
                pdf.y(pdf.y() + 9);
            }
        }

        pdf.ensure(55);
        pdf.section("Product-wise Breakdown", pdf.y() + 18);
        pdf.tableHeader(new String[]{"Product Name", "SKU", "Category", "Total Stock", "Status"}, new int[]{145, 75, 116, 82, 93}, new boolean[]{false, false, false, true, false});
        List<ProductResponseDto> sorted = products.stream().sorted(Comparator.comparing(ProductResponseDto::getName, String.CASE_INSENSITIVE_ORDER)).toList();
        int rowIndex = 0;
        for (ProductResponseDto product : sorted) {
            if (!pdf.ensure(22)) {
                pdf.section("Product-wise Breakdown (continued)", 48);
                pdf.tableHeader(new String[]{"Product Name", "SKU", "Category", "Total Stock", "Status"}, new int[]{145, 75, 116, 82, 93}, new boolean[]{false, false, false, true, false});
            }
            int stock = product.getCurrentStock();
            String status = stock <= 0 ? "Out of Stock" : stock < DashboardService.LOW_STOCK_THRESHOLD ? "Low Stock" : "In Stock";
            double[] statusColor = stock <= 0 ? new double[]{0.75, 0.12, 0.16} : stock < DashboardService.LOW_STOCK_THRESHOLD ? new double[]{0.69, 0.39, 0.02} : new double[]{0.08, 0.48, 0.24};
            pdf.tableRow(rowIndex++, new String[]{product.getName(), product.getSku(), product.getCategory(), Integer.toString(stock), status},
                    new int[]{145, 75, 116, 82, 93}, new boolean[]{false, false, false, true, false}, statusColor);
        }
        return pdf.toBytes();
    }

    private record LocationRow(String product, String sku, int quantity) {}

    private static final class ReportCanvas {
        private final List<StringBuilder> pages = new ArrayList<>();
        private int y = MARGIN;
        private int page() { if (pages.isEmpty()) pages.add(new StringBuilder()); return pages.size() - 1; }
        private StringBuilder stream() { return pages.get(page()); }
        int y() { return y; }
        void y(int value) { y = value; }

        void text(String value, int x, int top, int size, boolean bold, double[] color) {
            String font = bold ? "F2" : "F1";
            String safe = sanitize(value);
            stream().append(String.format(java.util.Locale.US, "%.3f %.3f %.3f rg BT /%s %d Tf 1 0 0 1 %d %d Tm (%s) Tj ET\n",
                    color[0], color[1], color[2], font, size, x, PAGE_H - top, escape(safe)));
        }

        void line(int x1, int top1, int x2, int top2, double[] color, double width) {
            stream().append(String.format(java.util.Locale.US, "%.3f %.3f %.3f RG %.2f w %d %d m %d %d l S\n",
                    color[0], color[1], color[2], width, x1, PAGE_H - top1, x2, PAGE_H - top2));
        }

        void fillRect(int x, int top, int width, int height, double[] color) {
            stream().append(String.format(java.util.Locale.US, "%.3f %.3f %.3f rg %d %d %d %d re f\n",
                    color[0], color[1], color[2], x, PAGE_H - top - height, width, height));
        }

        void section(String title, int top) {
            if (!ensure(30)) top = MARGIN + 22;
            text(title, MARGIN, top, 14, true, INDIGO);
            line(MARGIN, top + 7, PAGE_W - MARGIN, top + 7, new double[]{0.84, 0.84, 0.90}, .65);
            y = top + 17;
        }

        boolean ensure(int height) {
            if (y + height <= BOTTOM) return true;
            pages.add(new StringBuilder());
            y = MARGIN;
            return false;
        }

        void tableHeader(String[] labels, int[] widths, boolean[] right) {
            ensure(26);
            fillRect(MARGIN, y, CONTENT_W, 25, new double[]{0.91, 0.90, 0.98});
            int x = MARGIN;
            for (int i = 0; i < labels.length; i++) {
                int tx = right[i] ? x + widths[i] - 8 - Math.min(labels[i].length() * 5, widths[i] - 12) : x + 8;
                text(labels[i], tx, y + 16, 8, true, TEXT);
                x += widths[i];
            }
            y += 25;
        }

        void tableRow(int index, String[] values, int[] widths, boolean[] right, double[] statusColor) {
            if (index % 2 == 1) fillRect(MARGIN, y, CONTENT_W, 22, LIGHT);
            int x = MARGIN;
            for (int i = 0; i < values.length; i++) {
                String value = clip(values[i], Math.max(5, widths[i] / 5 - 3));
                int tx = right[i] ? x + widths[i] - 8 - value.length() * 5 : x + 8;
                double[] color = i == values.length - 1 && statusColor != null ? statusColor : TEXT;
                text(value, tx, y + 15, 8, i == values.length - 1 && statusColor != null, color);
                x += widths[i];
            }
            line(MARGIN, y + 22, PAGE_W - MARGIN, y + 22, new double[]{0.91, 0.91, 0.93}, .3);
            y += 22;
        }

        byte[] toBytes() {
            for (int i = 0; i < pages.size(); i++) {
                StringBuilder page = pages.get(i);
                page.append(String.format(java.util.Locale.US, "0.86 0.86 0.90 RG .5 w %d %d m %d %d l S\n",
                        MARGIN, PAGE_H - 808, PAGE_W - MARGIN, PAGE_H - 808));
                String footer = "Generated by StockSense";
                page.append(String.format(java.util.Locale.US, ".40 .42 .48 rg BT /F1 8 Tf 1 0 0 1 %d %d Tm (%s) Tj ET\n",
                        MARGIN, PAGE_H - 824, footer));
                String number = "Page " + (i + 1) + " of " + pages.size();
                page.append(String.format(java.util.Locale.US, ".40 .42 .48 rg BT /F1 8 Tf 1 0 0 1 %d %d Tm (%s) Tj ET\n",
                        PAGE_W - MARGIN - number.length() * 4, PAGE_H - 824, number));
            }
            return buildPdf(pages);
        }

        private static byte[] buildPdf(List<StringBuilder> pages) {
            List<byte[]> objects = new ArrayList<>();
            objects.add(bytes("<< /Type /Catalog /Pages 2 0 R >>"));
            StringBuilder kids = new StringBuilder();
            int pageCount = pages.size();
            for (int i = 0; i < pageCount; i++) kids.append(5 + i * 2).append(" 0 R ");
            objects.add(bytes("<< /Type /Pages /Kids [" + kids + "] /Count " + pageCount + " >>"));
            objects.add(bytes("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>"));
            objects.add(bytes("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>"));
            for (int i = 0; i < pageCount; i++) {
                int pageObject = 5 + i * 2;
                int contentObject = pageObject + 1;
                byte[] content = bytes(pages.get(i).toString());
                objects.add(bytes("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + PAGE_W + " " + PAGE_H + "] /Resources << /Font << /F1 3 0 R /F2 4 0 R >> >> /Contents " + contentObject + " 0 R >>"));
                objects.add(concat(bytes("<< /Length " + content.length + " >>\nstream\n"), content, bytes("\nendstream")));
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            write(out, bytes("%PDF-1.4\n%StockSense\n"));
            List<Integer> offsets = new ArrayList<>();
            offsets.add(0);
            for (int i = 0; i < objects.size(); i++) {
                offsets.add(out.size());
                write(out, bytes((i + 1) + " 0 obj\n")); write(out, objects.get(i)); write(out, bytes("\nendobj\n"));
            }
            int xref = out.size();
            write(out, bytes("xref\n0 " + (objects.size() + 1) + "\n0000000000 65535 f \n"));
            for (int i = 1; i < offsets.size(); i++) write(out, bytes(String.format(java.util.Locale.US, "%010d 00000 n \n", offsets.get(i))));
            write(out, bytes("trailer\n<< /Size " + (objects.size() + 1) + " /Root 1 0 R >>\nstartxref\n" + xref + "\n%%EOF"));
            return out.toByteArray();
        }

        private static byte[] bytes(String value) { return value.getBytes(StandardCharsets.ISO_8859_1); }
        private static byte[] concat(byte[]... chunks) { ByteArrayOutputStream out = new ByteArrayOutputStream(); for (byte[] chunk : chunks) write(out, chunk); return out.toByteArray(); }
        private static void write(ByteArrayOutputStream out, byte[] value) { out.write(value, 0, value.length); }
        private static String sanitize(String value) { return value == null ? "" : value.replace('\u2014', '\u0097').replace('\u2013', '-').replace('\u2022', '*').replaceAll("[^\\x20-\\xFF]", "?"); }
        private static String escape(String value) { return value.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)"); }
        private static String clip(String value, int max) { if (value == null) return ""; return value.length() <= max ? value : value.substring(0, Math.max(0, max - 1)) + "…"; }
    }
}
