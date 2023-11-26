package com.empresa.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.empresa.entity.Docente;
import com.empresa.service.DocenteService;
import com.empresa.util.UtilExcel;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.apachecommons.CommonsLog;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

@RestController
@RequestMapping("/url/consultaDocente")
@CrossOrigin(origins = "http://localhost:4200")
@CommonsLog
public class DocenteConsultaController {
    @Autowired
    private DocenteService docenteService;

    @ResponseBody
    @GetMapping("/consultaDocentePorParametros")
    public List<Docente> listaConsultaDocente(
            @RequestParam(name = "nombre", required = false, defaultValue = "") String nombre,
            @RequestParam(name = "dni", required = false, defaultValue = "") String dni,
            @RequestParam(name = "estado", required = false, defaultValue = "1") int estado,
            @RequestParam(name = "idUbigeo", required = false, defaultValue = "-1") int idUbigeo) {
        List<Docente> list = docenteService.listaConsulta("%" + nombre + "%", dni, estado, idUbigeo);
        return list;
    }

    @PostMapping("/reporteDocentePdf")
    public void exportPdf(
            @RequestParam(name = "nombre", required = false, defaultValue = "") String nombre,
            @RequestParam(name = "dni", required = false, defaultValue = "") String dni,
            @RequestParam(name = "estado", required = false, defaultValue = "1") int estado,
            @RequestParam(name = "idUbigeo", required = false, defaultValue = "-1") int idUbigeo,
            HttpServletRequest request,
            HttpServletResponse response) {

        try {

            // Paso 1: Fuente de datos
            List<Docente> list = docenteService.listaConsulta("%" + nombre + "%", dni, estado, idUbigeo);
            JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(list);

            // Paso 2: Diseño del reporte
            String fileRoute = request.getServletContext().getRealPath("/WEB-INF/reportes/ReporteDocente.jasper");
            log.info("Ruta del reporte: " + fileRoute);

            String fileImgRoute = request.getServletContext().getRealPath("/WEB-INF/img/logo.png");
            log.info("Ruta del reporte: " + fileImgRoute);

            // Paso 3: Parámetros del reporte
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("RUTA_LOGO", fileImgRoute);

            // Paso 4: Juntar la fuente de datos con el diseño del reporte
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new FileInputStream(new File(fileRoute)));
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, source);

            // Paso 5: Parametros para el Header del mensaje HTTP
            response.setContentType("application/x-pdf");
            // PostMan-Test: response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=ReporteDocentes.pdf");

            // Paso 6: Envio del reporte
            OutputStream outputStream = response.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private static String[] HEADERS = { "ID", "NOMBRE", "DNI", "ESTADO", "UBIGEO", "FECHA REGISTRO" };
    private static String SHEET = "Docentes";
    private static String TITLE = "REPORTE DE DOCENTES";
    private static int[] HEADER_WIDTHS = { 3000, 10000, 6000, 10000, 20000, 10000 };

    @PostMapping("/reporteDocenteExcel")
    public void exportExcel(
            @RequestParam(name = "nombre", required = false, defaultValue = "") String nombre,
            @RequestParam(name = "dni", required = false, defaultValue = "") String dni,
            @RequestParam(name = "estado", required = false, defaultValue = "1") int estado,
            @RequestParam(name = "idUbigeo", required = false, defaultValue = "-1") int idUbigeo,
            HttpServletRequest request,
            HttpServletResponse response) {

        Workbook excel = null;
        try {
            excel = new XSSFWorkbook();
            Sheet sheet = excel.createSheet(SHEET);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, HEADER_WIDTHS.length - 1));

            for (int i = 0; i < HEADER_WIDTHS.length; i++) {
                // sheet.setColumnWidth(i, HEADER_WIDTHS[i] * 256);
                sheet.setColumnWidth(i, HEADER_WIDTHS[i]);
            }

            CellStyle estiloHeadCentrado = UtilExcel.setEstiloHeadCentrado(excel);
            CellStyle estiloHeadIzquierda = UtilExcel.setEstiloHeadIzquierda(excel);
            CellStyle estiloNormalCentrado = UtilExcel.setEstiloNormalCentrado(excel);
            CellStyle estiloNormalIzquierda = UtilExcel.setEstiloNormalIzquierdo(excel);

            // Row 0
            Row row0 = sheet.createRow(0);
            Cell celAuxs = row0.createCell(0);
            celAuxs.setCellStyle(estiloHeadIzquierda);
            celAuxs.setCellValue(TITLE);

            // Row 1
            Row row1 = sheet.createRow(1);
            Cell celAuxs1 = row1.createCell(0);
            celAuxs1.setCellValue("");

            // Row 2
            Row row2 = sheet.createRow(2);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell celda1 = row2.createCell(i);
                celda1.setCellStyle(estiloHeadCentrado);
                celda1.setCellValue(HEADERS[i]);
            }

            List<Docente> list = docenteService.listaConsulta("%" + nombre + "%", dni, estado, idUbigeo);
            // Fila 3....n
            int rowIdx = 3;
            for (Docente obj : list) {
                Row row = sheet.createRow(rowIdx++);

                Cell cel0 = row.createCell(0);
                cel0.setCellValue(obj.getIdDocente());
                cel0.setCellStyle(estiloNormalCentrado);

                Cell cel1 = row.createCell(1);
                cel1.setCellValue(obj.getNombre());
                cel1.setCellStyle(estiloNormalIzquierda);

                Cell cel2 = row.createCell(2);
                cel2.setCellValue(obj.getDni());
                cel2.setCellStyle(estiloNormalCentrado);

                Cell cel3 = row.createCell(3);
                cel3.setCellValue(obj.getReporteEstado());
                cel3.setCellStyle(estiloNormalCentrado);

                Cell cel4 = row.createCell(4);
                cel4.setCellValue(obj.getReporteUbigeo());
                cel4.setCellStyle(estiloNormalIzquierda);

                Cell cel5 = row.createCell(5);
                cel5.setCellValue(obj.getReporteFechaRegistro());
                cel5.setCellStyle(estiloNormalCentrado);
            }

            response.setContentType("application/vnd.ms-excel");
            response.addHeader("Content-disposition", "attachment; filename=ReporteDocentes.xlsx");

            OutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);
            outputStream.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (excel != null)
                    excel.close();
            } catch (Exception e2) {
                System.out.println(e2.getMessage());
            }
        }
    }
}
