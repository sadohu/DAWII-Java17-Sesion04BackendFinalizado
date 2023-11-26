package com.empresa.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.empresa.entity.Docente;
import com.empresa.service.DocenteService;

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

    @GetMapping("/reporteDocentePdf")
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
            response.addHeader("Content-Disposition", "inline; filename=ReporteDocentes.pdf");

            // Paso 6: Envio del reporte
            OutputStream outputStream = response.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
