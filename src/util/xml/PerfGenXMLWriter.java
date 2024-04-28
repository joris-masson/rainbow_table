package util.xml;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * Util class for writing XML files which contains informations about tests data.
 *
 * @author Joris MASSON
 */
public abstract class PerfGenXMLWriter {
    /**
     * Write to XML execution time from a collision test.
     *
     * @param filename Output filename.
     */
    public static void writeExecTime(Long time, int tailleMdp, int nbReductions, String fctHashage, String filename) {
        try {
            OutputStream outputStream = new FileOutputStream(filename);
            XMLStreamWriter out = XMLOutputFactory.newInstance().createXMLStreamWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            out.writeStartDocument();
            out.writeStartElement("ExecutionTime");
            out.writeAttribute("Time", String.valueOf(time));
            out.writeStartElement("TailleMotDePasse");
            out.writeAttribute("tailleMDP", String.valueOf(tailleMdp));
            out.writeStartElement("NombreDeReductions");
            out.writeAttribute("nbReductions", String.valueOf(nbReductions));
            out.writeStartElement("FonctionDeHashage");
            out.writeAttribute("fctHash", fctHashage);
            out.writeEndElement();
            out.writeEndElement();
            out.writeEndElement();
            out.writeEndElement();
            out.writeEndDocument();
            out.close();
        } catch (FileNotFoundException | XMLStreamException e) {
            System.err.printf("Problème: %s", e.getMessage());
        }
    }
}
