package com.example.demo.controller;

import com.example.demo.entity.Client;
import com.example.demo.entity.Facture;
import com.example.demo.entity.LigneFacture;
import com.example.demo.service.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controlleur pour réaliser les exports.
 */
@Controller
@RequestMapping("/")
public class ExportController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private FactureService factureService;

   /* @Autowired
    private ExportService exportService;*/

    @GetMapping("/clients/csv")
    public void clientsCSV(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"clients.csv\"");
        PrintWriter writer = response.getWriter();
        List<Client> allClients = clientService.findAllClients();
        LocalDate now = LocalDate.now();
        writer.println("Id;Nom;Prenom;Date de Naissance;Age");
        //Créer une boucle d'itération affichant les différentes infos de la liste

        for (Client client : allClients) {

            writer.println(client.getId()
                    + " ; " +
                    client.getNom()
                    + " ; "
                    + client.getPrenom() +
                    " ; "
                    + client.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/YYYY")));
        }
    }


    @GetMapping("/clients/xlsx")
    public void clientsExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/vnd.ms-excel\n");
        response.setHeader("Content-Disposition", "attachment; filename=\"clients.xls\"");

        List<Client> allClients = clientService.findAllClients();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Clients");
        Row headerRow = sheet.createRow(0);

        Cell cellid = headerRow.createCell(0);
        cellid.setCellValue("Id");
        Cell cellPrenom = headerRow.createCell(1);
        cellPrenom.setCellValue("Prénom");
        Cell cellNom = headerRow.createCell((2));
        cellNom.setCellValue("Nom");
        Cell cellDateNaissance = headerRow.createCell((3));
        cellDateNaissance.setCellValue("Date de Naissance");

        //numéro de ligne
        int i = 1;
        for (Client client : allClients) {

            Row row = sheet.createRow(i);

            Cell id = row.createCell(0);
            id.setCellValue(client.getId());

            Cell prenom = row.createCell(1);
            prenom.setCellValue(client.getPrenom());

            Cell nom = row.createCell(2);
            nom.setCellValue(client.getNom());

            Cell dateNaissance = row.createCell(3);
            dateNaissance.setCellValue(client.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/YYYY")));

            i++;


        }
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/clients/{id}/factures/xlsx")
    public void factureXLSXByClient(@PathVariable("id") Long clientId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"factures-client-" + clientId + ".xlsx\"");
        List<Facture> factures = factureService.findAllFacturesClient(clientId);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Facture");
        Row headerRow = sheet.createRow(0);

        Cell cellId = headerRow.createCell(0);
        cellId.setCellValue("Id");

        Cell cellTotal = headerRow.createCell(1);
        cellTotal.setCellValue("Prix Total");

        int iRow = 1;
        for (Facture facture : factures) {
            Row row = sheet.createRow(iRow);

            Cell id = row.createCell(0);
            id.setCellValue(facture.getId());

            Cell prenom = row.createCell(1);
            prenom.setCellValue(facture.getTotal());

            iRow = iRow + 1;
        }
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/factures/xlsx")
    public void FacturesExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/vnd.ms-excel\n");
        response.setHeader("Content-Disposition", "attachment; filename=\"factures.xls\"");

        List<Client> allClients = clientService.findAllClients();
        Workbook workbook = new XSSFWorkbook();

        //numéro de ligne
        int iterationClients = 1;

        for (Client client : allClients) {

            Sheet sheet = workbook.createSheet(client.getNom());


            //Création des Header de la feuille Client
            Row headerRow = sheet.createRow(0);
            Cell cellid = headerRow.createCell(0);
            cellid.setCellValue("Id");
            Cell cellPrenom = headerRow.createCell(1);
            cellPrenom.setCellValue("Prénom");
            Cell cellNom = headerRow.createCell((2));
            cellNom.setCellValue("Nom");

            // Remplissage des cellules
            Row row = sheet.createRow(iterationClients);
            Cell id = row.createCell(0);
            id.setCellValue(client.getId());

            Cell prenom = row.createCell(1);
            prenom.setCellValue(client.getPrenom());

            Cell nom = row.createCell(2);
            nom.setCellValue(client.getNom());

            List<Facture> facturesDuClient = factureService.findAllFacturesClient(client.getId());

            for (Facture factureClient : facturesDuClient) {
                //Créer une feuille par factureClient avec comme nom Facture + idFacture
                Sheet sheetFactureClient = workbook.createSheet("Facture" + factureClient.getId());

                //Créer les headerRow de la feuille

                Row factureRow = sheetFactureClient.createRow(iterationClients);
                Cell cellLibelle = factureRow.createCell(0);
                cellLibelle.setCellValue("Nom de l'article");

                Cell cellQte = factureRow.createCell(1);
                cellQte.setCellValue("Quantité commandée");

                Cell cellPU = factureRow.createCell(2);
                cellPU.setCellValue("Prix unitaire");

                Cell cellPLigne = factureRow.createCell(3);
                cellPLigne.setCellValue("Prix de la ligne");

                //Récupérer les différents Lignes de factures de la factureClient

                Integer indexLigne = 2;

                for (LigneFacture ligneFacture : factureClient.getLigneFactures()) {
                    //Pour chaque article créer une ligne
                    Row ligneFactureRow = sheetFactureClient.createRow(indexLigne);

                    //Récupération des données
                    Cell cellArticleLibelle = ligneFactureRow.createCell(0);
                    cellArticleLibelle.setCellValue(ligneFacture.getArticle().getLibelle());

                    Cell cellQuantite = ligneFactureRow.createCell(1);
                    cellQuantite.setCellValue(ligneFacture.getQuantite());

                    Cell cellPrixUnitaire = ligneFactureRow.createCell(2);
                    cellPrixUnitaire.setCellValue(ligneFacture.getArticle().getPrix());

                    Cell cellSousTotal = ligneFactureRow.createCell(3);
                    cellSousTotal.setCellValue(ligneFacture.getSousTotal());

                    indexLigne++;
                }

                Row ligneTotal = sheetFactureClient.createRow(indexLigne);
                CellStyle cellStyle = workbook.createCellStyle();
                Font police = workbook.createFont();
                police.setColor(IndexedColors.RED.getIndex());
                police.setBold(true);
                cellStyle.setFont(police);
                cellStyle.setBottomBorderColor(IndexedColors.RED.getIndex());
                cellStyle.setTopBorderColor(IndexedColors.RED.getIndex());
                cellStyle.setRightBorderColor((IndexedColors.RED.getIndex()));
                cellStyle.setLeftBorderColor(IndexedColors.RED.getIndex());
                cellStyle.setBorderTop(BorderStyle.MEDIUM);
                cellStyle.setBorderBottom(BorderStyle.MEDIUM);
                cellStyle.setBorderRight(BorderStyle.MEDIUM);
                cellStyle.setBorderLeft(BorderStyle.MEDIUM);

                Cell cellLblTotal = ligneTotal.createCell(0);
                cellLblTotal.setCellValue("TOTAL DE LA FACTURE :");
                cellLblTotal.setCellStyle(cellStyle);


                Cell cellLigneTotal = ligneTotal.createCell(1);
                cellLigneTotal.setCellValue(factureClient.getTotal());
                cellLigneTotal.setCellStyle(cellStyle);

            }
        }

        workbook.write(response.getOutputStream());
        workbook.close();

    }

   /* @GetMapping("/facture/{id}/pdf")
    public void facturePdf(
            @PathVariable("id") Long idFacture,
            HttpServletResponse response) throws IOException
    {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"facture" + idFacture);
        exportService.exportPDF(response.getOutputStream());
    }*/

}
