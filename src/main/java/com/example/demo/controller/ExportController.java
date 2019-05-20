package com.example.demo.controller;

import com.example.demo.entity.Client;
import com.example.demo.service.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
                    +  " ; " +
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
}
