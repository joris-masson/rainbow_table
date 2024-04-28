package util.xml;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Util class for writing XML files which contains informations about tests data.
 *
 * @author Joris MASSON
 */
public abstract class ReductionCollisionTestDataXMLWriter {
    /**
     * Write to XML data from a collision test.
     *
     * @param data     Data to write.
     * @param filename Output filename.
     */
    public static void writeCollisionTestData(Map<Integer, Map<String, Double>> data, String filename) {
        try {
            OutputStream outputStream = new FileOutputStream(filename);
            XMLStreamWriter out = XMLOutputFactory.newInstance().createXMLStreamWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            out.writeStartDocument();
            out.writeStartElement("collisionTestData");
            for (Map.Entry<Integer, Map<String, Double>> testEntry : data.entrySet()) {
                out.writeStartElement("test");
                out.writeAttribute("passwordSize", testEntry.getKey().toString());
                for (Map.Entry<String, Double> algoEntry : testEntry.getValue().entrySet()) {
                    out.writeStartElement("data");
                    out.writeAttribute("algorithm", algoEntry.getKey());
                    out.writeCharacters(algoEntry.getValue().toString());
                    out.writeEndElement();
                }
                out.writeEndElement();
            }
            out.writeEndElement();
            out.writeEndDocument();
            out.close();
        } catch (FileNotFoundException | XMLStreamException e) {
            System.err.printf("Problème: %s", e.getMessage());
        }
    }

    /**
     * Write to XML execution time from a collision test.
     *
     * @param data     Data to write.
     * @param filename Output filename.
     */
    public static void writeCollisionTestExecTime(Map<Integer, Map<String, Long>> data, String filename) {
        try {
            OutputStream outputStream = new FileOutputStream(filename);
            XMLStreamWriter out = XMLOutputFactory.newInstance().createXMLStreamWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            out.writeStartDocument();
            out.writeStartElement("collisionTestExecutionTime");
            for (Map.Entry<Integer, Map<String, Long>> testEntry : data.entrySet()) {
                out.writeStartElement("test");
                out.writeAttribute("passwordSize", testEntry.getKey().toString());
                for (Map.Entry<String, Long> algoEntry : testEntry.getValue().entrySet()) {
                    out.writeStartElement("data");
                    out.writeAttribute("algorithm", algoEntry.getKey());
                    out.writeCharacters(algoEntry.getValue().toString());
                    out.writeEndElement();
                }
                out.writeEndElement();
            }
            out.writeEndElement();
            out.writeEndDocument();
            out.close();
        } catch (FileNotFoundException | XMLStreamException e) {
            System.err.printf("Problème: %s", e.getMessage());
        }
    }
}
