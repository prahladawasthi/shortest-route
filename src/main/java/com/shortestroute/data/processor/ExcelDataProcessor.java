package com.shortestroute.data.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shortestroute.dto.EdgeModel;
import com.shortestroute.dto.TrafficInfoModel;
import com.shortestroute.model.Vertex;

@Service
public class ExcelDataProcessor {
    private File file;
    private FileInputStream inputStream;
    private XSSFWorkbook workbook;
    private Logger logger = Logger.getLogger(ExcelDataProcessor.class.getName());

    @Autowired
    public ExcelDataProcessor(File file) {
        this.file = file;
    }

    public Map<String, Vertex> readVertexes() {
        Map<String, Vertex> vertexMap = new LinkedHashMap<>();
        try {
            inputStream = new FileInputStream(this.file);
            workbook = new XSSFWorkbook(inputStream);
            XSSFSheet firstSheet = workbook.getSheetAt(0);

            for (Row nextRow : firstSheet) {
                // skip header
                if (nextRow.getRowNum() == 0) {
                    continue;
                }
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                Vertex vertex = new Vertex();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    int columnIndex = cell.getColumnIndex();
                    switch (columnIndex + 1) {
                        case 1:
                            vertex.setId((String) getCellValue(cell));
                            break;
                        case 2:
                            vertex.setName((String) getCellValue(cell));
                            break;
                        default:
                    }
                }
                vertexMap.put(vertex.getId(), vertex);
            }

            workbook.close();
            inputStream.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "An Exception occurred while reading vertices data: " + ex);
            System.exit(1);
        }
        return vertexMap;
    }

    public List<EdgeModel> readEdges() {
        List<EdgeModel> edges = new ArrayList<>();
        try {
            inputStream = new FileInputStream(this.file);
            workbook = new XSSFWorkbook(inputStream);
            XSSFSheet secondSheet = workbook.getSheetAt(1);

            for (Row nextRow : secondSheet) {
                // skip header
                if (nextRow.getRowNum() == 0) {
                    continue;
                }
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                EdgeModel edge = new EdgeModel();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    int columnIndex = cell.getColumnIndex();
                    switch (columnIndex + 1) {
                        case 1:
                            edge.setId(String.valueOf((int) cell.getNumericCellValue()));
                            break;
                        case 2:
                            edge.setSource(cell.getStringCellValue());
                            break;
                        case 3:
                            edge.setDestination(cell.getStringCellValue());
                            break;
                        case 4:
                            edge.setWeight((float) cell.getNumericCellValue());
                            break;
                        default:
                    }
                }

                edges.add(edge);
            }

            workbook.close();
            inputStream.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "An Exception occurred while reading edges data: " + ex);
            System.exit(1);
        }
        return edges;
    }

    public List<TrafficInfoModel> readTraffics() {
        List<TrafficInfoModel> traffics = new ArrayList<>();
        try {
            inputStream = new FileInputStream(this.file);

            workbook = new XSSFWorkbook(inputStream);
            XSSFSheet thirdSheet = workbook.getSheetAt(2);

            for (Row nextRow : thirdSheet) {
                // skip header
                if (nextRow.getRowNum() == 0) {
                    continue;
                }
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                TrafficInfoModel traffic = new TrafficInfoModel();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    int columnIndex = cell.getColumnIndex();
                    switch (columnIndex + 1) {
                        case 1:
                            traffic.setId(String.valueOf((int) cell.getNumericCellValue()));
                            break;
                        case 2:
                            traffic.setSource((String) getCellValue(cell));
                            break;
                        case 3:
                            traffic.setDestination((String) getCellValue(cell));
                            break;
                        case 4:
                            traffic.setWeight((float) cell.getNumericCellValue());
                            break;
                        default:
                    }
                }
                traffics.add(traffic);
            }

            workbook.close();
            inputStream.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "An Exception occurred while reading traffics data: " + ex);
            System.exit(1);
        }

        return traffics;
    }

    private Object getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();

            case BOOLEAN:
                return cell.getBooleanCellValue();

            case NUMERIC:
                return cell.getNumericCellValue();
            default:
                return null;
        }
    }
}
