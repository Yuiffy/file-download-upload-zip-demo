package com.dyf.i18n.file;

import com.dyf.i18n.util.BOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuiff on 2017/2/9.
 */
public class XmlFileHandler implements KeyValueFileHandler {
    private Map<String, Integer> keyItemIdMap;
    private Document doc;
    private NodeList stringList;

    //<string name="xxxname">XXXXXXXXXXXX</string>
    public XmlFileHandler(File file) throws ParserConfigurationException, IOException, SAXException {
        this(new FileInputStream(file));
    }

    public XmlFileHandler(String xmlString) throws ParserConfigurationException, IOException, SAXException {
        this(new ByteArrayInputStream(BOMUtils.removeUTF8BOM(xmlString).getBytes("UTF-8")));
    }


    public XmlFileHandler(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        doc = db.parse(inputStream);
        stringList = doc.getElementsByTagName("string");
//        System.out.println("共有" + stringList.getLength() + "个string节点");
        keyItemIdMap = new HashMap<>();
        for (int i = 0; i < stringList.getLength(); i++) {
//        	System.out.println(new Integer(i) + "!");
            Node stringNode = stringList.item(i);
            //TODO:can add parentNode information to recognize 2 node in different place have same name.
            // System.out.println(stringNode.getParentNode());
            String name = ((Element) stringNode).getAttribute("name");
//            String value = stringNode.getFirstChild().getNodeValue();
            keyItemIdMap.put(name, i);
//            System.out.println("name:" + name + "\tvalue:" + value);
        }

    }

    @Override
    public List<String> getKeyList() {
        List<String> ret = new ArrayList<>(stringList.getLength());
        for (int i = 0; i < stringList.getLength(); i++) {
            Node stringNode = stringList.item(i);
            String name = ((Element) stringNode).getAttribute("name");
            ret.add(name);
        }
        return ret;
    }

    @Override
    public Map<String, String> getKeyValueMap() {
        Map<String, String> ret = new HashMap<>(stringList.getLength());
        for (int i = 0; i < stringList.getLength(); i++) {
            Node stringNode = stringList.item(i);
            String name = ((Element) stringNode).getAttribute("name");
            String value = stringNode.getFirstChild().getNodeValue();
            ret.put(name, value);
        }
        return ret;
    }

    @Override
    public void put(String key, String value) {
        int pos = keyItemIdMap.get(key);
        Node stringNode = stringList.item(pos);
        stringNode.getFirstChild().setNodeValue(value);
    }

    @Override
    public String getString() {
        String result = null;
        Document document = doc;
        if (document != null) {
            StringWriter strWtr = new StringWriter();
            StreamResult strResult = new StreamResult(strWtr);
            TransformerFactory tfac = TransformerFactory.newInstance();
            try {
                javax.xml.transform.Transformer t = tfac.newTransformer();
                t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                t.setOutputProperty(OutputKeys.INDENT, "yes");
                t.setOutputProperty(OutputKeys.METHOD, "xml"); // xml, html,
                // text
                t.setOutputProperty(
                        "{http://xml.apache.org/xslt}indent-amount", "4");
                t.transform(new DOMSource(document.getDocumentElement()),
                        strResult);
            } catch (Exception e) {
                System.err.println("XML.toString(Document): " + e);
            }
            result = strResult.getWriter().toString();
            try {
                strWtr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
