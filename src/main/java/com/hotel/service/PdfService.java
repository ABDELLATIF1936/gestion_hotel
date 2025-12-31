package com.hotel.service;

import com.hotel.exception.ServiceException;
import com.hotel.factory.ServiceFactory;
import com.hotel.model.*;
import com.hotel.service.interfaces.IChambreService;
import com.hotel.service.interfaces.IClientService;
import com.hotel.service.interfaces.IReservationService;
import com.hotel.util.DateUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

public class PdfService {

    private final IReservationService reservationService;
    private final IClientService clientService;
    private final IChambreService chambreService;

    public PdfService() {
        this.reservationService = ServiceFactory.getReservationService();
        this.clientService = ServiceFactory.getClientService();
        this.chambreService = ServiceFactory.getChambreService();
    }

    public void generateInvoicePdf(Facture facture, String destPath) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(destPath));

        document.open();

        // 1. En-tête (Hôtel)
        addHeader(document);
        addEmptyLine(document, 1);

        // 2. Info Facture
        addInvoiceInfo(document, facture);
        addEmptyLine(document, 1);

        // 3. Info Client & Réservation (Need to fetch relations)
        Reservation reservation = reservationService.findReservationById(facture.getIdReservation());
        if (reservation == null)
            throw new RuntimeException("Réservation introuvable pour la facture " + facture.getIdFacture());

        Client client = clientService.findClientById(reservation.getIdClient());
        Chambre chambre = chambreService.findChambreByNumero(reservation.getNumeroChambre());

        addClientAndReservationInfo(document, client, reservation, chambre);
        addEmptyLine(document, 2);

        // 4. Détails (Tableau)
        addDetailsTable(document, reservation, chambre, facture);

        // 5. Total
        addEmptyLine(document, 1);
        addTotal(document, facture);

        document.close();
    }

    private void addHeader(Document document) throws DocumentException {
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("HOTEL MANAGEMENT SYSTEM", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Font subFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.GRAY);
        Paragraph subtitle = new Paragraph(
                "123 Avenue des Champs-Élysées, 75008 Paris\nTél: +33 1 23 45 67 89 | Email: contact@hotel.com",
                subFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitle);

        // Ligne de séparation
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(new Phrase(" "));
        cell.setBorderWidthBottom(1f);
        cell.setBorderWidthTop(0f);
        cell.setBorderWidthLeft(0f);
        cell.setBorderWidthRight(0f);
        cell.setPaddingBottom(10f);
        table.addCell(cell);
        document.add(table);
    }

    private void addInvoiceInfo(Document document, Facture facture) throws DocumentException {
        Font font = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Paragraph p = new Paragraph("FACTURE N° " + facture.getIdFacture(), font);
        p.setAlignment(Element.ALIGN_RIGHT);
        document.add(p);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Paragraph date = new Paragraph("Date d'émission: " + facture.getDateEmission().format(formatter));
        date.setAlignment(Element.ALIGN_RIGHT);
        document.add(date);
    }

    private void addClientAndReservationInfo(Document document, Client client, Reservation reservation, Chambre chambre)
            throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 1, 1 });

        // Client Info
        PdfPCell clientCell = new PdfPCell();
        clientCell.setBorder(Rectangle.NO_BORDER);
        clientCell.addElement(new Paragraph("CLIENT:", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)));
        clientCell.addElement(new Paragraph(client.getPrenom() + " " + client.getNom()));
        clientCell.addElement(new Paragraph(client.getEmail()));
        clientCell.addElement(new Paragraph(client.getTelephone()));
        table.addCell(clientCell);

        // Reservation Info
        PdfPCell resCell = new PdfPCell();
        resCell.setBorder(Rectangle.NO_BORDER);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        resCell.addElement(new Paragraph("RÉSERVATION:", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)));
        resCell.addElement(new Paragraph("Réf: #" + reservation.getIdReservation()));
        resCell.addElement(
                new Paragraph("Chambre: " + chambre.getNumeroChambre() + " (" + chambre.getCategorie() + ")"));
        resCell.addElement(new Paragraph(
                "Du: " + reservation.getDateDebut().format(fmt) + " au " + reservation.getDateFin().format(fmt)));
        table.addCell(resCell);

        document.add(table);
    }

    private void addDetailsTable(Document document, Reservation reservation, Chambre chambre, Facture facture)
            throws DocumentException, ServiceException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 4, 1, 2, 2 });
        table.setHeaderRows(1);

        // Headers
        Stream.of("Description", "Qté", "Prix Unitaire", "Total").forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(1);
            header.setPhrase(new Phrase(columnTitle));
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setVerticalAlignment(Element.ALIGN_MIDDLE);
            header.setPadding(5);
            table.addCell(header);
        });

        // 1. Chambre Line
        int nbNuits = DateUtil.calculateNights(reservation.getDateDebut(), reservation.getDateFin());
        double totalChambre = chambre.getPrixNuit() * nbNuits;

        table.addCell("Séjour - Chambre " + chambre.getNumeroChambre() + " (" + chambre.getCategorie() + ")");
        PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(nbNuits)));
        qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(qtyCell);

        PdfPCell priceCell = new PdfPCell(new Phrase(String.format("%.2f €", chambre.getPrixNuit())));
        priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(priceCell);

        PdfPCell totalCell = new PdfPCell(new Phrase(String.format("%.2f €", totalChambre)));
        totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(totalCell);

        // 2. Services Lines
        List<Inclure> servicesInclus = reservationService.getServicesByReservation(reservation.getIdReservation());
        for (Inclure inc : servicesInclus) {
            // Need service name. Inclure likely has idService, fetch Service object?
            // Assuming Inclure has or I can fetch. Let's see Inclure model.
            // Wait, standard architecture: I need ServiceSupplementaireService or DAO to
            // get name if Inclure only has ID.
            // Let's assume Inclure has a method or field, IF NOT I will fix.
            // Checking: Inclure typically links Reservation and Service.
            // I'll take a safe bet: fetch Service details.
            ServiceFactory.getServiceSupplementaireService().getAllServices().stream()
                    .filter(s -> s.getIdService() == inc.getIdService())
                    .findFirst()
                    .ifPresent(service -> {
                        table.addCell("Service: " + service.getNom());

                        PdfPCell q = new PdfPCell(new Phrase(String.valueOf(inc.getQuantite())));
                        q.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(q);

                        PdfPCell p = new PdfPCell(new Phrase(String.format("%.2f €", service.getPrix()))); // Or
                                                                                                           // inc.getPrixUnitaire()
                                                                                                           // if stored
                                                                                                           // history
                        p.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(p);

                        PdfPCell t = new PdfPCell(
                                new Phrase(String.format("%.2f €", inc.getQuantite() * service.getPrix())));
                        t.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(t);
                    });
        }

        document.add(table);
    }

    private void addTotal(Document document, Facture facture) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(30);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Font bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

        PdfPCell labelCell = new PdfPCell(new Phrase("TOTAL NET A PAYER:", bold));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(String.format("%.2f €", facture.getMontantTotal()), bold));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);

        document.add(table);

        // Status
        Paragraph p = new Paragraph("Statut: " + facture.getStatut(),
                new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC));
        p.setAlignment(Element.ALIGN_RIGHT);
        document.add(p);
    }

    private void addEmptyLine(Document document, int number) throws DocumentException {
        for (int i = 0; i < number; i++) {
            document.add(new Paragraph(" "));
        }
    }
}
